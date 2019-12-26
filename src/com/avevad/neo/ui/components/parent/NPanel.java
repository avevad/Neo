package com.avevad.neo.ui.components.parent;

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
    public int getZIndex() {
        return 0;
    }
}
