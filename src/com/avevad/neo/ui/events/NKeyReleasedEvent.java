package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public class NKeyReleasedEvent extends NKeyEvent {

    public NKeyReleasedEvent(int key, char c) {
        super(key, c);
    }
}
