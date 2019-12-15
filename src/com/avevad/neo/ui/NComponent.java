package com.avevad.neo.ui;

import com.avevad.neo.graphics.NDimension;
import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;

public abstract class NComponent {
    private NParentComponent parent;
    private NRectangle bounds;

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
            if (!parent.hasChild(this)) parent.addChild(this);
        } catch (Exception ex) {
            this.parent = null;
            throw ex;
        }
    }

    public final NParentComponent getParent() {
        return parent;
    }

    public abstract boolean render(NGraphics g, int layer);

    public final NRectangle getBounds() {
        return bounds;
    }

    public final void setBounds(NRectangle bounds) {
        this.bounds = bounds;
    }

    public final NDimension getSize() {
        return bounds.getSize();
    }

    public final void setSize(NDimension size) {
        bounds = new NRectangle(bounds.getPoint(), size);
    }

    public final NPoint getLocation() {
        return bounds.getPoint();
    }

    public final void setLocation(NPoint location) {
        bounds = new NRectangle(location, bounds.getSize());
    }
}
