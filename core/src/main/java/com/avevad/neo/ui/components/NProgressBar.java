package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.NColor;
import com.avevad.neo.graphics.NFont;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NDirection;
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
        return false;
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        return false;
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        return false;
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        return false;
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        return false;
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
    public boolean render(int layer, NRectangle area) {
        return ui.drawProgressBar(this, layer, area);
    }

    public interface NProgressBarUI {
        boolean drawProgressBar(NProgressBar progressBar, int layer, NRectangle area);
    }
}
