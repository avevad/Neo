package com.avevad.neo.ui;


import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.events.*;
import com.avevad.neo.util.NPair;

import java.util.*;

public abstract class NParentComponent extends NComponent implements Iterable<NComponent> {
    private final List<NComponent> children = new ArrayList<>();
    private NComponent focus;
    private boolean isMousePressed;
    private NComponent lastHoveredChild;

    private boolean enabled = true;

    public NParentComponent() {
        super();
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

    public NComponent getFocus() {
        return focus;
    }

    @Override
    public boolean render(int layer) {
        if (layer == 0) synchronized (children) {
            Queue<NPair<NComponent, Integer>> render = new LinkedList<>();
            for (NComponent child : children) render.add(new NPair<>(child, 0));
            while (!render.isEmpty()) {
                NPair<NComponent, Integer> child = render.poll();
                if (child.a.isVisible()) if (child.a.render(child.b))
                    render.add(new NPair<>(child.a, child.b + 1));
            }
        }
        return false;
    }

    @Override
    public final Iterator<NComponent> iterator() {
        return children.iterator();
    }

    @Override
    public boolean onMousePressed(NMousePressedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;
        isMousePressed = true;
        for (NComponent comp : this) {
            if (!comp.isVisible()) continue;
            if (comp.onMousePressed(new NMousePressedEvent(event.x - comp.getX(), event.y - comp.getY(), event.button))) {
                focus = comp;
                return true;
            }
        }
        focus = null;
        return false;
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y) && !isMousePressed) return false;
        isMousePressed = false;
        for (NComponent comp : this) {
            if (!comp.isVisible()) continue;
            if (comp.onMouseReleased(new NMouseReleasedEvent(event.x - comp.getX(), event.y - comp.getY(), event.button)))
                return true;
        }
        return false;
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y) && !isMousePressed) return false;
        for (NComponent comp : this) {
            if (!comp.isVisible()) continue;
            if (comp.onMouseDragged(new NMouseDraggedEvent(event.x - comp.getX(), event.y - comp.getY(), event.button))) {
                if (lastHoveredChild != null && lastHoveredChild != comp) lastHoveredChild.onMouseExited();
                lastHoveredChild = comp;
                return true;
            }
        }
        if (lastHoveredChild != null) lastHoveredChild.onMouseExited();
        lastHoveredChild = null;
        return false;
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;
        for (NComponent comp : this) {
            if (!comp.isVisible()) continue;
            if (comp.onMouseMoved(new NMouseMovedEvent(event.x - comp.getX(), event.y - comp.getY()))) {
                if (lastHoveredChild != null && lastHoveredChild != comp) lastHoveredChild.onMouseExited();
                lastHoveredChild = comp;
                return true;
            }
        }
        if (lastHoveredChild != null) lastHoveredChild.onMouseExited();
        lastHoveredChild = null;
        return false;
    }

    @Override
    public void onMouseExited() {
        if (lastHoveredChild != null) lastHoveredChild.onMouseExited();
        lastHoveredChild = null;
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;
        for (NComponent comp : this) {
            if (!comp.isVisible()) continue;
            if (comp.onMouseWheelScrolled(new NMouseWheelScrolledEvent(event.x - comp.getX(), event.y - comp.getY(), event.value)))
                return true;
        }
        return false;
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {
        focus.onKeyPressed(new NKeyPressedEvent(event.key, event.c));
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {
        if (focus != null) focus.onKeyReleased(new NKeyReleasedEvent(event.key, event.c));
    }

    @Override
    public boolean isKeyboardNeeded() {
        return focus != null && focus.isKeyboardNeeded();
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
