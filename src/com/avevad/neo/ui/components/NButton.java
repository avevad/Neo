package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.*;
import com.avevad.neo.ui.events.*;

public class NButton extends NComponent {
    private String text = "";
    private NFont font;
    private int backgroundColor = NColor.NONE;
    private int foregroundColor = NColor.NONE;
    private boolean isPressed = false;
    private boolean isHovered = false;

    public final NEventDispatcher<ClickedEvent> clicked = new NEventDispatcher<>();


    public NButton() {
        setUI(new DefaultUI());
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
        return isPressed = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        boolean ret = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
        boolean isClicked = isPressed && ret;
        isPressed = false;
        if (isClicked) clicked.trigger(new ClickedEvent());
        return ret;
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        return isHovered = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        return isHovered = new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public void onMouseExited() {
        isHovered = false;
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {
        if (event.c == ' ' || event.c == '\n') isPressed = true;
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {
        if (isPressed) clicked.trigger(new ClickedEvent());
        if (event.c == ' ' || event.c == '\n') isPressed = false;
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

        @Override
        public boolean render(NComponent component, int layer) {
            if (!(component instanceof NButton)) throw new IllegalArgumentException("This UI can only render NButton");
            if (layer > 0) return false;
            NButton button = (NButton) component;
            NGraphics g = button.getGraphics();

            NDimension size = button.getSize();
            int w = size.w;
            int h = size.h;
            NFont font = button.getFont();
            NFontMetrics fontMetrics = g.getFontMetrics(font);
            String text = NLabel.cutToFit(button.getText(), w, fontMetrics);
            int ascent = fontMetrics.getAscent();
            NPoint point = NLabel.alignText(text, size, fontMetrics, NHorizontalTextAlignment.CENTER, NVerticalTextAlignment.CENTER);
            boolean pressed = button.isPressed();
            boolean hovered = button.isHovered();
            boolean focused = button.isFocused();

            int backgroundColor = button.getBackgroundColor();
            if (backgroundColor == NColor.NONE) backgroundColor = DEFAULT_BACKGROUND_COLOR;
            int foregroundColor = button.getForegroundColor();
            if (foregroundColor == NColor.NONE) foregroundColor = DEFAULT_FOREGROUND_COLOR;
            int pressColor = NColor.mix(backgroundColor, foregroundColor, PRESS_COLOR_RATIO);
            int hoverColor = NColor.mix(backgroundColor, foregroundColor, HOVER_COLOR_RATIO);
            int focusColor = NColor.mix(backgroundColor, foregroundColor, FOCUS_COLOR_RATIO);
            if (pressed) backgroundColor = pressColor;
            else if (hovered) backgroundColor = hoverColor;

            g.setColor(backgroundColor);
            g.fillRect(0, 0, w, h);

            g.setColor(foregroundColor);
            g.drawRect(0, 0, w - 1, h - 1);

            g.setFont(font);
            g.drawString(text, point);

            g.setColor(focusColor);
            if (focused) g.drawRect(point.x / 2, (point.y - ascent) / 2, w - point.x - 1, h - (point.y - ascent) - 1);

            return false;
        }
    }

    public static class ClickedEvent extends NEvent {

    }
}
