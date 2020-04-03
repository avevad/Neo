package com.avevad.neo.awt;

import com.avevad.neo.graphics.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class NAWTGraphics extends NGraphics {
    public static final Map<Object, Object> AWT_RENDERING_HINTS = new HashMap<>();

    public final Graphics2D graphics;
    private int color;
    private double opacity = 1;
    private NAWTFont font;

    public NAWTGraphics(Graphics2D graphics) {
        this.graphics = graphics;
        graphics.setRenderingHints(AWT_RENDERING_HINTS);
        this.font = new NAWTFont(new Font(Font.SERIF, Font.PLAIN, 14));
    }

    private void setAWTGraphicsProperties(){
        graphics.setFont(font.font);
        float red = NColor.red(color) / (float) 255;
        float green = NColor.green(color) / (float) 255;
        float blue = NColor.blue(color) / (float) 255;
        float alpha = (float) opacity;
        graphics.setColor(new Color(red, green, blue, alpha));
    }

    @Override
    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    @Override
    public double getOpacity() {
        return opacity;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void setFont(NFont font) {
        Objects.requireNonNull(font);
        if(!(font instanceof NAWTFont)) throw new IllegalArgumentException("Font must be an AWT Font");
        this.font = (NAWTFont) font;
    }

    @Override
    public NFont getFont() {
        return font;
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        setAWTGraphicsProperties();
        graphics.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawRect(int x, int y, int w, int h) {
        setAWTGraphicsProperties();
        graphics.drawRect(x, y, w, h);
    }

    @Override
    public void fillRect(int x, int y, int w, int h) {
        setAWTGraphicsProperties();
        graphics.fillRect(x, y, w, h);
    }

    @Override
    public void drawOval(int x, int y, int w, int h) {
        setAWTGraphicsProperties();
        graphics.drawOval(x, y, w, h);
    }

    @Override
    public void fillOval(int x, int y, int w, int h) {
        setAWTGraphicsProperties();
        graphics.fillOval(x, y, w, h);
    }

    @Override
    public void drawPolygon(int[] xs, int[] ys) {
        if(xs.length != ys.length) throw new IllegalArgumentException("Coordinate arrays must have the same length");
        setAWTGraphicsProperties();
        graphics.drawPolygon(xs, ys, xs.length);
    }

    @Override
    public void fillPolygon(int[] xs, int[] ys) {
        if(xs.length != ys.length) throw new IllegalArgumentException("Coordinate arrays must have the same length");
        setAWTGraphicsProperties();
        graphics.fillPolygon(xs, ys, xs.length);
    }

    @Override
    public void drawString(String s, int x, int y) {
        setAWTGraphicsProperties();
        graphics.drawString(s, x, y);
    }

    @Override
    public NGraphics create() {
        return new NAWTGraphics((Graphics2D) graphics.create());
    }

    @Override
    public NGraphics create(int x, int y, int w, int h) {
        return new NAWTGraphics((Graphics2D) graphics.create(x, y, w, h));
    }

    @Override
    public void drawImage(NImage img, int x, int y) {
        if(img instanceof NAWTImage) graphics.drawImage(((NAWTImage) img).img, x, y, null);
        else if(img instanceof NAWTReadonlyImage) {
            NAWTReadonlyImage readonlyImage = (NAWTReadonlyImage) img;
            graphics.drawImage(readonlyImage.img.img,
                    x, y, x + readonlyImage.w, y + readonlyImage.h,
                    readonlyImage.sx, readonlyImage.sy, readonlyImage.sx + readonlyImage.w, readonlyImage.sy + readonlyImage.h,
                    null);
        }
        else drawImage(img, 0, 0, img.w, img.h, x, y, img.w, img.h);
    }

    @Override
    public void drawImage(NImage img, int x, int y, int w, int h) {
        if(img instanceof NAWTImage) graphics.drawImage(((NAWTImage) img).img, x, y, w, h, null);
        else if(img instanceof NAWTReadonlyImage) {
            NAWTReadonlyImage readonlyImage = (NAWTReadonlyImage) img;
            graphics.drawImage(readonlyImage.img.img,
                    x, y, x + w, y + h,
                    readonlyImage.sx, readonlyImage.sy, readonlyImage.sx + readonlyImage.w, readonlyImage.sy + readonlyImage.h,
                    null);
        }
        else drawImage(img, 0, 0, img.w, img.h, x, y, w, h);
    }

    @Override
    public void drawImage(NImage img, int srcX, int srcY, int srcW, int srcH, int dstX, int dstY, int dstW, int dstH) {
        if(img instanceof NAWTImage) graphics.drawImage(((NAWTImage) img).img, srcX, srcY, srcW, srcH, dstX, dstY, dstW, dstH, null);
        else if(img instanceof NAWTReadonlyImage) {
            NAWTReadonlyImage readonlyImage = (NAWTReadonlyImage) img;
            graphics.drawImage(readonlyImage.img.img,
                    dstX, dstY, dstX + dstW, dstY + dstH,
                    readonlyImage.sx + srcX, readonlyImage.sy + srcY, readonlyImage.sx + srcX + srcW, readonlyImage.sy + srcY + srcH,
                    null);
        }
        else {
            double kx = (double) srcW / dstW;
            double ky = (double) srcH / dstH;
            int pw = (int) Math.ceil(kx);
            int ph = (int) Math.ceil(ky);
            for(int sx = srcX; sx < srcX + srcW; sx++){
                for(int sy = srcY; sy < srcY + srcH; sy++){
                    int dx = (int) (dstX + (sx - srcX) * kx);
                    int dy = (int) (dstY + (sy - srcY) * ky);
                    int px = img.getPixel(sx, sy);
                    graphics.setColor(new Color(px));
                    graphics.fillRect(dx, dy, pw, ph);
                }
            }
        }
    }

    @Override
    public NFontMetrics getFontMetrics(NFont font) {
        if(!(font instanceof NAWTFont)) throw new IllegalArgumentException("Font must be an AWT font");
        return new NAWTFontMetrics(graphics.getFontMetrics(((NAWTFont) font).font));
    }

    @Override
    public void rotate(int x, int y, double a) {
        graphics.rotate(a, x, y);
    }
}
