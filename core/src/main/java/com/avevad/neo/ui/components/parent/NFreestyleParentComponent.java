package com.avevad.neo.ui.components.parent;

import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NEventDispatcher;
import com.avevad.neo.ui.NParentComponent;
import com.avevad.neo.ui.components.NFreestyleComponent;
import com.avevad.neo.ui.events.*;

public class NFreestyleParentComponent extends NParentComponent {
    private boolean keyboardNeeded = false;
    private final NFreestyleComponent.NFreestyleRenderer renderer;

    public final NEventDispatcher<NMousePressedEvent> mousePressed = new NEventDispatcher<>();
    public final NEventDispatcher<NMouseReleasedEvent> mouseReleased = new NEventDispatcher<>();
    public final NEventDispatcher<NMouseDraggedEvent> mouseDragged = new NEventDispatcher<>();
    public final NEventDispatcher<NMouseWheelScrolledEvent> mouseWheelScrolled = new NEventDispatcher<>();
    public final NEventDispatcher<NMouseMovedEvent> mouseMoved = new NEventDispatcher<>();
    public final NEventDispatcher<NKeyPressedEvent> keyPressed = new NEventDispatcher<>();
    public final NEventDispatcher<NKeyReleasedEvent> keyReleased = new NEventDispatcher<>();

    public NFreestyleParentComponent(NFreestyleComponent.NFreestyleRenderer renderer) {
        this.renderer = renderer;
    }


    @Override
    public boolean render(int layer, NRectangle area) {
        boolean ret = renderer.render(layer);
        if (!ret) super.render(layer, area);
        return ret;
    }

    @Override
    public boolean onMousePressed(NMousePressedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMousePressed(event)) return true;
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;
        mousePressed.trigger(event);
        return true;
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseReleased(event)) return true;
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;
        mouseReleased.trigger(event);
        return true;
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseDragged(event)) return true;
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;
        mouseDragged.trigger(event);
        return true;
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseWheelScrolled(event)) return true;
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;
        mouseWheelScrolled.trigger(event);
        return true;
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseMoved(event)) return true;
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;
        mouseMoved.trigger(event);
        return true;
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {
        if (!isEnabled()) return;
        if (getFocus() != null) super.onKeyPressed(event);
        else keyPressed.trigger(event);
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {
        if (!isEnabled()) return;
        if (getFocus() != null) super.onKeyReleased(event);
        else keyReleased.trigger(event);
    }

    public void setKeyboardNeeded(boolean keyboardNeeded) {
        this.keyboardNeeded = keyboardNeeded;
    }

    @Override
    public boolean isKeyboardNeeded() {
        if (getFocus() == null) return keyboardNeeded;
        else return super.isKeyboardNeeded();
    }
}
