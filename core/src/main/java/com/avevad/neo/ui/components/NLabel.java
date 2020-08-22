package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NHorizontalDirection;
import com.avevad.neo.ui.NHorizontalTextAlignment;
import com.avevad.neo.ui.NVerticalTextAlignment;
import com.avevad.neo.ui.events.*;
import com.avevad.neo.util.NPair;

import static com.avevad.neo.ui.NHorizontalDirection.LEFT;

public class NLabel extends NComponent {

    private NImage icon;
    private NHorizontalDirection iconPosition;
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

    public void setIcon(NImage icon) {
        this.icon = icon;
    }

    public NImage getIcon() {
        return icon;
    }

    public void setIconPosition(NHorizontalDirection iconPosition) {
        this.iconPosition = iconPosition;
    }

    public NHorizontalDirection getIconPosition() {
        return iconPosition;
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

    public void resizeToFit() {
        setSize(resizeToFit(icon, text, getGraphics().getFontMetrics(getFont())));
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


    public static NPair<Integer, Integer> alignLabelX(NImage icon, String text, int width, NFontMetrics fontMetrics, NHorizontalTextAlignment align, NHorizontalDirection iconPosition) {
        if(iconPosition == null) iconPosition = LEFT;
        int iw = icon == null ? 0 : icon.w;
        int tw = fontMetrics.getWidth(text);
        int lw = tw + iw;
        int lx;
        switch (align) {
            case LEFT:
                lx = 0;
                break;
            case RIGHT:
                lx = width - lw;
                break;
            case CENTER:
            default:
                lx = (width - lw) / 2;
                break;
        }
        int ix, tx;
        switch (iconPosition) {
            case LEFT:
                ix = lx;
                tx = ix + iw;
                break;
            case RIGHT:
                ix = lx + lw - iw;
                tx = lx;
                break;
            default:
                throw new IllegalArgumentException("icon can't be aligned vertically");
        }
        return new NPair<>(ix, tx);
    }

    public static NPair<Integer, Integer> alignLabelY(NImage icon, String text, int height, NFontMetrics fontMetrics, NVerticalTextAlignment align) {
        int ih = icon == null ? 0 : icon.h;
        int ta = fontMetrics.getAscent();
        int td = fontMetrics.getDescent();
        int th = text.length() == 0 ? 0 : ta + td;
        int ty;
        switch (align) {
            case TOP:
                ty = ta;
                break;
            case BOTTOM:
                ty = height - td;
                break;
            case CENTER:
            default:
                ty = (height - th) / 2 + ta;
        }
        int iy = (ty - ta) + (th - ih) / 2;
        return new NPair<>(iy, ty);
    }

    public static NPair<NPoint, NPoint> alignLabel(NImage icon, String text, NDimension boundsSize, NFontMetrics fontMetrics,
                                                   NHorizontalTextAlignment hAlign, NVerticalTextAlignment vAlign, NHorizontalDirection iconPosition) {
        NPair<Integer, Integer> xs = alignLabelX(icon, text, boundsSize.w, fontMetrics, hAlign, iconPosition);
        NPair<Integer, Integer> ys = alignLabelY(icon, text, boundsSize.h, fontMetrics, vAlign);
        return new NPair<>(new NPoint(xs.a, ys.a), new NPoint(xs.b, ys.b));
    }

    public static NDimension resizeToFit(NImage icon, String text, NFontMetrics fontMetrics) {
        return new NDimension(
                (text.length() == 0 ? 0 : fontMetrics.getWidth(text)) + (icon == null ? 0 : icon.w) + 8,
                Integer.max(text.length() == 0 ? 0 : (fontMetrics.getAscent() + fontMetrics.getDescent()), icon == null ? 0 : icon.h) + 8
        );
    }

    @Override
    public boolean render(int layer, NRectangle area) {
        return ui.drawLabel(this, layer, area);
    }

    public interface NLabelUI {
        boolean drawLabel(NLabel label, int layer, NRectangle area);
    }

}
