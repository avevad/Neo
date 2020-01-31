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

    public NProgressBar() {
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

    private final static class DefaultUI implements NUI {
        public static final int DEFAULT_BACKGROUND_COLOR = NColor.WHITE;
        public static final int DEFAULT_FOREGROUND_COLOR = NColor.BLACK;

        @Override
        public boolean render(NComponent component, int layer) {
            if (!(component instanceof NProgressBar))
                throw new IllegalArgumentException("This UI can only render NProgressBar");
            if (layer > 0) return false;

            NProgressBar bar = (NProgressBar) component;
            NGraphics g = bar.getGraphics();

            NDirection direction = bar.getDirection();
            double progress = bar.getProgress();
            NDimension size = bar.getSize();
            int w = bar.getWidth();
            int h = bar.getHeight();
            int pw = (int) (w * progress);
            if (direction == NDirection.UP || direction == NDirection.DOWN) pw = w;
            int ph = (int) (h * progress);
            if (direction == NDirection.LEFT || direction == NDirection.RIGHT) ph = h;
            int px = 0;
            if (direction == NDirection.LEFT) px = w - pw;
            int py = 0;
            if (direction == NDirection.UP) py = h - ph;

            int backgroundColor = bar.getBackgroundColor();
            if (backgroundColor == NColor.NONE) backgroundColor = DEFAULT_BACKGROUND_COLOR;
            int foregroundColor = bar.getForegroundColor();
            if (foregroundColor == NColor.NONE) foregroundColor = DEFAULT_FOREGROUND_COLOR;
            int textColor = progress > 0.5 ? backgroundColor : foregroundColor;
            int baseColor = progress > 0.5 ? foregroundColor : backgroundColor;

            NFont font = bar.getFont();
            String text = bar.getText();
            text = String.format(text, progress * 100.);
            NFontMetrics fontMetrics = g.getFontMetrics(font);
            int ta = fontMetrics.getAscent();
            int td = fontMetrics.getDescent();
            int tw = fontMetrics.getWidth(text);
            NPoint p = NLabel.alignText(text, size, fontMetrics, NHorizontalTextAlignment.CENTER, NVerticalTextAlignment.CENTER);

            g.setOpacity(bar.getOpacity());

            g.setColor(backgroundColor);
            g.fillRect(0, 0, w, h);

            g.setColor(foregroundColor);
            g.fillRect(px, py, pw, ph);

            g.setColor(baseColor);
            g.fillRect(p.x, p.y - ta, tw, ta + td);

            g.setColor(textColor);
            g.drawString(text, p);

            g.setColor(foregroundColor);
            g.drawRect(0, 0, w - 1, h - 1);

            return false;
        }
    }
}
