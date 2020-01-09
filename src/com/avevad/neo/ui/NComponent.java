package com.avevad.neo.ui;

import com.avevad.neo.graphics.*;

public abstract class NComponent {
    private NParentComponent parent;
    private NRectangle bounds;
    private NGraphics graphics;
    private NUI ui;

    public NComponent(NUI ui, NRectangle bounds) {
        this.ui = ui;
        this.bounds = bounds;
    }

    public NComponent(NUI ui, int x, int y, int w, int h) {
        this.ui = ui;
        this.bounds = new NRectangle(x, y, w, h);
    }

    public synchronized void setParent(NParentComponent parent) {
        if (this.parent != null) throw new IllegalStateException("already have a parent");
        if (parent == null) throw new IllegalArgumentException("parent cannot be null");
        try {
            this.parent = parent;
            this.graphics = new NLinkedGraphics(parent, this);
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


    public void setUi(NUI ui) {
        this.ui = ui;
    }

    public final NUI getUi() {
        return ui;
    }


    public final void setBounds(NRectangle bounds) {
        this.bounds = bounds;
    }

    public final void setBounds(NPoint location, NDimension size) {
        setBounds(new NRectangle(location, size));
    }

    public final void setBounds(int x, int y, int w, int h) {
        setBounds(new NRectangle(x, y, w, h));
    }

    public final NRectangle getBounds() {
        return bounds;
    }


    public final void setLocation(NPoint location) {
        setBounds(location, bounds.getSize());
    }

    public final void setLocation(int x, int y) {
        setBounds(x, y, bounds.w, bounds.h);
    }

    public final NPoint getLocation() {
        return bounds.getPoint();
    }


    public final void setSize(NDimension size) {
        setBounds(bounds.getPoint(), size);
    }

    public final void setSize(int w, int h) {
        setBounds(bounds.x, bounds.y, w, h);
    }

    public final NDimension getSize() {
        return bounds.getSize();
    }


    public final void setWidth(int w) {
        setBounds(bounds.x, bounds.y, w, bounds.h);
    }

    public final int getWidth() {
        return bounds.w;
    }


    public final void setHeight(int h) {
        setBounds(bounds.x, bounds.y, bounds.w, h);
    }

    public final int getHeight() {
        return bounds.h;
    }


    public final void setX(int x) {
        setBounds(x, bounds.y, bounds.w, bounds.h);
    }

    public final int getX() {
        return bounds.x;
    }


    public final void setY(int y) {
        setBounds(bounds.x, y, bounds.w, bounds.h);
    }

    public final int getY() {
        return bounds.y;
    }


    public final boolean isFocused() {
        return parent != null && parent.getFocus() == this;
    }


    public boolean render(int layer) {
        if (ui != null) return ui.render(this, layer);
        else return false;
    }


    public abstract boolean onMousePressed(int x, int y, int button);

    public abstract boolean onMouseReleased(int x, int y, int button);

    public abstract boolean onMouseDragged(int x, int y, int button);

    public abstract boolean onMouseWheelScrolled(int x, int y, int value);

    public abstract boolean onMouseMoved(int x, int y);

    public abstract void onKeyPressed(int key);

    public abstract void onKeyReleased(int key);


    public abstract boolean isKeyboardNeeded();


    private static final class NLinkedGraphics extends NGraphics {
        private final NComponent parent;
        private final NComponent child;
        private NRectangle lastBounds;
        private NGraphics lastGraphics;
        private NGraphics graphics;
        private int color;
        private NFont font;
        private final NRectangle clip;

        public NLinkedGraphics(NComponent parent, NComponent child, NRectangle clip) {
            this.parent = parent;
            this.child = child;
            this.clip = clip;
            updateGraphics();
        }

        public NLinkedGraphics(NComponent parent, NComponent child) {
            this(parent, child, null);
        }

        private void updateGraphics() {
            if (lastBounds != child.getBounds() || lastGraphics != parent.getGraphics()) {
                graphics = parent.getGraphics() == null ? null : parent.getGraphics().create(child.getBounds());
                if (clip != null && graphics != null) graphics = graphics.create(clip);
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
            return new NLinkedGraphics(parent, child);
        }

        @Override
        public NGraphics create(int x, int y, int w, int h) {
            if (clip == null) return new NLinkedGraphics(parent, child, new NRectangle(x, y, w, h));
            else {
                x += clip.x;
                y += clip.y;
                int x1 = Integer.max(x, clip.x);
                int y1 = Integer.max(y, clip.y);
                int x2 = Integer.min(x + w, clip.x + clip.w);
                int y2 = Integer.min(y + h, clip.y + clip.h);
                if (x2 < x1) x2 = x1;
                if (y2 < y1) y2 = y1;
                return new NLinkedGraphics(parent, child, new NRectangle(x1, y1, x2 - x1, y2 - y1));
            }
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
}
