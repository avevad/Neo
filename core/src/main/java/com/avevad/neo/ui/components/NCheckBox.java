package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NEvent;
import com.avevad.neo.ui.NEventDispatcher;
import com.avevad.neo.ui.events.*;

import static com.avevad.neo.ui.components.NButton.EMULATE_MOUSE_CLICK_KEY;
import static com.avevad.neo.ui.components.NButton.EMULATE_MOUSE_PRESS_KEY;

public class NCheckBox extends NComponent {
    private int backgroundColor = NColor.NONE;
    private int foregroundColor = NColor.NONE;
    private boolean isPressed = false;
    private boolean isHovered = false;
    private boolean isChecked = false;
    private NCheckBoxUI ui;

    public final NEventDispatcher<NCheckStateChangedEvent> stateChanged = new NEventDispatcher<>();
    private boolean enabled = true;

    public void setUI(NCheckBoxUI ui) {
        this.ui = ui;
    }

    public NCheckBoxUI getUI() {
        return ui;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public boolean isHovered() {
        return isHovered;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }


    @Override
    public boolean onMousePressed(NMousePressedEvent event) {
        if (!isEnabled()) return false;
        return isPressed = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!isEnabled()) return false;
        boolean ret = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
        boolean isClicked = isPressed && ret;
        isPressed = false;
        if (isClicked) {
            isChecked = !isChecked;
            stateChanged.trigger(new NCheckStateChangedEvent(!isChecked, isChecked));
        }
        return ret;
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        if (!isEnabled()) return false;
        return isHovered = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        if (!isEnabled()) return false;
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        if (!isEnabled()) return false;
        return isHovered = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public void onMouseExited() {
        isHovered = false;
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {
        if (!isEnabled()) return;
        if (event.key == EMULATE_MOUSE_PRESS_KEY) {
            isPressed = true;
        }
        if (event.key == EMULATE_MOUSE_CLICK_KEY) {
            if (isPressed) {
                stateChanged.trigger(new NCheckStateChangedEvent(isChecked, !isChecked));
                isChecked = !isChecked;
            }
            isPressed = !isPressed;
        }
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {
        if (!isEnabled()) return;
        if (event.key == EMULATE_MOUSE_CLICK_KEY || event.key == EMULATE_MOUSE_PRESS_KEY) {
            if (isPressed) {
                stateChanged.trigger(new NCheckStateChangedEvent(isChecked, !isChecked));
                isChecked = !isChecked;
            }
            isPressed = false;
        }
    }

    @Override
    public boolean isKeyboardNeeded() {
        return false;
    }

    @Override
    public boolean render(int layer) {
        return ui.drawCheckbox(this, layer);
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public interface NCheckBoxUI {
        boolean drawCheckbox(NCheckBox checkBox, int layer);
    }

    public static final class NCheckStateChangedEvent extends NEvent {
        public final boolean wasChecked, isChecked;

        public NCheckStateChangedEvent(boolean wasChecked, boolean isChecked) {
            this.wasChecked = wasChecked;
            this.isChecked = isChecked;
        }
    }
}
