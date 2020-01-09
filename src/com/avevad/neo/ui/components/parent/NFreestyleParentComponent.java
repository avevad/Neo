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
        g.drawImage(canvas, getLocation(), getSize());
        return super.render(layer);
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        if (super.onMousePressed(x, y, button)) return true;
        if (x < 0 || x >= getWidth()) return false;
        if (y < 0 || y >= getHeight()) return false;
        mousePressed.trigger(new NMousePressedEvent(x, y, button));
        return true;
    }

    @Override
    public boolean onMouseReleased(int x, int y, int button) {
        if (super.onMouseReleased(x, y, button)) return true;
        if (x < 0 || x >= getWidth()) return false;
        if (y < 0 || y >= getHeight()) return false;
        mouseReleased.trigger(new NMouseReleasedEvent(x, y, button));
        return true;
    }

    @Override
    public boolean onMouseDragged(int x, int y, int button) {
        if (super.onMouseDragged(x, y, button)) return true;
        if (x < 0 || x >= getWidth()) return false;
        if (y < 0 || y >= getHeight()) return false;
        mouseDragged.trigger(new NMouseDraggedEvent(x, y, button));
        return true;
    }

    @Override
    public boolean onMouseWheelScrolled(int x, int y, int value) {
        if (super.onMouseWheelScrolled(x, y, value)) return true;
        if (x < 0 || x >= getWidth()) return false;
        if (y < 0 || y >= getHeight()) return false;
        mouseWheelScrolled.trigger(new NMouseWheelScrolledEvent(x, y, value));
        return true;
    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        if (super.onMouseMoved(x, y)) return true;
        if (x < 0 || x >= getWidth()) return false;
        if (y < 0 || y >= getHeight()) return false;
        mouseMoved.trigger(new NMouseMovedEvent(x, y));
        return true;
    }

    @Override
    public void onKeyPressed(int key) {
        if (getFocus() != null) super.onKeyPressed(key);
        else keyPressed.trigger(new NKeyPressedEvent(key));
    }

    @Override
    public void onKeyReleased(int key) {
        if (getFocus() != null) super.onKeyReleased(key);
        else keyReleased.trigger(new NKeyReleasedEvent(key));
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
