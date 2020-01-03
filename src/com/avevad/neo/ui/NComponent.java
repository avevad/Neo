package com.avevad.neo.ui;

import com.avevad.neo.graphics.*;

public abstract class NComponent implements Comparable<NComponent> {
    private NParentComponent parent;
    private NRectangle bounds;
    private NGraphics graphics;
    private int backgroundColor = 0xFFFFFF;
    private int foregroundColor = 0x000000;

    public NComponent(NRectangle bounds) {
        this.bounds = bounds;
    }

    public NComponent(int x, int y, int w, int h) {
        this.bounds = new NRectangle(x, y, w, h);
    }

    public synchronized void setParent(NParentComponent parent) {
        if (this.parent != null) throw new IllegalStateException("already have a parent");
        if (parent == null) throw new IllegalArgumentException("parent cannot be null");
        try {
            this.parent = parent;
            this.graphics = parent.getGraphics() == null ? null : new NLinkedGraphics(parent, this);
            if (!parent.hasChild(this)) parent.addChild(this);
        } catch (Exception ex) {
            this.parent = null;
            throw ex;
        }
    }

    public final NParentComponent getParent() {
        return parent;
    }

    public NGraphics getGraphics() {
        return graphics;
    }

    public final NRectangle getBounds() {
        return bounds;
    }

    public final void setBounds(NRectangle bounds) {
        this.bounds = bounds;
    }

    public final NDimension getSize() {
        return bounds.getSize();
    }

    public final int getWidth() {
        return bounds.w;
    }

    public final int getHeight() {
        return bounds.h;
    }

    public final void setSize(NDimension size) {
        bounds = new NRectangle(bounds.getPoint(), size);
    }

    public final NPoint getLocation() {
        return bounds.getPoint();
    }

    public final int getLocationX() {
        return bounds.x;
    }

    public final int getLocationY() {
        return bounds.y;
    }

    public final void setLocation(NPoint location) {
        bounds = new NRectangle(location, bounds.getSize());
    }

    public final void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public final int getBackgroundColor() {
        return backgroundColor;
    }

    public final void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public final int getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public int compareTo(NComponent other) {
        return getZIndex() - other.getZIndex();
    }

    private static final class NLinkedGraphics extends NGraphics {
        private final NComponent parent;
        private final NComponent child;
        private NRectangle lastBounds;
        private NGraphics lastGraphics;
        private NGraphics graphics;
        private int color;
        private NFont font;

        public NLinkedGraphics(NComponent parent, NComponent child) {
            this.parent = parent;
            this.child = child;
            updateGraphics();
        }

        private void updateGraphics() {
            if (lastBounds != child.getBounds() || lastGraphics != parent.getGraphics()) {
                graphics = parent.getGraphics() == null ? null : parent.getGraphics().create(child.getBounds());
                lastGraphics = parent.getGraphics();
                lastBounds = child.getBounds();
            }
        }

        private void setParameters() {
            graphics.setColor(color);
            if (font != null) graphics.setFont(font);
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
            this.font = font;
        }

        @Override
        public NFont getFont() {
            return font;
        }

        @Override
        public void drawLine(int x1, int y1, int x2, int y2) {
            updateGraphics();
            setParameters();
            graphics.drawLine(x1, y1, x2, y2);
        }

        @Override
        public void drawRect(int x, int y, int w, int h) {
            updateGraphics();
            setParameters();
            graphics.drawRect(x, y, w, h);
        }

        @Override
        public void fillRect(int x, int y, int w, int h) {
            updateGraphics();
            setParameters();
            graphics.fillRect(x, y, w, h);
        }

        @Override
        public void drawOval(int x, int y, int w, int h) {
            updateGraphics();
            setParameters();
            graphics.drawOval(x, y, w, h);
        }

        @Override
        public void fillOval(int x, int y, int w, int h) {
            updateGraphics();
            setParameters();
            graphics.fillOval(x, y, w, h);
        }

        @Override
        public void drawPolygon(int[] xs, int[] ys) {
            updateGraphics();
            setParameters();
            graphics.drawPolygon(xs, ys);
        }

        @Override
        public void fillPolygon(int[] xs, int[] ys) {
            updateGraphics();
            setParameters();
            graphics.fillPolygon(xs, ys);
        }

        @Override
        public void drawString(String s, int x, int y) {
            updateGraphics();
            setParameters();
            graphics.drawString(s, x, y);
        }

        @Override
        public NGraphics create() {
            throw new UnsupportedOperationException();
        }

        @Override
        public NGraphics create(int x, int y, int w, int h) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void drawImage(NImage img, int x, int y) {
            updateGraphics();
            setParameters();
            graphics.drawImage(img, x, y);
        }

        @Override
        public void drawImage(NImage img, int x, int y, int w, int h) {
            updateGraphics();
            setParameters();
            graphics.drawImage(img, x, y, w, h);
        }

        @Override
        public void drawImage(NImage img, int srcX, int srcY, int srcW, int srcH, int dstX, int dstY, int dstW, int dstH) {
            updateGraphics();
            setParameters();
            graphics.drawImage(img, srcX, srcY, srcW, srcH, dstX, dstY, dstW, dstH);
        }
    }

    public abstract boolean render(int layer);

    public abstract boolean onMousePressed(int x, int y, int button);

    public abstract boolean onMouseReleased(int x, int y, int button);

    public abstract boolean onMouseDragged(int x, int y, int button);

    public abstract boolean onMouseWheelScrolled(int x, int y, int value);

    public abstract boolean onMouseMoved(int x, int y);

    public abstract void onKeyPressed(int key);

    public abstract void onKeyReleased(int key);

    public abstract boolean isKeyboardNeeded();

    public abstract int getZIndex();
}
