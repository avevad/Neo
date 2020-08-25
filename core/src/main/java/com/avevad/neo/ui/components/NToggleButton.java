package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.events.NKeyPressedEvent;
import com.avevad.neo.ui.events.NKeyReleasedEvent;
import com.avevad.neo.ui.events.NMousePressedEvent;
import com.avevad.neo.ui.events.NMouseReleasedEvent;

public class NToggleButton extends NButton {
    @Override
    public boolean onMousePressed(NMousePressedEvent event) {
        if (!isEnabled()) return false;
        update();
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!isEnabled()) return false;
        if (new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) {
            isPressed = !isPressed;
            clicked.trigger(new ClickedEvent());
            update();
            return true;
        } else return false;
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {
        if (!isEnabled()) return;
        if (event.key == EMULATE_MOUSE_CLICK_KEY) {
            isPressed = !isPressed;
            clicked.trigger(new ClickedEvent());
        }
        update();
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {
        if (!isEnabled()) return;
        if (event.key == EMULATE_MOUSE_PRESS_KEY) {
            isPressed = !isPressed;
            clicked.trigger(new ClickedEvent());
        }
        update();
    }

    public void setPressed(boolean pressed) {
        this.isPressed = pressed;
    }
}
