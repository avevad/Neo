package com.avevad.neo.ui;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class NParentComponent extends NComponent implements Iterable<NComponent> {
    private final Set<NComponent> children = new HashSet<>();

    public boolean hasChild(NComponent child) {
        return children.contains(child);
    }

    public final synchronized void addChild(NComponent child) {
        if (child == null) throw new IllegalArgumentException("child cannot be null");
        if (hasChild(child)) throw new IllegalArgumentException("already have this child");
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
