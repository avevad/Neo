package com.avevad.neo.ui;

import java.util.HashSet;
import java.util.Set;

public final class NEventDispatcher<E extends NEvent> {
    private final Set<NEventHandler<E>> handlers;

    public NEventDispatcher() {
        handlers = new HashSet<>();
    }

    public void addHandler(NEventHandler<E> handler) {
        handlers.add(handler);
    }

    public void removeHandler(NEventHandler<E> handler) {
        handlers.remove(handler);
    }

    public void trigger(E event) {
        for (NEventHandler<E> handler : handlers) {
            handler.handle(event);
        }
    }

    public interface NEventHandler<E extends NEvent> {
        void handle(E event);
    }
}
