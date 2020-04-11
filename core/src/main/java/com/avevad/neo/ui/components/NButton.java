package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.*;
import com.avevad.neo.ui.events.*;

public class NButton extends NComponent {
    public static final NKeyEvent.NKey EMULATE_MOUSE_PRESS_KEY = NKeyEvent.NKey.SPACE;
    public static final NKeyEvent.NKey EMULATE_MOUSE_CLICK_KEY = NKeyEvent.NKey.ENTER;

    private String text = "";
    private NFont font;
    private int backgroundColor = NColor.NONE;
    private int foregroundColor = NColor.NONE;
    private boolean isPressed = false;
    private boolean isHovered = false;
    private NButtonUI ui;

    public final NEventDispatcher<ClickedEvent> clicked = new NEventDispatcher<>();
    private boolean enabled = true;

    public void setUI(NButtonUI ui) {
        this.ui = ui;
    }

    public NButtonUI getUI() {
        return ui;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setFont(NFont font) {
        this.font = font;
    }

    public NFont getFont() {
        return font;
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
        if (isClicked) clicked.trigger(new ClickedEvent());
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
            if (isPressed) clicked.trigger(new ClickedEvent());
            isPressed = !isPressed;
        }
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {
        if (!isEnabled()) return;
        if (event.key == EMULATE_MOUSE_CLICK_KEY || event.key == EMULATE_MOUSE_PRESS_KEY) {
            if (isPressed) clicked.trigger(new ClickedEvent());
            isPressed = false;
        }
    }

    @Override
    public boolean isKeyboardNeeded() {
        return false;
    }

    @Override
    public boolean render(int layer) {
        return ui.drawButton(this, layer);
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public interface NButtonUI {
        boolean drawButton(NButton button, int layer);
    }

    public static class ClickedEvent extends NEvent {

    }
}
