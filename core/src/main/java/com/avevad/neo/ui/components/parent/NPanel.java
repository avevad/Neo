package com.avevad.neo.ui.components.parent;

import com.avevad.neo.graphics.NColor;
import com.avevad.neo.graphics.NRectangle;
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
        return super.onMousePressed(event);
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!isEnabled()) return false;
        return super.onMouseReleased(event);
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        if (!isEnabled()) return false;
        return super.onMouseDragged(event);
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        if (!isEnabled()) return false;
        return super.onMouseMoved(event);
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        if (!isEnabled()) return false;
        return super.onMouseWheelScrolled(event);
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
    public boolean render(int layer, NRectangle area) {
        boolean ret = ui.drawPanel(this, layer, area);
        if (layer == 0) super.render(layer, area);
        return ret;
    }

    public interface NPanelUI {
        boolean drawPanel(NPanel panel, int layer, NRectangle area);
    }
}
