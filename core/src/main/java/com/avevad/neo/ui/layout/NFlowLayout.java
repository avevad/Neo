package com.avevad.neo.ui.layout;

import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NParentComponent;

import java.util.ArrayList;
import java.util.List;

public class NFlowLayout {
    private boolean recalculating = false;
    private final NParentComponent parent;
    private final List<NComponent> components = new ArrayList<>();

    public NFlowLayout(NParentComponent parent) {
        this.parent = parent;
        parent.boundsChanged.addHandler(event -> recalculateBounds());
    }

    public void add(NComponent component) {
        parent.addChild(component);
        components.add(component);
        component.boundsChanged.addHandler(event -> recalculateBounds());
        component.visibilityChanged.addHandler(event -> recalculateBounds());
        recalculateBounds();
    }

    public synchronized void recalculateBounds() {
        if (recalculating) return;
        recalculating = true;
        List<NComponent> components = new ArrayList<>();
        for (NComponent component : this.components) if (component.isVisible()) components.add(component);
        int rowStart = 0;
        int rowHeight = 0;
        int x = 0;
        int y = 0;
        for (int i = 0; i < components.size(); i++) {
            NComponent component = components.get(i);
            if (rowStart != i && x + component.getWidth() > parent.getWidth()) {
                for (int j = rowStart; j < i; j++) {
                    NComponent component2 = components.get(j);
                    if (component2 == component) break;
                    alignY(component2, y, rowHeight);
                }
                y += rowHeight;
                rowHeight = 0;
                x = 0;
                rowStart = i;
            }
            component.setX(x);
            x += component.getWidth();
            rowHeight = Integer.max(rowHeight, component.getHeight());
        }
        for (int i = rowStart; i < components.size(); i++) {
            alignY(components.get(i), y, rowHeight);
        }
        recalculating = false;
    }

    private void alignY(NComponent component, int y, int rowHeight) {
        component.setY(y + (rowHeight - component.getHeight()) / 2);
    }
}
