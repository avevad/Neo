package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public class NMouseReleasedEvent extends NEvent {
    public final int x, y, button;

    public NMouseReleasedEvent(int x, int y, int button) {
        this.x = x;
        this.y = y;
        this.button = button;
    }
}
