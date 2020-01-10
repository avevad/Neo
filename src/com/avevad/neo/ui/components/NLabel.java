package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.NHorizontalTextAlignment;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NUI;
import com.avevad.neo.ui.NVerticalTextAlignment;

public class NLabel extends NComponent {

    private String text = "";
    private NFont font;
    private NHorizontalTextAlignment hAlign = NHorizontalTextAlignment.CENTER;
    private NVerticalTextAlignment vAlign = NVerticalTextAlignment.CENTER;
    private int color = NColor.NONE;


    public NLabel(NFont font) {
        this.font = font;
        setUI(new DefaultUI());
    }


    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setFont(NFont font) {
        this.font = font;
    }

    public NFont getFont() {
        return font;
    }

    public void setHorizontalAlignment(NHorizontalTextAlignment hAlign) {
        this.hAlign = hAlign;
    }

    public NHorizontalTextAlignment getHorizontalAlignment() {
        return hAlign;
    }

    public void setVerticalAlignment(NVerticalTextAlignment vAlign) {
        this.vAlign = vAlign;
    }

    public NVerticalTextAlignment getVerticalAlignment() {
        return vAlign;
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(x, y);
    }

    @Override
    public boolean onMouseReleased(int x, int y, int button) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(x, y);
    }

    @Override
    public boolean onMouseDragged(int x, int y, int button) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(x, y);
    }

    @Override
    public boolean onMouseWheelScrolled(int x, int y, int value) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(x, y);
    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        return new NRectangle(NPoint.ZERO, getSize()).contains(x, y);
    }

    @Override
    public void onKeyPressed(int key) {

    }

    @Override
    public void onKeyReleased(int key) {

    }

    @Override
    public boolean isKeyboardNeeded() {
        return false;
    }


    public static String cutToFit(String s, int w, NFontMetrics fontMetrics) {
        if (fontMetrics.getWidth(s) <= w) return s;
        int l = 0, r = s.length();
        while (r - l > 1) {
            int n = (l + r) / 2;
            if (fontMetrics.getWidth(s.substring(0, n) + "...") <= w) l = n;
            else r = n;
        }
        return s.substring(0, l) + "...";
    }


    private final static class DefaultUI implements NUI {
        public static final int DEFAULT_COLOR = NColor.BLACK;

        @Override
        public boolean render(NComponent component, int layer) {
            if (!(component instanceof NLabel)) throw new IllegalArgumentException("This UI can only render NLabel");
            if (layer > 0) return false;

            NLabel label = (NLabel) component;
            NGraphics g = label.getGraphics();

            NHorizontalTextAlignment hAlign = label.getHorizontalAlignment();
            NVerticalTextAlignment vAlign = label.getVerticalAlignment();
            int color = label.getColor();
            if (color == NColor.NONE) color = DEFAULT_COLOR;
            NFont font = label.getFont();
            NFontMetrics fontMetrics = g.getFontMetrics(font);
            int w = label.getWidth();
            int h = label.getHeight();
            String s = NLabel.cutToFit(label.getText(), w, fontMetrics);
            int sw = fontMetrics.getWidth(s);
            int sa = fontMetrics.getAscent();
            int sd = fontMetrics.getDescent();
            int sh = sa + sd;

            int x, y;
            switch (hAlign) {
                case LEFT:
                    x = 0;
                    break;
                case RIGHT:
                    x = w - sw;
                    break;
                case CENTER:
                default:
                    x = (w - sw) / 2;
                    break;
            }
            switch (vAlign) {
                case TOP:
                    y = sa;
                    break;
                case BOTTOM:
                    y = h - sd;
                    break;
                case CENTER:
                default:
                    y = (h - sh) / 2 + sa;
                    break;
            }
            g.setFont(font);
            g.setColor(color);
            g.drawString(s, x, y);
            return false;
        }
    }
}
