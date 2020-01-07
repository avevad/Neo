package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public class NKeyPressedEvent extends NEvent {
    public final int key;

    public NKeyPressedEvent(int key) {
        this.key = key;
    }
}
