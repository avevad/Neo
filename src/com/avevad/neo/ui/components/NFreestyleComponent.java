package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NImage;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NComponent;

public class NFreestyleComponent extends NComponent {
    public final NImage canvas;

    public NFreestyleComponent(NImage canvas, int x, int y, int w, int h) {
        super(x, y, canvas.w, canvas.h);
        this.canvas = canvas;
    }

    @Override
    public boolean render(int layer) {
        NGraphics g = getParent().getGraphics();
        g.drawImage(canvas, getLocation(), getSize());
        return false;
    }

    @Override
    public boolean onMousePressed(int x, int y, int button) {
        return false;
    }

    @Override
    public boolean onMouseReleased(int x, int y, int button) {
        return false;
    }

    @Override
    public boolean onMouseDragged(int x, int y, int button) {
        return false;
    }

    @Override
    public boolean onMouseWheelScrolled(int x, int y, int value) {
        return false;
    }

    @Override
    public boolean onMouseMoved(int x, int y) {
        return false;
    }

    @Override
    public void onKeyPressed(int key) {

    }

    @Override
    public void onKeyReleased(int key) {

    }

    @Override
    public boolean isKeyboardNeeded() {
        return false;
    }

    @Override
    public int getZIndex() {
        return 0;
    }
}
