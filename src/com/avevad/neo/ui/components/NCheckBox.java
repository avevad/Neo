package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.*;
import com.avevad.neo.ui.events.*;

import static com.avevad.neo.ui.components.NButton.EMULATE_MOUSE_CLICK_KEY;
import static com.avevad.neo.ui.components.NButton.EMULATE_MOUSE_PRESS_KEY;

public class NCheckBox extends NComponent {
    private int backgroundColor = NColor.NONE;
    private int foregroundColor = NColor.NONE;
    private boolean isPressed = false;
    private boolean isHovered = false;
    private boolean isChecked = false;

    public final NEventDispatcher<NCheckStateChangedEvent> stateChanged = new NEventDispatcher<>();


    public NCheckBox() {
        setUI(new NCheckBox.DefaultUI());
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
            stateChanged.trigger(new NCheckStateChangedEvent(isChecked, !isChecked));
            isChecked = !isChecked;
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

    private final static class DefaultUI implements NUI {
        public static final int DEFAULT_BACKGROUND_COLOR = NColor.WHITE;
        public static final int DEFAULT_FOREGROUND_COLOR = NColor.BLACK;

        public static final double PRESS_COLOR_RATIO = 0.25;
        public static final double HOVER_COLOR_RATIO = 0.75;
        public static final double FOCUS_COLOR_RATIO = 0.5;

        public static final int DISABLED_COVER_COLOR = NColor.WHITE;
        public static final double DISABLED_COVER_OPACITY = 0.5;

        public static final double CHECK_X1 = 0.2, CHECK_Y1 = 0.2;
        public static final double CHECK_X2 = 0.5, CHECK_Y2 = 0.8;
        public static final double CHECK_X3 = 0.8, CHECK_Y3 = 0.2;

        @Override
        public boolean render(NComponent component, int layer) {
            if (!(component instanceof NCheckBox))
                throw new IllegalArgumentException("This UI can only render NCheckBox");
            if (layer > 0) return false;
            NCheckBox checkBox = (NCheckBox) component;
            NGraphics g = checkBox.getGraphics();

            NDimension size = checkBox.getSize();
            int w = size.w;
            int h = size.h;
            int x1 = (int) (w * CHECK_X1);
            int y1 = (int) (h * CHECK_Y1);
            int x2 = (int) (w * CHECK_X2);
            int y2 = (int) (h * CHECK_Y2);
            int x3 = (int) (w * CHECK_X3);
            int y3 = (int) (h * CHECK_Y3);
            int fx1 = Integer.min(Integer.min(x1, x2), x3);
            int fy1 = Integer.min(Integer.min(y1, y2), y3);
            int fx2 = Integer.max(Integer.max(x1, x2), x3);
            int fy2 = Integer.max(Integer.max(y1, y2), y3);
            int fw = fx2 - fx1;
            int fh = fy2 - fy1;
            boolean pressed = checkBox.isPressed();
            boolean hovered = checkBox.isHovered();
            boolean focused = checkBox.isFocused();
            boolean checked = checkBox.isChecked();

            int backgroundColor = checkBox.getBackgroundColor();
            if (backgroundColor == NColor.NONE) backgroundColor = DEFAULT_BACKGROUND_COLOR;
            int foregroundColor = checkBox.getForegroundColor();
            if (foregroundColor == NColor.NONE) foregroundColor = DEFAULT_FOREGROUND_COLOR;
            int pressColor = NColor.mix(backgroundColor, foregroundColor, PRESS_COLOR_RATIO);
            int hoverColor = NColor.mix(backgroundColor, foregroundColor, HOVER_COLOR_RATIO);
            int focusColor = NColor.mix(backgroundColor, foregroundColor, FOCUS_COLOR_RATIO);
            if (pressed) backgroundColor = pressColor;
            else if (hovered) backgroundColor = hoverColor;

            g.setOpacity(checkBox.getOpacity());

            g.setColor(backgroundColor);
            g.fillRect(0, 0, w, h);

            if (checked) {
                g.setColor(foregroundColor);
                g.drawLine(x1, y1, x2, y2);
                g.drawLine(x2, y2, x3, y3);
            }

            if (focused) {
                g.setColor(focusColor);
                g.drawRect(fx1, fy1, fw, fh);
            }

            g.setColor(foregroundColor);
            g.drawRect(0, 0, w - 1, h - 1);

            if (!checkBox.isEnabled()) {
                g.setOpacity(DISABLED_COVER_OPACITY * checkBox.getOpacity());
                g.setColor(DISABLED_COVER_COLOR);
                g.fillRect(new NRectangle(NPoint.ZERO, checkBox.getSize()));
            }

            return false;
        }
    }

    public static final class NCheckStateChangedEvent extends NEvent {
        public final boolean wasChecked, isChecked;

        public NCheckStateChangedEvent(boolean wasChecked, boolean isChecked) {
            this.wasChecked = wasChecked;
            this.isChecked = isChecked;
        }
    }
}
