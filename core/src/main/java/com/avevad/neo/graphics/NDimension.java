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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NDimension that = (NDimension) o;

        if (w != that.w) return false;
        return h == that.h;
    }
}
