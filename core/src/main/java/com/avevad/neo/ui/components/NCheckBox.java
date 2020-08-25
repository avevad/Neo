package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.NColor;
import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
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
        isPressed = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
        update();
        return isPressed;
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
        update();
        return ret;
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        if (!isEnabled()) return false;
        isHovered = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
        update();
        return isHovered;
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        if (!isEnabled()) return false;
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        if (!isEnabled()) return false;
        isHovered = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
        update();
        return isHovered;
    }

    @Override
    public void onMouseExited() {
        isHovered = false;
        update();
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
        update();
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
        update();
    }

    @Override
    public boolean isKeyboardNeeded() {
        return false;
    }

    @Override
    public boolean render(int layer, NRectangle area) {
        return ui.drawCheckbox(this, layer, area);
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public interface NCheckBoxUI {
        boolean drawCheckbox(NCheckBox checkBox, int layer, NRectangle area);
    }

    public static final class NCheckStateChangedEvent extends NEvent {
        public final boolean wasChecked, isChecked;

        public NCheckStateChangedEvent(boolean wasChecked, boolean isChecked) {
            this.wasChecked = wasChecked;
            this.isChecked = isChecked;
        }
    }
}
