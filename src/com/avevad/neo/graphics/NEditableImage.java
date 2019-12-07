package com.avevad.neo.graphics;

public abstract class NEditableImage extends NImage {

    public NEditableImage(int w, int h) {
        super(w, h);
    }

    public abstract void setPixel(int w, int h, int rgb);
}
