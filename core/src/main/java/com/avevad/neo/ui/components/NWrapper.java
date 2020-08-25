package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NParentComponent;
import com.avevad.neo.ui.components.parent.NPanel;
import com.avevad.neo.ui.events.*;

public class NWrapper extends NComponent {
    private final NComponent child;
    private final NParentComponent root = new NWrapperParent();

    public NWrapper(NComponent child) {
        this.child = child;
        root.addChild(child);
        root.setFocus(child);
        boundsChanged.addHandler(event -> root.setBounds(getBounds()));
    }

    @Override
    public boolean onMousePressed(NMousePressedEvent event) {
        return root.onMousePressed(event);
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        return root.onMouseReleased(event);
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        return root.onMouseDragged(event);
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        return root.onMouseWheelScrolled(event);
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        return root.onMouseMoved(event);
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {
        root.onKeyPressed(event);
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {
        root.onKeyReleased(event);
    }

    @Override
    public boolean isKeyboardNeeded() {
        return root.isKeyboardNeeded();
    }

    @Override
    public boolean render(int layer, NRectangle area) {
        return root.render(layer, area);
    }

    private final class NWrapperParent extends NParentComponent {

        @Override
        public NGraphics getGraphics() {
            return NWrapper.this.getGraphics();
        }

        @Override
        public boolean render(int layer, NRectangle area) {
            return child.render(layer, area.move(child.getLocation().negate()));
        }
    }
}
