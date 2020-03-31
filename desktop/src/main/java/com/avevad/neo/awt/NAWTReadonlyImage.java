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
    final int sx;
    final int sy;
    final int sw;
    final int sh;

    public NAWTReadonlyImage(BufferedImage img){
        this(img, 0, 0, img.getWidth(), img.getHeight());
    }

    public NAWTReadonlyImage(BufferedImage img, int sx, int sy, int sw, int sh) {
        super(sw, sh);
        this.img = new NAWTImage(img);
        this.sx = sx;
        this.sy = sy;
        this.sw = sw;
        this.sh = sh;
    }

    @Override
    public int getPixel(int x, int y) {
        return img.getPixel(sx + x, sy + y);
    }

    @Override
    public NImage copyReadonly(int x, int y, int w, int h) {
        return new NAWTReadonlyImage(img.img, sx + x, sy + y, w, h);
    }

    @Override
    public NEditableImage copyEditable(int x, int y, int w, int h) {
        BufferedImage newImg = new BufferedImage(w, h, img.img.getType());
        newImg.getGraphics().drawImage(img.img, sx + x, sy + y, w, h, null);
        return new NAWTImage(newImg);
    }


}
