package com.avevad.neo.graphics;

public abstract class NFontMetrics {
    public abstract int getAscent();

    public abstract int getDescent();

    public abstract int getLeading();

    public abstract int getWidth(String s);
}
