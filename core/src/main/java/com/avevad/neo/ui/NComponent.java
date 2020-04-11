package com.avevad.neo.ui;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.events.*;

public abstract class NComponent {
    private NParentComponent parent;
    private NRectangle bounds;
    private NGraphics graphics;
    private int focusIndex = 0;
    private double opacity = 1;

    public NComponent() {
        this.bounds = new NRectangle(0, 0, 0, 0);
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
        return parent == null || (parent.getFocus() == this && parent.isFocused());
    }

    public int getFocusIndex() {
        return focusIndex;
    }

    public void setFocusIndex(int focusIndex) {
        this.focusIndex = focusIndex;
    }


    public final void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public final double getOpacity() {
        return opacity;
    }

    public abstract boolean render(int layer);


    public abstract boolean onMousePressed(NMousePressedEvent event);

    public abstract boolean onMouseReleased(NMouseReleasedEvent event);

    public abstract boolean onMouseDragged(NMouseDraggedEvent event);

    public abstract boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event);

    public abstract boolean onMouseMoved(NMouseMovedEvent event);

    public void onMouseExited() {

    }

    public abstract void onKeyPressed(NKeyPressedEvent event);

    public abstract void onKeyReleased(NKeyReleasedEvent event);


    public abstract boolean isKeyboardNeeded();


    private static final class NLinkedGraphics extends NGraphics {
        private final NComponent parent;
        private final NComponent child;
        private NRectangle lastBounds;
        private NGraphics lastGraphics;
        private NGraphics graphics;
        private int color;
        private double opacity = 1;
        private NFont font;
        private final NRectangle clip;
        private final NPoint offset;

        public NLinkedGraphics(NComponent parent, NComponent child, NRectangle clip, NPoint offset) {
            this.parent = parent;
            this.child = child;
            this.clip = clip;
            this.offset = offset;
            updateGraphics();
        }

        public NLinkedGraphics(NComponent parent, NComponent child) {
            this(parent, child, null, NPoint.ZERO);
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
            graphics.setOpacity(opacity);
            if (font != null) graphics.setFont(font);
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
            x1 += offset.x;
            y1 += offset.y;
            x2 += offset.x;
            y2 += offset.y;
            graphics.drawLine(x1, y1, x2, y2);
        }

        @Override
        public void drawRect(int x, int y, int w, int h) {
            updateGraphics();
            setParameters();
            x += offset.x;
            y += offset.y;
            graphics.drawRect(x, y, w, h);
        }

        @Override
        public void fillRect(int x, int y, int w, int h) {
            updateGraphics();
            setParameters();
            x += offset.x;
            y += offset.y;
            graphics.fillRect(x, y, w, h);
        }

        @Override
        public void drawOval(int x, int y, int w, int h) {
            updateGraphics();
            setParameters();
            x += offset.x;
            y += offset.y;
            graphics.drawOval(x, y, w, h);
        }

        @Override
        public void fillOval(int x, int y, int w, int h) {
            updateGraphics();
            setParameters();
            x += offset.x;
            y += offset.y;
            graphics.fillOval(x, y, w, h);
        }

        @Override
        public void drawPolygon(int[] xs, int[] ys) {
            updateGraphics();
            setParameters();
            int[] nxs = new int[xs.length];
            for(int i = 0; i < xs.length; i++) nxs[i] = xs[i] + offset.x;
            int[] nys = new int[ys.length];
            for(int i = 0; i < ys.length; i++) nys[i] = ys[i] + offset.y;
            graphics.drawPolygon(nxs, nys);
        }

        @Override
        public void fillPolygon(int[] xs, int[] ys) {
            updateGraphics();
            setParameters();
            int[] nxs = new int[xs.length];
            for(int i = 0; i < xs.length; i++) nxs[i] = xs[i] + offset.x;
            int[] nys = new int[ys.length];
            for(int i = 0; i < ys.length; i++) nys[i] = ys[i] + offset.y;
            graphics.fillPolygon(nxs, nys);
        }

        @Override
        public void drawString(String s, int x, int y) {
            updateGraphics();
            setParameters();
            x += offset.x;
            y += offset.y;
            graphics.drawString(s, x, y);
        }

        @Override
        public NGraphics create() {
            return new NLinkedGraphics(parent, child);
        }

        @Override
        public NGraphics create(int x, int y, int w, int h) {
            if (clip == null) return new NLinkedGraphics(parent, child, new NRectangle(x, y, w, h), offset);
            else {
                int offsetX = offset.x;
                int offsetY = offset.y;
                if(x < 0) offsetX += x;
                if(y < 0) offsetY += y;
                x += clip.x;
                y += clip.y;
                int x1 = Integer.max(x, clip.x);
                int y1 = Integer.max(y, clip.y);
                int x2 = Integer.min(x + w, clip.x + clip.w);
                int y2 = Integer.min(y + h, clip.y + clip.h);
                if (x2 < x1) x2 = x1;
                if (y2 < y1) y2 = y1;
                return new NLinkedGraphics(parent, child, new NRectangle(x1, y1, x2 - x1, y2 - y1), new NPoint(offsetX, offsetY));
            }
        }

        @Override
        public void drawImage(NImage img, int x, int y) {
            updateGraphics();
            setParameters();
            x += offset.x;
            y += offset.y;
            graphics.drawImage(img, x, y);
        }

        @Override
        public void drawImage(NImage img, int x, int y, int w, int h) {
            updateGraphics();
            setParameters();
            x += offset.x;
            y += offset.y;
            graphics.drawImage(img, x, y, w, h);
        }

        @Override
        public void drawImage(NImage img, int srcX, int srcY, int srcW, int srcH, int dstX, int dstY, int dstW, int dstH) {
            updateGraphics();
            setParameters();
            dstX += offset.x;
            dstY += offset.y;
            graphics.drawImage(img, srcX, srcY, srcW, srcH, dstX, dstY, dstW, dstH);
        }

        @Override
        public NFontMetrics getFontMetrics(NFont font) {
            updateGraphics();
            return graphics.getFontMetrics(font);
        }

        @Override
        public void rotate(int x, int y, double a) {
            updateGraphics();
            x += offset.x;
            y += offset.y;
            graphics.rotate(x, y, a);
        }
    }
}
