package com.avevad.neo.awt;

import com.avevad.neo.graphics.NFontMetrics;

import java.awt.*;

public final class NAWTFontMetrics extends NFontMetrics {
    public final FontMetrics fontMetrics;

    public NAWTFontMetrics(FontMetrics fontMetrics) {
        this.fontMetrics = fontMetrics;
    }


    @Override
    public int getAscent() {
        return fontMetrics.getAscent();
    }

    @Override
    public int getDescent() {
        return fontMetrics.getDescent();
    }

    @Override
    public int getLeading() {
        return fontMetrics.getLeading();
    }

    @Override
    public int getWidth(String s) {
        return fontMetrics.stringWidth(s);
    }
}
