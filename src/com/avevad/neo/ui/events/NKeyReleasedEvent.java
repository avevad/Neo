package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public class NKeyReleasedEvent extends NEvent {
    public final int key;

    public NKeyReleasedEvent(int key) {
        this.key = key;
    }
}
