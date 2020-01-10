package com.avevad.neo.ui.components.parent;

import com.avevad.neo.graphics.NColor;
import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NParentComponent;
import com.avevad.neo.ui.NUI;

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

    private static final class DefaultUI implements NUI {
        public static final int DEFAULT_COLOR = NColor.WHITE;

        @Override
        public boolean render(NComponent component, int layer) {
            if (!(component instanceof NPanel)) throw new IllegalArgumentException("This UI can only render NPanel");
            NPanel panel = (NPanel) component;
            NGraphics g = panel.getParent().getGraphics();
            if (layer == 0) {
                g.setColor(panel.getColor());
                g.fillRect(panel.getX(), panel.getY(), panel.getWidth(), panel.getHeight());
            }
            return false;
        }
    }
}
