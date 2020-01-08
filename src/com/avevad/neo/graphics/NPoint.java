package com.avevad.neo.graphics;

public final class NPoint {
    public final int x, y;

    public NPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "NPoint(" + x + ", " + y + ")";
    }

    public static final NPoint ZERO = new NPoint(0, 0);
}
