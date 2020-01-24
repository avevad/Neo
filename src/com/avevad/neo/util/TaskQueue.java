package com.avevad.neo.util;

import com.avevad.neo.logging.NLogDestination;
import com.avevad.neo.logging.NLogMessage;
import com.avevad.neo.logging.NLogger;

import java.util.ArrayDeque;
import java.util.Queue;

public final class TaskQueue {
    private final Thread worker;
    private final Object lock = new Object();
    private final Queue<Runnable> queue = new ArrayDeque<>();
    private final String name;
    private final NLogger logger;
    private boolean destroyed = false;

    public TaskQueue(String name, NLogDestination destination) {
        this.name = name;
        logger = new NLogger(toString(), destination);
        worker = new Thread(this::run, toString() + ":worker");
    }

    @Override
    public String toString() {
        return "TaskQueue('" + name + "')";
    }

    public void join(Runnable task) {
        if (destroyed) throw new IllegalStateException("destroyed");
        synchronized (lock) {
            queue.add(task);
            lock.notifyAll();
        }
    }

    public void start() {
        worker.start();
    }

    public void destroy() {
        worker.stop();
        queue.clear();
        destroyed = true;
    }

    private void run() {
        while (true) {
            while (queue.isEmpty()) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        logger.log(NLogMessage.NSeverity.WARNING, "worker", "Worker interrupted!");
                        logger.log(NLogMessage.NSeverity.WARNING, "worker", e);
                    }
                }
            }
            try {
                queue.poll().run();
            } catch (Exception ex) {
                logger.log(NLogMessage.NSeverity.ERROR, "worker", "Task has thrown an exception!");
                logger.log(NLogMessage.NSeverity.ERROR, ex);
            }
        }
    }
}
