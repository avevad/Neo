package com.avevad.neo.graphics;


public abstract class NGraphics {
    public abstract void setOpacity(double opacity);

    public abstract double getOpacity();

    public abstract void setColor(int color);

    public abstract int getColor();

    public abstract void setFont(NFont font);

    public abstract NFont getFont();

    public abstract void drawLine(int x1, int y1, int x2, int y2);

    public void drawLine(NPoint point1, NPoint point2) {
        drawLine(point1.x, point1.y, point2.x, point2.y);
    }

    public abstract void drawRect(int x, int y, int w, int h);

    public void drawRect(NPoint point, NDimension size) {
        drawRect(point.x, point.y, size.w, size.h);
    }

    public void drawRect(NRectangle rect) {
        drawRect(rect.x, rect.y, rect.w, rect.h);
    }

    public abstract void fillRect(int x, int y, int w, int h);

    public void fillRect(NPoint point, NDimension size) {
        fillRect(point.x, point.y, size.w, size.h);
    }

    public void fillRect(NRectangle rect) {
        fillRect(rect.x, rect.y, rect.w, rect.h);
    }

    public abstract void drawOval(int x, int y, int w, int h);

    public void drawOval(NPoint point, NDimension size) {
        drawOval(point.x, point.y, size.w, size.h);
    }

    public void drawOval(NRectangle bounds) {
        drawOval(bounds.x, bounds.y, bounds.w, bounds.h);
    }

    public abstract void fillOval(int x, int y, int w, int h);

    public void fillOval(NPoint point, NDimension size) {
        fillOval(point.x, point.y, size.w, size.h);
    }

    public void fillOval(NRectangle bounds) {
        fillOval(bounds.x, bounds.y, bounds.w, bounds.h);
    }

    public abstract void drawPolygon(int[] xs, int[] ys);

    public abstract void fillPolygon(int[] xs, int[] ys);

    public abstract void drawString(String s, int x, int y);

    public void drawString(String s, NPoint point) {
        drawString(s, point.x, point.y);
    }

    public abstract NGraphics create();

    public abstract NGraphics create(int x, int y, int w, int h);

    public NGraphics create(NPoint point, NDimension size) {
        return create(point.x, point.y, size.w, size.h);
    }

    public NGraphics create(NRectangle bounds) {
        return create(bounds.x, bounds.y, bounds.w, bounds.h);
    }

    public abstract void drawImage(NImage img, int x, int y);

    public void drawImage(NImage img, NPoint point) {
        drawImage(img, point.x, point.y);
    }

    public abstract void drawImage(NImage img, int x, int y, int w, int h);

    public void drawImage(NImage img, NPoint point, NDimension size) {
        drawImage(img, point.x, point.y, size.w, size.h);
    }

    public void drawImage(NImage img, NRectangle bounds) {
        drawImage(img, bounds.x, bounds.y, bounds.w, bounds.h);
    }

    public abstract void drawImage(NImage img, int srcX, int srcY, int srcW, int srcH, int dstX, int dstY, int dstW, int dstH);

    public void drawImage(NImage img, NPoint srcPoint, NDimension srcSize, NPoint dstPoint, NDimension dstSize) {
        drawImage(img, srcPoint.x, srcPoint.y, srcSize.w, srcSize.h, dstPoint.x, dstPoint.y, dstSize.w, dstSize.h);
    }

    public void drawImage(NImage img, NRectangle srcArea, NRectangle dstArea) {
        drawImage(img, srcArea.x, srcArea.y, srcArea.w, srcArea.h, dstArea.x, dstArea.y, dstArea.w, dstArea.h);
    }

    public abstract NFontMetrics getFontMetrics(NFont font);

}
