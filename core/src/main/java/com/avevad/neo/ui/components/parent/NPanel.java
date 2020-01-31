package com.avevad.neo.ui.components.parent;

import com.avevad.neo.graphics.NColor;
import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NParentComponent;
import com.avevad.neo.ui.NUI;
import com.avevad.neo.ui.events.*;

public class NPanel extends NParentComponent {
    private int color = NColor.NONE;

    public NPanel() {
        setUI(new DefaultUI());
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

    private static final class DefaultUI implements NUI {
        public static final int DEFAULT_COLOR = NColor.WHITE;
        public static final int DISABLED_COVER_COLOR = NColor.WHITE;
        public static final double DISABLED_COVER_OPACITY = 0.5;

        @Override
        public boolean render(NComponent component, int layer) {
            if (!(component instanceof NPanel)) throw new IllegalArgumentException("This UI can only render NPanel");
            NPanel panel = (NPanel) component;
            NGraphics g = panel.getParent().getGraphics();
            if (layer == 0) {
                g.setOpacity(panel.getOpacity());
                g.setColor(panel.getColor());
                g.fillRect(panel.getBounds());
            }
            if (layer == 1 && !panel.isEnabled()) {
                g.setOpacity(DISABLED_COVER_OPACITY * panel.getOpacity());
                g.setColor(DISABLED_COVER_COLOR);
                g.fillRect(panel.getBounds());
            }
            return layer < 1;
        }
    }
}
