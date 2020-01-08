package com.avevad.neo.ui.components.parent;

import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NParentComponent;

public class NPanel extends NParentComponent {
    public NPanel(NRectangle bounds) {
        super(bounds);
    }

    public NPanel(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Override
    public boolean render(int layer) {
        NGraphics g = getParent().getGraphics();
        if (layer == 0) {
            g.setColor(getBackgroundColor());
            g.fillRect(getLocationX(), getLocationY(), getWidth(), getHeight());
        }
        return super.render(layer);
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        if (super.onMousePressed(x, y, button)) return true;
        return new NRectangle(NPoint.ZERO, getSize()).contains(x, y);
    }

    @Override
    public boolean onMouseReleased(int x, int y, int button) {
        if (super.onMouseReleased(x, y, button)) return true;
        return new NRectangle(NPoint.ZERO, getSize()).contains(x, y);
    }

    @Override
    public boolean onMouseDragged(int x, int y, int button) {
        if (super.onMouseDragged(x, y, button)) return true;
        return new NRectangle(NPoint.ZERO, getSize()).contains(x, y);
    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        if (super.onMouseMoved(x, y)) return true;
        return new NRectangle(NPoint.ZERO, getSize()).contains(x, y);
    }

    @Override
    public boolean onMouseWheelScrolled(int x, int y, int value) {
        if (super.onMouseWheelScrolled(x, y, value)) return true;
        return new NRectangle(NPoint.ZERO, getSize()).contains(x, y);
    }
}
