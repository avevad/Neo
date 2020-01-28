package com.avevad.neo.net.socket.messaging;

import com.avevad.neo.logging.NLogDestination;
import com.avevad.neo.logging.NLogMessage;
import com.avevad.neo.logging.NLogger;
import com.avevad.neo.util.NTaskQueue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class NSocketServer {
    private final Object lock = new Object();

    private final int port;
    private final NLogger logger;
    private final NTaskQueue taskQueue;
    private ServerSocket socket;

    private NClientConnectedHandler clientConnectedHandler;
    private NClientDisconnectedHandler clientDisconnectedHandler;

    private final Map<Class<? extends NSocketMessage>, NSocketMessageHandler> messageHandlers = new HashMap<>();
    private final Map<Class<? extends NSocketCommand>, NSocketCommandHandler> commandHandlers = new HashMap<>();

    private final Map<Long, RuntimeException> exceptions = new HashMap<>();
    private final Map<Long, Serializable> responses = new HashMap<>();

    private final Map<Integer, Client> clients = new HashMap<>();

    private boolean destroyed = false;

    public NSocketServer(int port, NLogDestination destination) {
        this.port = port;
        logger = new NLogger(toString(), destination);
        taskQueue = new NTaskQueue(toString() + ":taskQueue");
        taskQueue.start();
    }

    public void open() throws IOException {
        if (destroyed) throw new IllegalStateException("destroyed");
        if (socket != null) throw new IllegalStateException("already open");
        socket = new ServerSocket(port);
        new Thread(() -> {
            while (socket != null && !socket.isClosed()) {
                try {
                    Socket clientSocket = socket.accept();
                    int i = 1;
                    for (; clients.containsKey(i); i++) ;
                    int finalI = i;
                    Thread listener = new Thread(() -> listen(finalI), toString() + ":listener(" + i + ")");
                    clients.put(i, new Client(i, clientSocket, listener));
                    listener.start();
                    if (clientConnectedHandler != null)
                        taskQueue.join(() -> clientConnectedHandler.onClientConnected(finalI));
                } catch (IOException e) {
                    logger.log(NLogMessage.NSeverity.DEBUG, "connector", "Failed to connect client:");
                    logger.log(NLogMessage.NSeverity.DEBUG, "connector", e);
                }
            }
        }, toString() + ":connector").start();
    }

    public void close() throws IOException {
        if (socket == null) throw new IllegalStateException("already closed");
        socket.close();
        socket = null;
    }

    public void destroy() {
        if (socket != null) throw new IllegalStateException("server is open");
        if (clients.size() != 0) throw new IllegalStateException("server have clients connected");
        destroyed = true;
        taskQueue.join(taskQueue::destroy);
    }

    public void disconnect(int id) throws IOException {
        Client client = clients.get(id);
        if (client == null) throw new IllegalArgumentException("no such client");
        client.listener.stop();
        clients.remove(id);
        client.socket.close();
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void setClientConnectedHandler(NClientConnectedHandler clientConnectedHandler) {
        this.clientConnectedHandler = clientConnectedHandler;
    }

    public void setClientDisconnectedHandler(NClientDisconnectedHandler clientDisconnectedHandler) {
        this.clientDisconnectedHandler = clientDisconnectedHandler;
    }

    public void resetOutputObjectBuffer(int id) throws IOException {
        Client client = clients.get(id);
        if (client == null) throw new IllegalArgumentException("no such client");
        client.out.reset();
    }

    public void setAutoOutputObjectBufferReset(int id, boolean autoReset) {
        Client client = clients.get(id);
        if (client == null) throw new IllegalArgumentException("no such client");
        client.autoOutputReset = autoReset;
    }

    public void resetInputObjectBuffer(int id) throws IOException {
        Client client = clients.get(id);
        if (client == null) throw new IllegalArgumentException("no such client");
        client.in.reset();
    }

    public void setAutoInputObjectBufferReset(int id, boolean autoReset) {
        Client client = clients.get(id);
        if (client == null) throw new IllegalArgumentException("no such client");
        client.autoInputReset = autoReset;
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

    public void send(NSocketMessage message, int to) {
        Client client = clients.get(to);
        if (client == null) throw new IllegalArgumentException("no such client");
        try {
            client.send(new NMessagePacket(message));
        } catch (IOException ex) {
            logger.log(NLogMessage.NSeverity.WARNING, "Client #" + to + " has disconnected while sending message");
            logger.log(NLogMessage.NSeverity.WARNING, ex);
            throw new IllegalArgumentException("no such client", ex);
        }
    }

    public void send(NSocketMessage message) {
        for (Client client : clients.values()) {
            try {
                client.send(new NMessagePacket(message));
            } catch (IOException e) {
                logger.log(NLogMessage.NSeverity.WARNING, "Client #" + client.id + " has disconnected while sending message to all");
                logger.log(NLogMessage.NSeverity.WARNING, e);
            }
        }
    }

    public void send(NSocketMessage message, Iterable<Integer> to) {
        for (int id : to) {
            if (!clients.containsKey(id)) continue;
            Client client = clients.get(id);
            try {
                client.send(new NMessagePacket(message));
            } catch (IOException ex) {
                logger.log(NLogMessage.NSeverity.WARNING, "Client #" + id + " has disconnected while sending group message");
                logger.log(NLogMessage.NSeverity.WARNING, ex);
            }
        }
    }

    public <R extends Serializable> R invoke(NSocketCommand<R> command, int to) {
        Client client = clients.get(to);
        if (client == null) throw new IllegalArgumentException("no such client");
        NCommandPacket commandPacket = new NCommandPacket(command);
        try {
            client.send(commandPacket);
        } catch (IOException e) {
            logger.log(NLogMessage.NSeverity.WARNING, "Client #" + to + " has disconnected while sending command");
            logger.log(NLogMessage.NSeverity.WARNING, e);
            throw new IllegalArgumentException("no such client", e);
        }
        synchronized (lock) {
            while (responses.get(commandPacket.id) == null && exceptions.get(commandPacket.id) == null && clients.get(to) != null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    logger.log(NLogMessage.NSeverity.DEBUG, "Interrupted while waiting for client response");
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
            logger.log(NLogMessage.NSeverity.WARNING, "Client #" + to + " has disconnected while receiving response");
            throw new IllegalArgumentException("no such client");
        }
    }


    private void listen(int id) {
        String label = "listener(" + id + ")";
        Client client = clients.get(id);
        try {
            while (true) {
                try {
                    Object o = client.in.readObject();
                    if (o == null) throw new IOException("null object received");
                    if (client.autoInputReset) client.in.reset();
                    if (o instanceof NMessagePacket) {
                        NMessagePacket messagePacket = (NMessagePacket) o;
                        Class<? extends NSocketMessage> messageClass = messagePacket.message.getClass();
                        if (messageHandlers.containsKey(messageClass)) {
                            taskQueue.join(() -> messageHandlers.get(messageClass).handleMessage(id, messagePacket.message));
                        } else
                            logger.log(NLogMessage.NSeverity.WARNING, label, "No handler for message class " + messageClass);
                    } else if (o instanceof NCommandPacket) {
                        NCommandPacket commandPacket = (NCommandPacket) o;
                        Class<? extends NSocketCommand> commandClass = commandPacket.command.getClass();
                        if (commandHandlers.containsKey(commandClass)) {
                            taskQueue.join(() -> {
                                try {
                                    Serializable response = commandHandlers.get(commandClass).handleCommand(id, commandPacket.command);
                                    try {
                                        client.send(new NResponsePacket(commandPacket.id, response));
                                    } catch (IOException ioex) {
                                        logger.log(NLogMessage.NSeverity.DEBUG, label, "IOException while sending response to client");
                                        logger.log(NLogMessage.NSeverity.DEBUG, label, ioex);
                                    }
                                } catch (RuntimeException ex) {
                                    logger.log(NLogMessage.NSeverity.WARNING, label, "Exception in command handler for class " + commandClass + ":");
                                    logger.log(NLogMessage.NSeverity.WARNING, label, ex);
                                    try {
                                        client.send(new NExceptionPacket(commandPacket.id, ex));
                                    } catch (IOException ioex) {
                                        logger.log(NLogMessage.NSeverity.DEBUG, label, "IOException while sending exception to client");
                                        logger.log(NLogMessage.NSeverity.DEBUG, label, ioex);
                                    }
                                }
                            });
                        } else
                            logger.log(NLogMessage.NSeverity.ERROR, label, "No handler for command class " + commandClass);
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
                        logger.log(NLogMessage.NSeverity.ERROR, "Client has sent the object of an unknown class " + o.getClass());
                    }
                } catch (ClassNotFoundException e) {
                    logger.log(NLogMessage.NSeverity.ERROR, label, "Client has sent the object of an unknown class!");
                    logger.log(NLogMessage.NSeverity.ERROR, label, e);
                }
            }
        } catch (IOException e) {
            logger.log(NLogMessage.NSeverity.DEBUG, label, "Lost connection with client:");
            logger.log(NLogMessage.NSeverity.DEBUG, label, e);
            clients.remove(id);
            if (clientDisconnectedHandler != null)
                taskQueue.join(() -> clientDisconnectedHandler.onClientDisconnected(id));
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }


    @Override
    public String toString() {
        return "NSocketServer(:" + port + ")";
    }

    private static final class Client {
        public final int id;
        public final Socket socket;
        public final ObjectOutputStream out;
        public final ObjectInputStream in;
        public final Thread listener;
        public boolean autoOutputReset = false;
        public boolean autoInputReset = false;

        public Client(int id, Socket socket, Thread listener) throws IOException {
            this.id = id;
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            this.listener = listener;
        }

        public synchronized void send(Serializable o) throws IOException {
            out.writeObject(o);
            if (autoOutputReset) out.reset();
        }
    }

    public interface NClientConnectedHandler {
        void onClientConnected(int id);
    }

    public interface NClientDisconnectedHandler {
        void onClientDisconnected(int id);
    }

    public interface NSocketMessageHandler<M extends NSocketMessage> {
        void handleMessage(int clientId, M message);
    }

    public interface NSocketCommandHandler<C extends NSocketCommand<R>, R extends Serializable> {
        R handleCommand(int clientId, C command);
    }
}
