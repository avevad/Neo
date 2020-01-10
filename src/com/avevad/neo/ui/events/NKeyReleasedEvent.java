package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public class NKeyReleasedEvent extends NEvent {
    public final int key;
    public final char c;

    public NKeyReleasedEvent(int key, char c) {
        this.key = key;
        this.c = c;
    }
}
