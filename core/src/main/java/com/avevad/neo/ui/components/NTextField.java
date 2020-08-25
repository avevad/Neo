package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NEvent;
import com.avevad.neo.ui.NEventDispatcher;
import com.avevad.neo.ui.events.*;


public class NTextField extends NComponent {
    private boolean isMousePressed;
    private long lastKeyPressTime = 0;
    private String text = "";
    private NTextSelection selection = new NTextSelection(0, 0);
    private int caretPosition = 0;
    private NFont font;
    private int backgroundColor = NColor.NONE;
    private int foregroundColor = NColor.NONE;
    private int viewOffset = 0;

    public final NEventDispatcher<NTextChangedEvent> textChanged = new NEventDispatcher<>();
    public final NEventDispatcher<NCaretPositionChangedEvent> caretPositionChanged = new NEventDispatcher<>();
    public final NEventDispatcher<NTextSelectionChangedEvent> selectionChanged = new NEventDispatcher<>();
    private boolean enabled = true;
    private NTextFieldUI ui;

    public void setUI(NTextFieldUI ui) {
        this.ui = ui;
        update();
    }

    public NTextFieldUI getUI() {
        return ui;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (caretPosition > text.length()) setCaretPosition(text.length());
        this.text = text;
        update();
    }

    public NTextSelection getSelection() {
        return selection;
    }

    public void setSelection(NTextSelection selection) {
        this.selection = selection;
        if (caretPosition != selection.begin && caretPosition != selection.end) setCaretPosition(selection.end);
        update();
    }

    public NFont getFont() {
        return font;
    }

    public void setFont(NFont font) {
        this.font = font;
        update();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        update();
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
        update();
    }

    public int getViewOffset() {
        return viewOffset;
    }

    public void setViewOffset(int viewOffset) {
        this.viewOffset = viewOffset;
        update();
    }

    public int getCaretPosition() {
        return caretPosition;
    }

    public void setCaretPosition(int caretPosition) {
        this.caretPosition = caretPosition;
        if (caretPosition != selection.begin && caretPosition != selection.end)
            setSelection(new NTextSelection(caretPosition, caretPosition));
        update();
    }

    public long getLastKeyPressTime() {
        return lastKeyPressTime;
    }

