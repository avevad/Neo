package com.avevad.neo.graphics;

import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NEvent;
import com.avevad.neo.ui.NEventDispatcher;
import com.avevad.neo.ui.NUI;
import com.avevad.neo.ui.events.*;

import java.util.ArrayList;
import java.util.List;

import static com.avevad.neo.ui.components.NButton.EMULATE_MOUSE_CLICK_KEY;
import static com.avevad.neo.ui.components.NButton.EMULATE_MOUSE_PRESS_KEY;

public class NRadioButton extends NComponent {
    private int backgroundColor = NColor.NONE;
    private int foregroundColor = NColor.NONE;
    private boolean isPressed = false;
    private boolean isHovered = false;
    private boolean isChecked = false;

    public final NEventDispatcher<NCheckedEvent> checked = new NEventDispatcher<>();


    public NRadioButton() {
        setUI(new DefaultUI());
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
            if (!isChecked) checked.trigger(new NCheckedEvent());
            isChecked = true;
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
                if (!isChecked) checked.trigger(new NCheckedEvent());
                isChecked = true;
            }
            isPressed = !isPressed;
        }
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {
        if (!isEnabled()) return;
        if (event.key == EMULATE_MOUSE_CLICK_KEY || event.key == EMULATE_MOUSE_PRESS_KEY) {
            if (isPressed) {
                if (!isChecked) checked.trigger(new NCheckedEvent());
                isChecked = true;
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

        public static final double CHECK_RATIO = 0.6;

        @Override
        public boolean render(NComponent component, int layer) {
            if (!(component instanceof NRadioButton))
                throw new IllegalArgumentException("This UI can only render NRadioButton");
            if (layer > 0) return false;
            NRadioButton radioButton = (NRadioButton) component;
            NGraphics g = radioButton.getGraphics();

            NDimension size = radioButton.getSize();
            int w = size.w;
            int h = size.h;
            int cx = (int) ((1. - CHECK_RATIO) / 2. * w);
            int cy = (int) ((1. - CHECK_RATIO) / 2. * h);
            int cw = (int) (CHECK_RATIO * w);
            int ch = (int) (CHECK_RATIO * h);
            boolean pressed = radioButton.isPressed();
            boolean hovered = radioButton.isHovered();
            boolean focused = radioButton.isFocused();
            boolean checked = radioButton.isChecked();

            int backgroundColor = radioButton.getBackgroundColor();
            if (backgroundColor == NColor.NONE) backgroundColor = DEFAULT_BACKGROUND_COLOR;
            int foregroundColor = radioButton.getForegroundColor();
            if (foregroundColor == NColor.NONE) foregroundColor = DEFAULT_FOREGROUND_COLOR;
            int pressColor = NColor.mix(backgroundColor, foregroundColor, PRESS_COLOR_RATIO);
            int hoverColor = NColor.mix(backgroundColor, foregroundColor, HOVER_COLOR_RATIO);
            int focusColor = NColor.mix(backgroundColor, foregroundColor, FOCUS_COLOR_RATIO);
            if (pressed) backgroundColor = pressColor;
            else if (hovered) backgroundColor = hoverColor;

            g.setOpacity(radioButton.getOpacity());

            g.setColor(backgroundColor);
            g.fillOval(0, 0, w, h);

            if (checked) {
                g.setColor(foregroundColor);
                g.fillOval(cx, cy, cw - 1, ch - 1);
            }

            if (focused) {
                g.setColor(focusColor);
                g.drawOval(cx, cy, cw - 1, ch - 1);
            }

            g.setColor(foregroundColor);
            g.drawOval(0, 0, w - 1, h - 1);

            if (!radioButton.isEnabled()) {
                g.setOpacity(DISABLED_COVER_OPACITY * radioButton.getOpacity());
                g.setColor(DISABLED_COVER_COLOR);
                g.fillOval(new NRectangle(NPoint.ZERO, radioButton.getSize()));
            }

            return false;
        }
    }

    public static final class NCheckedEvent extends NEvent {

        public NCheckedEvent() {
        }
    }


    public static final class NRadioButtonGroup {
        private final List<NRadioButton> buttons = new ArrayList<>();
        private final List<NEventDispatcher.NEventHandler<NCheckedEvent>> handlers = new ArrayList<>();

        public final NEventDispatcher<NCheckPositionChangedEvent> checkPositionChanged = new NEventDispatcher<>();

        public void addButton(final NRadioButton button) {
            if (buttons.contains(button)) throw new IllegalArgumentException("group contains that button");
            synchronized (handlers) {
                synchronized (buttons) {
                    buttons.add(button);
                    NEventDispatcher.NEventHandler<NCheckedEvent> handler = event -> {
                        NRadioButton oldCheck = null;
                        for (NRadioButton button2 : buttons) {
                            if (button2.isChecked()) oldCheck = button2;
                            if (button2 != button) button2.setChecked(false);
                        }
                        if (oldCheck != button)
                            checkPositionChanged.trigger(new NCheckPositionChangedEvent(oldCheck, button));
                    };
                    button.checked.addHandler(handler);
                    handlers.add(handler);
                }
            }
        }

        public void removeButton(final NRadioButton button) {
            if (!buttons.contains(button)) return;
            synchronized (handlers) {
                synchronized (buttons) {
                    int i = buttons.indexOf(button);
                    buttons.remove(i);
                    button.checked.removeHandler(handlers.get(i));
                    handlers.remove(i);
                }
            }
        }

        public int getButtonIndex(NRadioButton button) {
            return buttons.indexOf(button);
        }

        public static final class NCheckPositionChangedEvent extends NEvent {
            public final NRadioButton oldCheck, newCheck;

            public NCheckPositionChangedEvent(NRadioButton oldCheck, NRadioButton newCheck) {
                this.oldCheck = oldCheck;
                this.newCheck = newCheck;
            }
        }
    }
}
