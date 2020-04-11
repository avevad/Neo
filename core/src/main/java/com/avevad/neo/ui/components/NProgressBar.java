package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.*;
import com.avevad.neo.ui.events.*;


public class NProgressBar extends NComponent {
    private double progress = 0;
    private NDirection direction = NDirection.RIGHT;
    private int backgroundColor = NColor.NONE;
    private int foregroundColor = NColor.NONE;
    private NFont font;
    private String text = "";
    private NProgressBarUI ui;

    public void setUI(NProgressBarUI ui) {
        this.ui = ui;
    }

    public NProgressBarUI getUI() {
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

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public double getProgress() {
        return progress;
    }

    public void setDirection(NDirection direction) {
        this.direction = direction;
    }

    public NDirection getDirection() {
        return direction;
    }

    @Override
    public boolean onMousePressed(NMousePressedEvent event) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {

    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {

    }

    @Override
    public boolean isKeyboardNeeded() {
        return false;
    }

    @Override
    public boolean render(int layer) {
        return ui.drawProgressBar(this, layer);
    }

    public interface NProgressBarUI {
        boolean drawProgressBar(NProgressBar progressBar, int layer);
    }
}
