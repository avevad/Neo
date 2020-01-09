package com.avevad.neo.ui;


import com.avevad.neo.graphics.NRectangle;

import java.util.*;

public abstract class NParentComponent extends NComponent implements Iterable<NComponent> {
    private final List<NComponent> children = new ArrayList<>();
    private NComponent focus;

    public NParentComponent(NUI ui, NRectangle bounds) {
        super(ui, bounds);
    }

    public NParentComponent(NUI ui, int x, int y, int w, int h) {
        super(ui, x, y, w, h);
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

    public final void setFocus(NComponent focus) {
        if (focus != null && !hasChild(focus)) throw new IllegalArgumentException("focus must be a child");
        this.focus = focus;
    }

    public final NComponent getFocus() {
        return focus;
    }

    @Override
    public boolean render(int layer) {
        boolean ret = super.render(layer);
        if (layer == 0) synchronized (children) {
            List<NComponent> zSort = new ArrayList<>();
            List<NComponent> render = new ArrayList<>(children);
            List<NComponent> render2 = new ArrayList<>();
            int l = 0;
            while (!render.isEmpty()) {
                for (NComponent child : render) {
                    if (child.render(l)) render2.add(child);
                }
                zSort.addAll(render);
                zSort.removeAll(render2);
                render.clear();
                render.addAll(render2);
                render2.clear();
                l++;
            }
            children.clear();
            children.addAll(zSort);
        }
        return ret;
    }

    @Override
    public final Iterator<NComponent> iterator() {
        return children.iterator();
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        for (NComponent comp : this) {
            if (comp.onMousePressed(x - comp.getX(), y - comp.getY(), button)) {
                setFocus(comp);
                return true;
            }
        }
        setFocus(null);
        return false;
    }

    @Override
    public boolean onMouseReleased(int x, int y, int button) {
        for (NComponent comp : this) {
            if (comp.onMouseReleased(x - comp.getX(), y - comp.getY(), button)) return true;
        }
        return false;
    }

    @Override
    public boolean onMouseDragged(int x, int y, int button) {
        for (NComponent comp : this) {
            if (comp.onMouseDragged(x - comp.getX(), y - comp.getY(), button)) return true;
        }
        return false;
    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        for (NComponent comp : this) {
            if (comp.onMouseMoved(x - comp.getX(), y - comp.getY())) return true;
        }
        return false;
    }

    @Override
    public boolean onMouseWheelScrolled(int x, int y, int value) {
        for (NComponent comp : this) {
            if (comp.onMouseWheelScrolled(x - comp.getX(), y - comp.getY(), value)) return true;
        }
        return false;
    }

    @Override
    public void onKeyPressed(int key) {
        if (focus != null) focus.onKeyPressed(key);
    }

    @Override
    public void onKeyReleased(int key) {
        if (focus != null) focus.onKeyReleased(key);
    }

    @Override
    public boolean isKeyboardNeeded() {
        return focus != null && focus.isKeyboardNeeded();
    }
}
