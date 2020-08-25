package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.NColor;
import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NEvent;
import com.avevad.neo.ui.NEventDispatcher;
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
    private NRadioButtonUI ui;

    public final NEventDispatcher<NCheckedEvent> checked = new NEventDispatcher<>();
    private boolean enabled = true;

    public void setUI(NRadioButtonUI ui) {
        this.ui = ui;
        update();
    }

    public NRadioButtonUI getUI() {
        return ui;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        update();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
        update();
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
        update();
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
            if (!isChecked) checked.trigger(new NCheckedEvent());
            isChecked = true;
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
                if (!isChecked) checked.trigger(new NCheckedEvent());
                isChecked = true;
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
                if (!isChecked) checked.trigger(new NCheckedEvent());
                isChecked = true;
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
        return ui.drawRadioButton(this, layer, area);
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
        update();
    }

    public interface NRadioButtonUI {
        boolean drawRadioButton(NRadioButton radioButton, int layer, NRectangle area);
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
