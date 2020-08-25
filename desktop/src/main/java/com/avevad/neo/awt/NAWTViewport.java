package com.avevad.neo.awt;

import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NParentComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class NAWTViewport extends JPanel {
    private NGraphics graphics;
    private Graphics2D graphics2D;
    public final NAWTPanel panel = new NAWTPanel();

    public NAWTViewport() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.setSize(getWidth(), getHeight());
                System.out.println("changed: " + getWidth() + " " + getHeight());
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        if (g != graphics2D) {
            graphics2D = (Graphics2D) g;
            graphics = new NAWTGraphics(graphics2D);
        }
        int layer = 0;
        while (panel.render(layer, new NRectangle(NPoint.ZERO, panel.getSize()))) layer++;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(panel.getX() + panel.getWidth(), panel.getY() + panel.getHeight());
    }

    public class NAWTPanel extends NParentComponent {
        private NAWTPanel() {
        }

        @Override
        public NGraphics getGraphics() {
            if (graphics == null) {
                Graphics2D graphics2D = (Graphics2D) NAWTViewport.this.getGraphics();
                if (graphics2D != null) graphics = new NAWTGraphics(graphics2D);
            }
            return graphics;
        }

        @Override
        public void update(NRectangle area) {
            repaint(new Rectangle(area.x, area.y, area.w, area.h));
        }
    }
}
