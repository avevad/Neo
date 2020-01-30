package com.avevad.neo.graphics;

import java.io.File;
import java.io.IOException;
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

    public abstract NImage copyReadonly(int x, int y, int w, int h);

    public final NImage copyReadonly() {
        return copyReadonly(0, 0, w, h);
    }

    public abstract NEditableImage copyEditable(int x, int y, int w, int h);

    public final NEditableImage copyEditable() {
        return copyEditable(0, 0, w, h);
    }

    public final NDimension getSize() {
        return new NDimension(w, h);
    }


    public interface NImageIO<I extends NImage> {
        I loadImage(InputStream from) throws IOException;

        void saveImage(I image, OutputStream to, String format) throws IOException;
    }
}
