package com.avevad.neo.awt;

import com.avevad.neo.graphics.NEditableImage;
import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class NAWTImage extends NEditableImage {
    public static final NImageIO<NAWTImage> AWT_IMAGE_IO = new NImageIO<NAWTImage>() {
        @Override
        public NAWTImage loadImage(InputStream from) throws IOException {
            return new NAWTImage(ImageIO.read(from));
        }

        @Override
        public void saveImage(NAWTImage image, OutputStream to, String format) throws IOException{
            ImageIO.write(image.img, format, to);
        }
    };
    public final BufferedImage img;

    public NAWTImage(BufferedImage img) {
        super(img.getWidth(), img.getHeight());
        this.img = img;
    }

    @Override
    public NImage copyReadonly(int x, int y, int w, int h) {
        BufferedImage newImg = new BufferedImage(w, h, img.getType());
        newImg.getGraphics().drawImage(img, 0, 0, w, h, x, y, x + w, y + h, null);
        return new NAWTReadonlyImage(newImg);
    }

    @Override
    public NEditableImage copyEditable(int x, int y, int w, int h) {
        BufferedImage newImg = new BufferedImage(w, h, img.getType());
        newImg.getGraphics().drawImage(img, 0, 0, w, h, x, y, x + w, y + h, null);
        return new NAWTImage(newImg);
    }

    @Override
    public void setPixel(int x, int y, int rgb) {
        img.setRGB(x, y, rgb);
    }

    @Override
    public NGraphics getGraphics() {
        return new NAWTGraphics(img.getGraphics());
    }

    @Override
    public int getPixel(int x, int y) {
        return img.getRGB(x, y);
    }
}
