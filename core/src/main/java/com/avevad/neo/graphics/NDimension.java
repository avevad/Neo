package com.avevad.neo.graphics;

public final class NDimension {
    public final int w, h;

    public NDimension(int w, int h) {
        this.w = w;
        this.h = h;
    }

    @Override
    public String toString() {
        return "NDimension(" + w + ", " + h + ")";
    }
}
