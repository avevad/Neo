package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public abstract class NKeyEvent extends NEvent {
    public final NKey key;
    public final char c;

    public NKeyEvent(NKey key, char c) {
        this.key = key;
        this.c = c;
    }

    public enum NKey {
        CTRL, ALT, SHIFT,
        TAB,
        INSERT, DELETE, HOME, END, PAGE_UP, PAGE_DOWN,
        ARROW_UP, ARROW_LEFT, ARROW_DOWN, ARROW_RIGHT,
        UNKNOWN
    }
}
