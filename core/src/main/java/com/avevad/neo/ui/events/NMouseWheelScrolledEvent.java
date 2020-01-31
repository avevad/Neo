package com.avevad.neo.ui.events;

import com.avevad.neo.ui.NEvent;

public class NMouseWheelScrolledEvent extends NEvent {
    public final int x, y, value;

    public NMouseWheelScrolledEvent(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }
}
