package com.avevad.neo.ui;

import com.avevad.neo.graphics.NGraphics;

public abstract class NComponent {
    private NParentComponent parent;

    public NComponent() {

    }

    public NComponent(NParentComponent parent) {
        this();
        setParent(parent);
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
}
