package com.avevad.neo.ui;


import com.avevad.neo.graphics.NRectangle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class NParentComponent extends NComponent implements Iterable<NComponent> {
    private final Set<NComponent> children = new HashSet<>();

    public NParentComponent(NRectangle bounds) {
        super(bounds);
    }

    public NParentComponent(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public boolean hasChild(NComponent child) {
        return children.contains(child);
    }

    public final synchronized void addChild(NComponent child) {
        if (child == null) throw new IllegalArgumentException("child cannot be null");
        if (hasChild(child)) throw new IllegalArgumentException("already have this child");
        NComponent component = this;
        while (component != null) {
            if (component == child) throw new IllegalArgumentException("ancestor of the component cannot be its child");
            component = component.getParent();
        }
        try {
            children.add(child);
            if (child.getParent() != this) child.setParent(this);
        } catch (Exception ex) {
            children.remove(child);
            throw ex;
        }
    }

    @Override
    public final Iterator<NComponent> iterator() {
        return children.iterator();
    }
}
