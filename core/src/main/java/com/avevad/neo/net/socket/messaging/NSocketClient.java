package com.avevad.neo.net.socket.messaging;

import com.avevad.neo.logging.NLogDestination;
import com.avevad.neo.logging.NLogMessage;
import com.avevad.neo.logging.NLogger;
import com.avevad.neo.util.NTaskQueue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public final class NSocketClient {
    private final Object lock = new Object();

    private final String address;
    private final int port;
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    private final NLogger logger;
    private final NTaskQueue taskQueue;

    private NDisconnectedHandler disconnectedHandler;

    private final Map<Class<? extends NSocketMessage>, NSocketMessageHandler> messageHandlers = new HashMap<>();
    private final Map<Class<? extends NSocketCommand>, NSocketCommandHandler> commandHandlers = new HashMap<>();

    private final Map<Long, RuntimeException> exceptions = new HashMap<>();
    private final Map<Long, Serializable> responses = new HashMap<>();

    private final Thread listener;
    private boolean connected = true;

    public boolean autoOutputReset = false;
    public boolean autoInputReset = false;

    public NSocketClient(String address, int port, NLogDestination logDestination) throws IOException {
        this.address = address;
        this.port = port;
        socket = new Socket(address, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        logger = new NLogger(toString(), logDestination);
        taskQueue = new NTaskQueue(toString() + ":taskQueue", logDestination);
        listener = new Thread(this::listen, toString() + ":listener");
        listener.start();
        taskQueue.start();
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        if (!connected) throw new IllegalStateException("already disconnected");
        try {
            socket.close();
            listener.stop();
            taskQueue.join(taskQueue::destroy);
        } catch (IOException e) {
            throw new IllegalStateException("already disconnected");
        }
    }

    private void sendObject(Serializable o) throws IOException {
        out.writeObject(o);
        if (autoOutputReset) out.reset();
    }

    public void resetOutputObjectBuffer() throws IOException {
        out.reset();
    }

    public void setAutoOutputObjectBufferReset(boolean autoReset) {
        autoOutputReset = autoReset;
    }

    public void resetInputObjectBuffer() throws IOException {
        in.reset();
    }

    public void setAutoInputObjectBufferReset(boolean autoReset) {
        autoInputReset = autoReset;
    }

    public void send(NSocketMessage message) {
        if (!connected) throw new IllegalStateException("disconnected");
        try {
            sendObject(new NMessagePacket(message));
        } catch (IOException ex) {
            logger.log(NLogMessage.NSeverity.WARNING, "Server has disconnected while sending message");
            logger.log(NLogMessage.NSeverity.WARNING, ex);
            if (!connected) throw new IllegalStateException("disconnected");
        }
    }

    public <R extends Serializable> R invoke(NSocketCommand<R> command) {
        if (!connected) throw new IllegalStateException("disconnected");
        NCommandPacket commandPacket = new NCommandPacket(command);
        try {
            sendObject(commandPacket);
        } catch (IOException e) {
            logger.log(NLogMessage.NSeverity.WARNING, "Server has disconnected while sending command");
            logger.log(NLogMessage.NSeverity.WARNING, e);
            if (!connected) throw new IllegalStateException("disconnected");
        }
        synchronized (lock) {
            while (responses.get(commandPacket.id) == null && exceptions.get(commandPacket.id) == null && connected) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    logger.log(NLogMessage.NSeverity.DEBUG, "Interrupted while waiting for server response");
                }
            }
        }
        Serializable response = responses.get(commandPacket.id);
        RuntimeException exception = exceptions.get(commandPacket.id);
        if (exception != null) {
            exceptions.remove(commandPacket.id);
            throw exception;
        } else if (response != null) {
            responses.remove(commandPacket.id);
            return (R) response;
        } else {
            logger.log(NLogMessage.NSeverity.WARNING, "Server has disconnected while receiving response");
            throw new IllegalStateException("disconnected");
        }
    }

    public <M extends NSocketMessage> void setMessageHandler(Class<M> messageClass, NSocketMessageHandler<M> handler) {
        if (messageClass == null) throw new IllegalArgumentException("invalid message class " + messageClass);
        if (handler == null) messageHandlers.remove(messageClass);
        else messageHandlers.put(messageClass, handler);
    }

    public <C extends NSocketCommand<R>, R extends Serializable> void setCommandHandler(Class<C> commandClass, NSocketCommandHandler<C, R> handler) {
        if (commandClass == null) throw new IllegalArgumentException("invalid command class " + commandClass);
        if (handler == null) commandHandlers.remove(commandClass);
        else commandHandlers.put(commandClass, handler);
    }

    public void setDisconnectedHandler(NDisconnectedHandler disconnectedHandler) {
        this.disconnectedHandler = disconnectedHandler;
    }

    private void listen() {
        String label = "listener";
        try {
            while (true) {
                try {
                    Object o = in.readObject();
                    if (o == null) throw new IOException("null object received");
                    if (autoInputReset) in.reset();
                    if (o instanceof NMessagePacket) {
                        NMessagePacket messagePacket = (NMessagePacket) o;
                        Class<? extends NSocketMessage> messageClass = messagePacket.message.getClass();
                        if (messageHandlers.containsKey(messageClass)) {
                            taskQueue.join(() -> messageHandlers.get(messageClass).handleMessage(messagePacket.message));
                        } else
                            logger.log(NLogMessage.NSeverity.WARNING, label, "No handler for message class " + messageClass);
                    } else if (o instanceof NCommandPacket) {
                        NCommandPacket commandPacket = (NCommandPacket) o;
                        Class<? extends NSocketCommand> commandClass = commandPacket.command.getClass();
                        if (commandHandlers.containsKey(commandClass)) {
                            taskQueue.join(() -> {
                                try {
                                    Serializable response = commandHandlers.get(commandClass).handleCommand(commandPacket.command);
                                    try {
                                        sendObject(new NResponsePacket(commandPacket.id, response));
                                    } catch (IOException ioex) {
                                        logger.log(NLogMessage.NSeverity.DEBUG, label, "IOException while sending response to server");
                                        logger.log(NLogMessage.NSeverity.DEBUG, label, ioex);
                                    }
                                } catch (RuntimeException ex) {
                                    logger.log(NLogMessage.NSeverity.ERROR, label, "Exception in command handler for class " + commandClass + ":");
                                    logger.log(NLogMessage.NSeverity.ERROR, label, ex);
                                    try {
                                        sendObject(new NExceptionPacket(commandPacket.id, ex));
                                    } catch (IOException ioex) {
                                        logger.log(NLogMessage.NSeverity.DEBUG, label, "IOException while sending exception to server");
                                        logger.log(NLogMessage.NSeverity.DEBUG, label, ioex);
                                    }
                                }
                            });
                        } else {
                            logger.log(NLogMessage.NSeverity.ERROR, label, "No handler for command class " + commandClass);
                            try {
                                sendObject(new NExceptionPacket(commandPacket.id, new NNoSuitableSocketCommandHandlerException()));
                            } catch (IOException ioex) {
                                logger.log(NLogMessage.NSeverity.DEBUG, label, "IOException while sending exception to server");
                                logger.log(NLogMessage.NSeverity.DEBUG, label, ioex);
                            }
                        }
                    } else if (o instanceof NResponsePacket) {
                        NResponsePacket responsePacket = (NResponsePacket) o;
                        responses.put(responsePacket.id, responsePacket.response);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    } else if (o instanceof NExceptionPacket) {
                        NExceptionPacket exceptionPacket = (NExceptionPacket) o;
                        exceptions.put(exceptionPacket.id, exceptionPacket.ex);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    } else {
                        logger.log(NLogMessage.NSeverity.ERROR, "Server has sent the object of an unknown class " + o.getClass());
                    }
                } catch (ClassNotFoundException e) {
                    logger.log(NLogMessage.NSeverity.ERROR, label, "Server has sent the object of an unknown class!");
                    logger.log(NLogMessage.NSeverity.ERROR, label, e);
                }
            }
        } catch (IOException e) {
            logger.log(NLogMessage.NSeverity.DEBUG, label, "Lost connection with server:");
            logger.log(NLogMessage.NSeverity.DEBUG, label, e);
            connected = false;
            synchronized (lock) {
                lock.notifyAll();
            }
            if (disconnectedHandler != null) taskQueue.join(disconnectedHandler::onDisconnected);
            taskQueue.join(taskQueue::destroy);
        }
    }


    @Override
    public String toString() {
        return "NSocketClient(" + address + ":" + port + ")";
    }

    public interface NDisconnectedHandler {
        void onDisconnected();
    }

    public interface NSocketMessageHandler<M extends NSocketMessage> {
        void handleMessage(M message);
    }

    public interface NSocketCommandHandler<C extends NSocketCommand<R>, R extends Serializable> {
        R handleCommand(C command);
    }
}
