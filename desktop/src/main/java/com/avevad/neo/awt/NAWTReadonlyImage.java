package com.avevad.neo.awt;

import com.avevad.neo.graphics.NEditableImage;
import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class NAWTReadonlyImage extends NImage {
    public static final NImageIO<NAWTReadonlyImage> AWT_READONLY_IMAGE_IO = new NImageIO<NAWTReadonlyImage>() {
        @Override
        public NAWTReadonlyImage newImage(int w, int h) {
            return new NAWTReadonlyImage(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
        }

        @Override
        public NAWTReadonlyImage loadImage(InputStream from) throws IOException{
            return new NAWTReadonlyImage(ImageIO.read(from));
        }

        @Override
        public void saveImage(NAWTReadonlyImage image, OutputStream to, String format) throws IOException{
            ImageIO.write(image.img.img, format, to);
        }
    };

    final NAWTImage img;

    public NAWTReadonlyImage(BufferedImage img){
        super(img.getWidth(), img.getHeight());
        this.img = new NAWTImage(img);
    }

    @Override
    public int getPixel(int x, int y) {
        return img.getPixel(x, y);
    }

    @Override
    public NImage copyReadonly(int x, int y, int w, int h) {
        return img.copyReadonly(x, y, w, h);
    }

    @Override
    public NEditableImage copyEditable(int x, int y, int w, int h) {
        return img.copyEditable();
    }


}
