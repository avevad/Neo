package com.avevad.neo.awt;

import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.graphics.NPoint;
import com.avevad.neo.graphics.NRectangle;
import com.avevad.neo.ui.NParentComponent;
import com.avevad.neo.ui.events.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.avevad.neo.ui.events.NKeyEvent.NKey;
import static java.awt.event.KeyEvent.*;

public class NAWTViewport extends JPanel {
    private static final Map<Integer, NKey> KEY_MAP = new HashMap<>();

    static {
        KEY_MAP.put(VK_UP, NKey.ARROW_UP);
        KEY_MAP.put(VK_LEFT, NKey.ARROW_LEFT);
        KEY_MAP.put(VK_DOWN, NKey.ARROW_DOWN);
        KEY_MAP.put(VK_RIGHT, NKey.ARROW_RIGHT);
        KEY_MAP.put(VK_INSERT, NKey.INSERT);
        KEY_MAP.put(VK_DELETE, NKey.DELETE);
        KEY_MAP.put(VK_HOME, NKey.HOME);
        KEY_MAP.put(VK_END, NKey.END);
        KEY_MAP.put(VK_PAGE_UP, NKey.PAGE_UP);
        KEY_MAP.put(VK_PAGE_DOWN, NKey.PAGE_DOWN);
        KEY_MAP.put(VK_TAB, NKey.TAB);
        KEY_MAP.put(VK_CONTROL, NKey.CTRL);
        KEY_MAP.put(VK_SHIFT, NKey.SHIFT);
        KEY_MAP.put(VK_ALT, NKey.ALT);
        KEY_MAP.put(VK_SPACE, NKey.SPACE);
        KEY_MAP.put(VK_ENTER, NKey.ENTER);
    }

    private NGraphics graphics;
    private Graphics2D graphics2D;
    public final NAWTPanel panel = new NAWTPanel();

    public NAWTViewport() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                panel.onMousePressed(new NMousePressedEvent(e.getX(), e.getY(), e.getButton()));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                panel.onMouseReleased(new NMouseReleasedEvent(e.getX(), e.getY(), e.getButton()));
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                panel.onMouseWheelScrolled(new NMouseWheelScrolledEvent(e.getX(), e.getY(), e.getWheelRotation()));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                panel.onMouseDragged(new NMouseDraggedEvent(e.getX(), e.getY(), e.getButton()));
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                panel.onMouseMoved(new NMouseMovedEvent(e.getX(), e.getY()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.onMouseExited();
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                NKeyEvent.NKey key = KEY_MAP.getOrDefault(code, NKeyEvent.NKey.UNKNOWN);
                panel.onKeyPressed(new NKeyPressedEvent(key, e.getKeyChar()));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                NKeyEvent.NKey key = KEY_MAP.getOrDefault(code, NKeyEvent.NKey.UNKNOWN);
                panel.onKeyReleased(new NKeyReleasedEvent(key, e.getKeyChar()));
            }
        };
        addKeyListener(keyAdapter);
        Set<KeyStroke> forwardKeys = new HashSet<>();
        setFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
        Set<KeyStroke> backwardKeys = new HashSet<>();
        setFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
        setFocusable(true);
        requestFocus();
    }

    @Override
    public void paint(Graphics g) {
        if (g != graphics2D) {
            graphics2D = (Graphics2D) g;
            graphics = new NAWTGraphics(graphics2D);
        }
        if (getWidth() != panel.getWidth() || getHeight() != panel.getHeight()) panel.setSize(getWidth(), getHeight());
        int layer = 0;
        while (panel.render(layer, new NRectangle(NPoint.ZERO, panel.getSize()))) layer++;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(panel.getWidth(), panel.getHeight());
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
