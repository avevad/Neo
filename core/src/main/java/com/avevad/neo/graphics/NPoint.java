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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NPoint point = (NPoint) o;

        if (x != point.x) return false;
        return y == point.y;
    }

    public NPoint negate() {
        return new NPoint(-x, -y);
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
