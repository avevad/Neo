package com.avevad.neo.ui;


import com.avevad.neo.graphics.NRectangle;

import java.util.*;

public abstract class NParentComponent extends NComponent implements Iterable<NComponent> {
    private final List<NComponent> children = new ArrayList<>();

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

    @Override
    public void onMousePressed(int x, int y, int button) {
        for (NComponent comp : this) {
            comp.onMousePressed(x - comp.getLocationX(), y - comp.getLocationY(), button);
        }
    }

    @Override
    public void onMouseReleased(int x, int y, int button) {
        for (NComponent comp : this) {
            comp.onMouseReleased(x - comp.getLocationX(), y - comp.getLocationY(), button);
        }
    }

    @Override
    public void onMouseDragged(int x, int y, int button) {
        for (NComponent comp : this) {
            comp.onMouseDragged(x - comp.getLocationX(), y - comp.getLocationY(), button);
        }
    }

    @Override
    public void onMouseMoved(int x, int y) {
        for (NComponent comp : this) {
            comp.onMouseMoved(x - comp.getLocationX(), y - comp.getLocationY());
        }
    }

}
