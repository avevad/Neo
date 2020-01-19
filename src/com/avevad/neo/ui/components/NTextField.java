package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.*;
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

    public NTextField() {
        setUI(new DefaultUI());
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NTextSelection getSelection() {
        return selection;
    }

    public void setSelection(NTextSelection selection) {
        this.selection = selection;
        if (caretPosition != selection.begin && caretPosition != selection.end) setCaretPosition(selection.end);
    }

    public NFont getFont() {
        return font;
    }

    public void setFont(NFont font) {
        this.font = font;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public int getViewOffset() {
        return viewOffset;
    }

    public void setViewOffset(int viewOffset) {
        this.viewOffset = viewOffset;
    }

    public int getCaretPosition() {
        return caretPosition;
    }

    public void setCaretPosition(int caretPosition) {
        this.caretPosition = caretPosition;
        if (caretPosition != selection.begin && caretPosition != selection.end)
            setSelection(new NTextSelection(caretPosition, caretPosition));
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
        return true;
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!isEnabled()) return false;
        isMousePressed = false;
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
                    setSelection(new NTextSelection(caret, caret));
                    setCaretPosition(caret);
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
                } else {
                    String newText = text.substring(0, selection.begin) + c + text.substring(selection.end);
                    int caret = selection.begin + 1;
                    NTextSelection newSelection = new NTextSelection(caret, caret);
                    textChanged.trigger(new NTextChangedEvent(text, newText));
                    caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, caret));
                    selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                    setSelection(newSelection);
                    setCaretPosition(caret);
                }
            }
        } else {
            if (event.key == NKeyEvent.NKey.ARROW_LEFT) {
                if (caretPosition != 0) {
                    NTextSelection newSelection = new NTextSelection(caretPosition - 1, caretPosition - 1);
                    caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, caretPosition - 1));
                    selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                    setSelection(newSelection);
                    setCaretPosition(caretPosition - 1);
                }
            }
            if (event.key == NKeyEvent.NKey.ARROW_RIGHT) {
                if (caretPosition != text.length()) {
                    NTextSelection newSelection = new NTextSelection(caretPosition + 1, caretPosition + 1);
                    caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, caretPosition + 1));
                    selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                    setSelection(newSelection);
                    setCaretPosition(caretPosition + 1);
                }
            }
            if (event.key == NKeyEvent.NKey.HOME) {
                NTextSelection newSelection = new NTextSelection(0, 0);
                caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, 0));
                selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                setSelection(newSelection);
                setCaretPosition(0);
            }
            if (event.key == NKeyEvent.NKey.END) {
                NTextSelection newSelection = new NTextSelection(text.length(), text.length());
                caretPositionChanged.trigger(new NCaretPositionChangedEvent(caretPosition, text.length()));
                selectionChanged.trigger(new NTextSelectionChangedEvent(selection, newSelection));
                setSelection(newSelection);
                setCaretPosition(text.length());
            }
        }
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {

    }

    @Override
    public boolean isKeyboardNeeded() {
        return isFocused() && isEnabled();
    }


    private static final class DefaultUI implements NUI {
        public static final int DEFAULT_BACKGROUND_COLOR = NColor.WHITE;
        public static final int DEFAULT_FOREGROUND_COLOR = NColor.BLACK;

        public static final int CARET_BLINK_DURATION = 500;

        public static final int DISABLED_COVER_COLOR = NColor.WHITE;
        public static final double DISABLED_COVER_OPACITY = 0.5;

        @Override
        public boolean render(NComponent component, int layer) {
            if (!(component instanceof NTextField))
                throw new IllegalArgumentException("This UI can only render NTextField");
            if (layer > 0) return false;
            NTextField textField = (NTextField) component;
            NGraphics g = textField.getGraphics();

            String text = textField.getText();
            NFont font = textField.getFont();
            NFontMetrics fontMetrics = g.getFontMetrics(font);
            int w = textField.getWidth();
            int h = textField.getHeight();
            int backgroundColor = textField.getBackgroundColor();
            int offset = textField.getViewOffset();
            NTextSelection selection = textField.getSelection();
            int caretPosition = textField.getCaretPosition();
            int selectionLength = selection.length();
            int textY = NLabel.alignText(text, h, fontMetrics, NVerticalTextAlignment.CENTER);
            int ascent = fontMetrics.getAscent();
            String selectionText = text.substring(selection.begin, selection.end);
            int selectionRectangleX = fontMetrics.getWidth(text.substring(0, selection.begin)) - offset;
            int selectionRectangleY = textY - ascent;
            int selectionRectangleW = fontMetrics.getWidth(selectionText);
            int textH = fontMetrics.getAscent() + fontMetrics.getDescent();
            int caretX = fontMetrics.getWidth(text.substring(0, caretPosition)) - offset;

            boolean caretVisible = ((System.currentTimeMillis() - textField.getLastKeyPressTime()) / CARET_BLINK_DURATION) % 2 == 0;
            caretVisible &= textField.isKeyboardNeeded();
            if (backgroundColor == NColor.NONE) backgroundColor = DEFAULT_BACKGROUND_COLOR;
            int foregroundColor = textField.getForegroundColor();
            if (foregroundColor == NColor.NONE) foregroundColor = DEFAULT_FOREGROUND_COLOR;
            int caretColor = foregroundColor;

            g.setOpacity(textField.getOpacity());

            g.setColor(backgroundColor);
            g.fillRect(0, 0, w, h);

            g.setColor(foregroundColor);
            g.setFont(font);
            g.drawString(text, -offset, textY);

            if (selectionLength != 0) {
                g.setColor(foregroundColor);
                g.fillRect(selectionRectangleX, selectionRectangleY, selectionRectangleW, textH);

                g.setColor(backgroundColor);
                g.drawString(selectionText, selectionRectangleX, textY);
            }

            if (caretVisible) {
                g.setColor(caretColor);
                if (caretPosition == selection.end) g.fillRect(caretX, selectionRectangleY, 2, textH);
                else g.fillRect(caretX - 2, selectionRectangleY, 2, textH);
            }

            g.setColor(foregroundColor);
            g.drawRect(0, 0, w - 1, h - 1);

            if (!textField.isEnabled()) {
                g.setOpacity(DISABLED_COVER_OPACITY * textField.getOpacity());
                g.setColor(DISABLED_COVER_COLOR);
                g.fillRect(new NRectangle(NPoint.ZERO, textField.getSize()));
            }

            if (caretX < 2) {
                textField.setViewOffset(caretX + offset);
            } else if (caretX > w - 2) {
                textField.setViewOffset(caretX + offset - w + 2);
            }

            return false;
        }
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
