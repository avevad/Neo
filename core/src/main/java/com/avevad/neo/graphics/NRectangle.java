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

    @Override
    public String toString() {
        return "NRectangle(" + x + ", " + y + ", " + w + ", " + h + ")";
    }

    public boolean contains(NPoint point) {
        return contains(point.x, point.y);
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x < this.x + w && y >= this.y && y < this.y + h;
    }

    public NRectangle intersect(NRectangle that) {
        if (that == null) return null;
        if (this.x + this.w < that.x || that.x + that.w < this.x || this.y + this.h < that.y || that.y + that.h < this.y)
            return null;
        return new NRectangle(
                new NPoint(Integer.max(this.x, that.x), Integer.max(this.y, that.y)),
                new NPoint(Integer.min(this.x + this.w, that.x + that.w), Integer.min(this.y + this.h, that.y + that.h))
        );
    }

    public NRectangle move(NPoint delta) {
        return new NRectangle(x + delta.x, y + delta.y, w, h);
    }

    public boolean equals(Object o) {
        if (!(o instanceof NRectangle)) return false;
        NRectangle rectangle = (NRectangle) o;
        return x == rectangle.x && y == rectangle.y && w == rectangle.w && h == rectangle.h;
    }
}
