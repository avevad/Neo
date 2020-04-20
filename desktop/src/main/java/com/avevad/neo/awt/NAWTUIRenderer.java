package com.avevad.neo.awt;

import com.avevad.neo.graphics.NGraphics;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NParentComponent;
import com.avevad.neo.ui.events.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.awt.event.KeyEvent.*;

public final class NAWTUIRenderer {
    private static final Map<Integer, NKeyEvent.NKey> KEY_MAP = new HashMap<>();

    static {
        KEY_MAP.put(VK_UP, NKeyEvent.NKey.ARROW_UP);
        KEY_MAP.put(VK_LEFT, NKeyEvent.NKey.ARROW_LEFT);
        KEY_MAP.put(VK_DOWN, NKeyEvent.NKey.ARROW_DOWN);
        KEY_MAP.put(VK_RIGHT, NKeyEvent.NKey.ARROW_RIGHT);
        KEY_MAP.put(VK_INSERT, NKeyEvent.NKey.INSERT);
        KEY_MAP.put(VK_DELETE, NKeyEvent.NKey.DELETE);
        KEY_MAP.put(VK_HOME, NKeyEvent.NKey.HOME);
        KEY_MAP.put(VK_END, NKeyEvent.NKey.END);
        KEY_MAP.put(VK_PAGE_UP, NKeyEvent.NKey.PAGE_UP);
        KEY_MAP.put(VK_PAGE_DOWN, NKeyEvent.NKey.PAGE_DOWN);
        KEY_MAP.put(VK_TAB, NKeyEvent.NKey.TAB);
        KEY_MAP.put(VK_CONTROL, NKeyEvent.NKey.CTRL);
        KEY_MAP.put(VK_SHIFT, NKeyEvent.NKey.SHIFT);
        KEY_MAP.put(VK_ALT, NKeyEvent.NKey.ALT);
        KEY_MAP.put(VK_SPACE, NKeyEvent.NKey.SPACE);
        KEY_MAP.put(VK_ENTER, NKeyEvent.NKey.ENTER);
    }

    private volatile int maxFPS = 60;
    private final Thread rendererThread;

    public NAWTUIRenderer(NComponent component, Component parent) {
        component.setSize(parent.getWidth(), parent.getHeight());
        rendererThread = new ComponentRenderer(component, parent);
    }

    public void start() {
        rendererThread.start();
    }

    public void suspend() {
        rendererThread.suspend();
    }

    public void resume() {
        rendererThread.resume();
    }

    public void stop() {
        rendererThread.stop();
    }

    public void setMaxFPS(int maxFPS) {
        this.maxFPS = maxFPS;
    }

    private static void bindEvents(NComponent to, Component from) {
        Set<KeyStroke> forwardKeys = new HashSet<>(1);
        forwardKeys.add(KeyStroke.getKeyStroke(
                VK_TAB, InputEvent.CTRL_MASK));
        from.setFocusTraversalKeys(
                KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        Set<KeyStroke> backwardKeys = new HashSet<>(1);
        backwardKeys.add(KeyStroke.getKeyStroke(
                VK_TAB, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        from.setFocusTraversalKeys(
                KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

        from.setFocusable(true);
        from.requestFocus();

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                to.onMousePressed(new NMousePressedEvent(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getButton()));
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                to.onMouseReleased(new NMouseReleasedEvent(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getButton()));
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                to.onMouseExited();
            }

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                to.onMouseDragged(new NMouseDraggedEvent(mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getButton()));
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                to.onMouseMoved(new NMouseMovedEvent(mouseEvent.getX(), mouseEvent.getY()));
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                to.onMouseWheelScrolled(new NMouseWheelScrolledEvent(mouseWheelEvent.getX(), mouseWheelEvent.getY(), mouseWheelEvent.getUnitsToScroll()));
            }
        };
        from.addMouseWheelListener(mouseAdapter);
        from.addMouseListener(mouseAdapter);
        from.addMouseMotionListener(mouseAdapter);
        from.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int code = keyEvent.getKeyCode();
                NKeyEvent.NKey key = KEY_MAP.getOrDefault(code, NKeyEvent.NKey.UNKNOWN);
                to.onKeyPressed(new NKeyPressedEvent(key, keyEvent.getKeyChar()));
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                int code = keyEvent.getKeyCode();
                NKeyEvent.NKey key = KEY_MAP.getOrDefault(code, NKeyEvent.NKey.UNKNOWN);
                to.onKeyReleased(new NKeyReleasedEvent(key, keyEvent.getKeyChar()));
            }
        });

        from.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                to.setSize(from.getWidth(), from.getHeight());
            }
        });
    }

    public final class ComponentRenderer extends Thread {
        private final NComponent component;
        private final Component parent;
        private BufferedImage buffer;
        private final RootComponent root;

        public ComponentRenderer(NComponent component, Component parent) {
            this.component = component;
            this.parent = parent;
            root = new RootComponent();
            root.addChild(component);
            root.setFocus(component);
            bindEvents(component, this.parent);
            setName("Neo-AWT Component Renderer");
            updateGraphics();
        }

        @Override
        public void run() {
            while (true){
                long before = System.currentTimeMillis();

                //buffer & components setup:
                if(buffer == null || buffer.getWidth() != component.getWidth() || buffer.getHeight() != component.getHeight()) {
                    updateGraphics();
                }
                if(component.getX() != 0) component.setX(0);
                if(component.getY() != 0) component.setY(0);

                //render:
                int layer = 0;
                while (component.render(layer++));
                parent.getGraphics().drawImage(buffer, 0, 0, null);

                long after = System.currentTimeMillis();

                //FPS control:
                long renderTime = after - before;
                final int delay = 1000 / maxFPS;
                long sleep = delay - renderTime;
                try {
                    Thread.sleep(sleep <= 0 ? 0 : sleep);
                } catch (InterruptedException e) {
                }
            }
        }

        private void updateGraphics() {
            buffer = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
            root.graphics = new NAWTGraphics((Graphics2D) buffer.getGraphics());
            parent.setSize(component.getWidth(), component.getHeight());
        }
    }

    private static final class RootComponent extends NParentComponent {
        private NGraphics graphics;

        @Override
        public NGraphics getGraphics() {
            return graphics;
        }
    }

}
