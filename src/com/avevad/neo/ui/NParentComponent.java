package com.avevad.neo.ui;


import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.events.*;

import java.util.*;

public abstract class NParentComponent extends NComponent implements Iterable<NComponent> {
    private final List<NComponent> children = new ArrayList<>();
    private NComponent focus;
    private boolean isMousePressed;
    private NComponent lastHoveredChild;

    public final Set<NKeyEvent.NKey> focusMovementKeys = new HashSet<>();
    public final Set<NKeyEvent.NKey> pressedFocusMovementKeys = new HashSet<>();
    public final Set<NKeyEvent.NKey> reverseFocusMovementKeys = new HashSet<>();
    private final Set<NKeyEvent.NKey> pressedReverseFocusMovementKeys = new HashSet<>();

    public NParentComponent() {
        super();
        focusMovementKeys.add(NKeyEvent.NKey.TAB);
        reverseFocusMovementKeys.add(NKeyEvent.NKey.SHIFT);
        reverseFocusMovementKeys.add(NKeyEvent.NKey.TAB);
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

    public final NComponent getNextFocusable() {
        NComponent min = null;
        if (focus == null) {
            for (NComponent component : this) {
                if (component.getFocusIndex() < 0) continue;
                if (min == null) {
                    min = component;
                    continue;
                }
                if (component.getFocusIndex() > min.getFocusIndex()) continue;
                if (component.getFocusIndex() < min.getFocusIndex() || component.hashCode() < min.hashCode())
                    min = component;
            }
            return min;
        }
        for (NComponent component : this) {
            if (component.getFocusIndex() < 0) continue;
            if (component.getFocusIndex() < focus.getFocusIndex()) continue;
            if (component.getFocusIndex() > focus.getFocusIndex() || component.hashCode() > focus.hashCode())
                return component;
        }
        return null;
    }

    public final NComponent getPrevFocusable() {
        NComponent max = null;
        if (focus == null) {
            for (NComponent component : this) {
                if (component.getFocusIndex() < 0) continue;
                if (max == null) {
                    max = component;
                    continue;
                }
                if (component.getFocusIndex() < max.getFocusIndex()) continue;
                if (component.getFocusIndex() > max.getFocusIndex() || component.hashCode() > max.hashCode())
                    max = component;
            }
            return max;
        }
        for (NComponent component : this) {
            if (component.getFocusIndex() < 0) continue;
            if (component.getFocusIndex() > focus.getFocusIndex()) continue;
            if (component.getFocusIndex() < focus.getFocusIndex() || component.hashCode() < focus.hashCode())
                return component;
        }
        return null;
    }

    public boolean moveFocusForward() {
        if (focus instanceof NParentComponent) {
            NParentComponent pfocus = (NParentComponent) focus;
            if (pfocus.moveFocusForward()) focus = getNextFocusable();
        } else {
            focus = getNextFocusable();
        }
        return focus == null;
    }

    public boolean moveFocusBackward() {
        if (focus instanceof NParentComponent) {
            NParentComponent pfocus = (NParentComponent) focus;
            if (pfocus.moveFocusBackward()) focus = getPrevFocusable();
        } else {
            focus = getPrevFocusable();
        }
        return focus == null;
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
    public boolean onMousePressed(NMousePressedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;
        isMousePressed = true;
        for (NComponent comp : this) {
            if (comp.onMousePressed(new NMousePressedEvent(event.x - comp.getX(), event.y - comp.getY(), event.button))) {
                setFocus(comp);
                return true;
            }
        }
        setFocus(null);
        return false;
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y) && !isMousePressed) return false;
        isMousePressed = false;
        for (NComponent comp : this) {
            if (comp.onMouseReleased(new NMouseReleasedEvent(event.x - comp.getX(), event.y - comp.getY(), event.button)))
                return true;
        }
        return false;
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y) && !isMousePressed) return false;
        for (NComponent comp : this) {
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
            if (comp.onMouseWheelScrolled(new NMouseWheelScrolledEvent(event.x - comp.getX(), event.y - comp.getY(), event.value)))
                return true;
        }
        return false;
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {
        if (reverseFocusMovementKeys.contains(event.key)) {
            pressedReverseFocusMovementKeys.add(event.key);
            System.out.println("added reverse");
        }
        if (focusMovementKeys.contains(event.key)) {
            pressedFocusMovementKeys.add(event.key);
            System.out.println("added straight");
        }
        if (pressedReverseFocusMovementKeys.size() == reverseFocusMovementKeys.size()) {
            moveFocusBackward();
        } else if (pressedFocusMovementKeys.size() == focusMovementKeys.size()) {
            moveFocusForward();
        } else if (focus != null) focus.onKeyPressed(new NKeyPressedEvent(event.key, event.c));
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {
        pressedReverseFocusMovementKeys.remove(event.key);
        pressedFocusMovementKeys.remove(event.key);
        if (focus != null) focus.onKeyReleased(new NKeyReleasedEvent(event.key, event.c));
    }

    @Override
    public boolean isKeyboardNeeded() {
        return focus != null && focus.isKeyboardNeeded();
    }
}
