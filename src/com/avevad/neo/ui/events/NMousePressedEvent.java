package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public class NMousePressedEvent extends NEvent {
    public final int x, y, button;

    public NMousePressedEvent(int x, int y, int button) {
        this.x = x;
        this.y = y;
        this.button = button;
    }
}
