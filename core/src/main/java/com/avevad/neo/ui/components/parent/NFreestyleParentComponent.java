package com.avevad.neo.ui.components.parent;

import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NImage;
import com.avevad.neo.ui.NEventDispatcher;
import com.avevad.neo.ui.NParentComponent;
import com.avevad.neo.ui.NUI;
import com.avevad.neo.ui.events.*;

public class NFreestyleParentComponent extends NParentComponent {
    public final NImage canvas;
    private boolean keyboardNeeded = false;

    public final NEventDispatcher<NMousePressedEvent> mousePressed = new NEventDispatcher<>();
    public final NEventDispatcher<NMouseReleasedEvent> mouseReleased = new NEventDispatcher<>();
    public final NEventDispatcher<NMouseDraggedEvent> mouseDragged = new NEventDispatcher<>();
    public final NEventDispatcher<NMouseWheelScrolledEvent> mouseWheelScrolled = new NEventDispatcher<>();
    public final NEventDispatcher<NMouseMovedEvent> mouseMoved = new NEventDispatcher<>();
    public final NEventDispatcher<NKeyPressedEvent> keyPressed = new NEventDispatcher<>();
    public final NEventDispatcher<NKeyReleasedEvent> keyReleased = new NEventDispatcher<>();

    public NFreestyleParentComponent(NImage canvas) {
        this.canvas = canvas;
    }

    @Override
    public void setUI(NUI ui) {
        throw new UnsupportedOperationException("this component doesn't support custom UI");
    }

    @Override
    public boolean render(int layer) {
        NGraphics g = getParent().getGraphics();
        g.setOpacity(getOpacity());
        g.drawImage(canvas, getLocation(), getSize());
        return super.render(layer);
    }

    @Override
    public boolean onMousePressed(NMousePressedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMousePressed(event)) return true;
        if (event.x < 0 || event.x >= getWidth()) return false;
        if (event.y < 0 || event.y >= getHeight()) return false;
        mousePressed.trigger(event);
        return true;
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseReleased(event)) return true;
        if (event.x < 0 || event.x >= getWidth()) return false;
        if (event.y < 0 || event.y >= getHeight()) return false;
        mouseReleased.trigger(event);
        return true;
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseDragged(event)) return true;
        if (event.x < 0 || event.x >= getWidth()) return false;
        if (event.y < 0 || event.y >= getHeight()) return false;
        mouseDragged.trigger(event);
        return true;
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseWheelScrolled(event)) return true;
        if (event.x < 0 || event.x >= getWidth()) return false;
        if (event.y < 0 || event.y >= getHeight()) return false;
        mouseWheelScrolled.trigger(event);
        return true;
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseMoved(event)) return true;
        if (event.x < 0 || event.x >= getWidth()) return false;
        if (event.y < 0 || event.y >= getHeight()) return false;
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
