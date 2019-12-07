package com.avevad.neo.graphics;

public final class NRectangle {
    public final int x, y, w, h;

    public NRectangle(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public NRectangle(NPoint point, NDimension size) {
        x = point.x;
        y = point.y;
        w = size.w;
        h = size.h;
    }

    public NRectangle(NPoint p1, NPoint p2) {
        x = p1.x;
        y = p1.y;
        w = p2.x - p1.x;
        h = p2.y - p1.y;
    }

    public NPoint getPoint() {
        return new NPoint(x, y);
    }

    public NDimension getSize() {
        return new NDimension(w, h);
    }
}
