package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NHorizontalTextAlignment;
import com.avevad.neo.ui.NVerticalTextAlignment;
import com.avevad.neo.ui.events.*;

public class NLabel extends NComponent {

    private String text = "";
    private NFont font;
    private NHorizontalTextAlignment hAlign = NHorizontalTextAlignment.CENTER;
    private NVerticalTextAlignment vAlign = NVerticalTextAlignment.CENTER;
    private int color = NColor.NONE;
    private NLabelUI ui;

    public void setUI(NLabelUI ui) {
        this.ui = ui;
    }

    public NLabelUI getUI() {
        return ui;
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


    public static int alignText(String text, int width, NFontMetrics fontMetrics, NHorizontalTextAlignment align) {
        int tw = fontMetrics.getWidth(text);
        switch (align) {
            case LEFT:
                return 0;
            case RIGHT:
                return width - tw;
            case CENTER:
            default:
                return (width - tw) / 2;
        }
    }

    public static int alignText(String text, int height, NFontMetrics fontMetrics, NVerticalTextAlignment align) {
        int ta = fontMetrics.getAscent();
        int td = fontMetrics.getDescent();
        int th = ta + td;
        switch (align) {
            case TOP:
                return ta;
            case BOTTOM:
                return height - td;
            case CENTER:
            default:
                return (height - th) / 2 + ta;
        }
    }

    public static NPoint alignText(String text, NDimension boundsSize, NFontMetrics fontMetrics,
                                   NHorizontalTextAlignment hAlign, NVerticalTextAlignment vAlign) {
        int x = alignText(text, boundsSize.w, fontMetrics, hAlign);
        int y = alignText(text, boundsSize.h, fontMetrics, vAlign);
        return new NPoint(x, y);
    }

    @Override
    public boolean render(int layer) {
        return ui.drawLabel(this, layer);
    }

    public interface NLabelUI {
        boolean drawLabel(NLabel label, int layer);
    }

}
