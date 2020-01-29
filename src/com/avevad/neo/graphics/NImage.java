package com.avevad.neo.graphics;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class NImage {
    public final int w, h;

    public NImage(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public abstract int getPixel(int x, int y);

    public abstract void draw(NGraphics g, int x, int y);

    public abstract void draw(NGraphics g, int x, int y, int w, int h);

    public abstract void draw(NGraphics g, int srcX, int srcY, int srcW, int srcH, int dstX, int dstY, int dstW, int dstH);

    public final NDimension getSize() {
        return new NDimension(w, h);
    }


    public interface NImageIO<I extends NImage> {
        I loadImage(InputStream from, String format);

        void saveImage(I image, OutputStream to, String format);
    }
}
