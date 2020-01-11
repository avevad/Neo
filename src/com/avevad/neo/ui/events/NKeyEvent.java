package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public abstract class NKeyEvent extends NEvent {
    public final int key;
    public final char c;

    public NKeyEvent(int key, char c) {
        this.key = key;
        this.c = c;
    }
}