    @Override
    public boolean onMousePressed(NMousePressedEvent event) {
        if (!isEnabled()) return false;
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;
        isMousePressed = true;
        NFontMetrics fontMetrics = getGraphics().getFontMetrics(font);
        int pos = 0;
        for (; pos < text.length(); pos++) {
            int x = fontMetrics.getWidth(text.substring(0, pos));
            if (pos < text.length()) x = (x + fontMetrics.getWidth(text.substring(0, pos + 1))) / 2;
            x -= viewOffset;
            if (event.x <= x) break;
        }
        NTextSelection newSelection = new NTextSelection(pos, pos);
        caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, pos));
        selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
        setSelection(newSelection);
        lastKeyPressTime = System.currentTimeMillis();
        update();
        return true;
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!isEnabled()) return false;
        isMousePressed = false;
        update();
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        if (!isEnabled()) return false;
        if (!isMousePressed) return false;
        NFontMetrics fontMetrics = getGraphics().getFontMetrics(font);
        int pos = 0;
        for (; pos < text.length(); pos++) {
            int x = fontMetrics.getWidth(text.substring(0, pos));
            if (pos < text.length()) x = (x + fontMetrics.getWidth(text.substring(0, pos + 1))) / 2;
            x -= viewOffset;
            if (event.x <= x) break;
        }
        int begin = selection.begin, end = selection.end;
        if (caretPosition == begin) {
            begin = pos;
        } else {
            end = pos;
        }
        NTextSelection newSelection = new NTextSelection(Integer.min(begin, end), Integer.max(begin, end));
        caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, pos));
        selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
        setSelection(newSelection);
        setCaretPosition(pos);
        lastKeyPressTime = System.currentTimeMillis();
        update();
        return true;
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        if (!isEnabled()) return false;
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        if (!isEnabled()) return false;
        update();
        return new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y);
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {
        if (!isEnabled()) return;
        lastKeyPressTime = System.currentTimeMillis();
        if (((short) event.c) != -1) {
            char c = event.c;
            NKeyEvent.NKey key = event.key;
            if (c == '\b' || key == NKeyEvent.NKey.DELETE) {
                if (selection.length() == 0) {
                    if (c == '\b') if (caretPosition > 0) {
                        String newText = text.substring(0, caretPosition - 1) + text.substring(caretPosition);
                        NTextSelection newSelection = new NTextSelection(caretPosition - 1, caretPosition - 1);
                        textChanged.trigger(new NTextChangedEvent(text, newText));
                        caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, caretPosition - 1));
                        selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                        text = newText;
                        setCaretPosition(caretPosition - 1);
                        setSelection(newSelection);
                    }
                    if (key == NKeyEvent.NKey.DELETE) if (caretPosition < text.length()) {
                        String newText = text.substring(0, caretPosition) + text.substring(caretPosition + 1);
                        textChanged.trigger(new NTextChangedEvent(text, newText));
                        text = newText;
                    }
                } else {
                    String newText = text.substring(0, selection.begin) + text.substring(selection.end);
                    int caret = selection.begin;
                    textChanged.trigger(new NTextChangedEvent(text, newText));
                    caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, caret));
                    selectionChanged.trigger(new NTextSelectionChangedEvent(selection, new NTextSelection(caret, caret)));
                    text = newText;
                    setCaretPosition(caret);
                    setSelection(new NTextSelection(caret, caret));
                }
            } else if (c >= 32) {
                if (selection.length() == 0) {
                    String newText = text.substring(0, caretPosition) + c + text.substring(caretPosition);
                    NTextSelection newSelection = new NTextSelection(caretPosition + 1, caretPosition + 1);
                    textChanged.trigger(new NTextChangedEvent(text, newText));
                    caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, caretPosition + 1));
                    selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                    text = newText;
                    setCaretPosition(caretPosition + 1);
                    setSelection(newSelection);
                } else {
                    String newText = text.substring(0, selection.begin) + c + text.substring(selection.end);
                    int caret = selection.begin + 1;
                    NTextSelection newSelection = new NTextSelection(caret, caret);
                    textChanged.trigger(new NTextChangedEvent(text, newText));
                    caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, caret));
                    selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                    text = newText;
                    setCaretPosition(caret);
                    setSelection(newSelection);
                }
            }
        } else {
            if (event.key == NKeyEvent.NKey.ARROW_LEFT) {
                if (caretPosition != 0) {
                    NTextSelection newSelection = new NTextSelection(caretPosition - 1, caretPosition - 1);
                    caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, caretPosition - 1));
                    selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                    setCaretPosition(caretPosition - 1);
                    setSelection(newSelection);
                }
            }
            if (event.key == NKeyEvent.NKey.ARROW_RIGHT) {
                if (caretPosition != text.length()) {
                    NTextSelection newSelection = new NTextSelection(caretPosition + 1, caretPosition + 1);
                    caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, caretPosition + 1));
                    selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                    setCaretPosition(caretPosition + 1);
                    setSelection(newSelection);
                }
            }
            if (event.key == NKeyEvent.NKey.HOME) {
                NTextSelection newSelection = new NTextSelection(0, 0);
                caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, 0));
                selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                setCaretPosition(0);
                setSelection(newSelection);
            }
            if (event.key == NKeyEvent.NKey.END) {
                NTextSelection newSelection = new NTextSelection(text.length(), text.length());
                caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, text.length()));
                selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                setCaretPosition(text.length());
                setSelection(newSelection);
            }
        }
        update();
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {

    }

    @Override
    public boolean isKeyboardNeeded() {
        return isFocused() && isEnabled();
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
        update();
    }

    @Override
    public boolean render(int layer, NRectangle area) {
        return ui.drawTextField(this, layer, area);
    }

    public interface NTextFieldUI {
        boolean drawTextField(NTextField textField, int layer, NRectangle area);
    }

    public static final class NTextSelection {
        public final int begin, end;

        public NTextSelection(int begin, int end) {
            this.begin = begin;
            this.end = end;
        }

        public final int length() {
            return end - begin;
        }

        @Override
        public String toString() {
            return "[" + begin + "; " + end + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof NTextSelection)) return false;
            NTextSelection selection = (NTextSelection) o;
            return selection.begin == begin && selection.end == end;
        }

    }

    public static final class NTextChangedEvent extends NEvent {
        public final String oldText, newText;

        public NTextChangedEvent(String oldText, String newText) {
            this.oldText = oldText;
            this.newText = newText;
        }
    }

    public static final class NCaretPositionChangedEvent extends NEvent {
        public final int oldPos, newPos;

        public NCaretPositionChangedEvent(int oldPos, int newPos) {
            this.oldPos = oldPos;
            this.newPos = newPos;
        }
    }

    public static final class NTextSelectionChangedEvent extends NEvent {
        public final NTextSelection oldSelection, newSelection;

        public NTextSelectionChangedEvent(NTextSelection oldSelection, NTextSelection newSelection) {
            this.oldSelection = oldSelection;
            this.newSelection = newSelection;
        }
    }
}
