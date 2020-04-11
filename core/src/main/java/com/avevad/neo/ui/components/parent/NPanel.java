package com.avevad.neo.ui.components.parent;

import com.avevad.neo.graphics.NColor;
import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NParentComponent;
import com.avevad.neo.ui.events.*;

public class NPanel extends NParentComponent {
    private int color = NColor.NONE;
    private NPanelUI ui;

    public void setUI(NPanelUI ui) {
        this.ui = ui;
    }

    public NPanelUI getUI() {
        return ui;
    }

    public final void setColor(int color) {
        this.color = color;
    }

    public final int getColor() {
        return color;
    }

    @Override
    public boolean onMousePressed(NMousePressedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMousePressed(event)) return true;
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseReleased(event)) return true;
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseDragged(event)) return true;
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseMoved(event)) return true;
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        if (!isEnabled()) return false;
        if (super.onMouseWheelScrolled(event)) return true;
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public void onMouseExited() {
        if (!isEnabled()) return;
        super.onMouseExited();
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {
        if (!isEnabled()) return;
        super.onKeyPressed(event);
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {
        if (!isEnabled()) return;
        super.onKeyReleased(event);
    }

    @Override
    public boolean render(int layer) {
        boolean ret = ui.drawPanel(this, layer);
        super.render(layer);
        return ret;
    }

    public interface NPanelUI {
        boolean drawPanel(NPanel panel, int layer);
    }
}
