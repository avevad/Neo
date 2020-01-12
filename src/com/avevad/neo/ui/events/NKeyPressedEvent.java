package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public class NKeyPressedEvent extends NKeyEvent {

    public NKeyPressedEvent(NKey key, char c) {
        super(key, c);
    }
}
