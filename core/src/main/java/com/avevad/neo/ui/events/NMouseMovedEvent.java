package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public class NMouseMovedEvent extends NEvent {
    public final int x, y;

    public NMouseMovedEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
