package com.avevad.neo.graphics;


public abstract class NGraphics {
    public abstract void setColor(int color);

    public abstract int getColor();

    public abstract void setFont(NFont font);

    public abstract NFont getFont();

    public abstract void drawLine(int x1, int y1, int x2, int y2);

    public abstract void drawRect(int x, int y, int w, int h);

    public abstract void fillRect(int x, int y, int w, int h);

    public abstract void drawOval(int x, int y, int w, int h);

    public abstract void fillOval(int x, int y, int w, int h);

    public abstract void drawPolygon(int[] xs, int[] ys);

    public abstract void fillPolygon(int[] xs, int[] ys);

    public abstract void drawString(String s, int x, int y);

    public abstract NGraphics create();

    public abstract NGraphics create(int x, int y, int w, int h);
}
