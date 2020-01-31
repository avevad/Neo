package com.avevad.neo.graphics;

public final class NColor {
    private NColor() {
    }

    public static final int NONE = -1;

    public static final int BLACK = 0x000000;
    public static final int BLUE = 0x0000FF;
    public static final int GREEN = 0x00FF00;
    public static final int CYAN = 0x00FFFF;
    public static final int RED = 0xFF0000;
    public static final int MAGENTA = 0xFF00FF;
    public static final int YELLOW = 0xFFFF00;
    public static final int WHITE = 0xFFFFFF;

    public static int red(int color) {
        return (color & RED) >> 16;
    }

    public static int green(int color) {
        return (color & GREEN) >> 8;
    }

    public static int blue(int color) {
        return color & BLUE;
    }

    public static int rgb(int r, int g, int b) {
        return (r << 16) + (g << 8) + b;
    }


    public static int mix(int first, int second, double firstPart, double secondPart) {
        int r = (int) (red(first) * firstPart + red(second) * secondPart);
        int g = (int) (green(first) * firstPart + green(second) * secondPart);
        int b = (int) (blue(first) * firstPart + blue(second) * secondPart);
        return rgb(r, g, b);
    }

    public static int mix(int first, int second, double firstPart) {
        return mix(first, second, firstPart, 1 - firstPart);
    }

    public static int mix(int first, int second) {
        return mix(first, second, 0.5, 0.5);
    }

    public static int negative(int color) {
        return rgb(0xFF - red(color), 0xFF - green(color), 0xFF - blue(color));
    }
}
