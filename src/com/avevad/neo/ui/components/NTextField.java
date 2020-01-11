package com.avevad.neo.ui.components;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.NComponent;
import com.avevad.neo.ui.NHorizontalTextAlignment;
import com.avevad.neo.ui.NUI;
import com.avevad.neo.ui.NVerticalTextAlignment;
import com.avevad.neo.ui.events.*;


public class NTextField extends NComponent {
    private String text;
    private NTextSelection selection = new NTextSelection(0, 0);
    private int caretPosition = 0;
    private NFont font;
    private int backgroundColor = NColor.NONE;
    private int foregroundColor = NColor.NONE;
    private int viewOffset = 0;

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


    @Override
    public boolean onMousePressed(NMousePressedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;

        return true;
    }

    @Override
    public boolean onMouseReleased(NMouseReleasedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;

        return true;
    }

    @Override
    public boolean onMouseDragged(NMouseDraggedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;

        return true;
    }

    @Override
    public boolean onMouseWheelScrolled(NMouseWheelScrolledEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;

        return true;
    }

    @Override
    public boolean onMouseMoved(NMouseMovedEvent event) {
        if (!new NRectangle(NPoint.ZERO, getSize()).contains(event.x, event.y)) return false;

        return true;
    }

    @Override
    public void onKeyPressed(NKeyPressedEvent event) {
        if (((short) event.c) != -1) {
            char c = event.c;
            if (c == '\b' || c == 127) {
                if (selection.length() == 0) {
                    if (c == '\b') if (caretPosition > 0) {
                        text = text.substring(0, caretPosition - 1) + text.substring(caretPosition);
                        setCaretPosition(caretPosition - 1);
                    }
                    if (c == 127) if (caretPosition < text.length()) {
                        text = text.substring(0, caretPosition) + text.substring(caretPosition + 1);
                    }
                } else {
                    text = text.substring(0, selection.begin) + text.substring(selection.end);
                    int caret = selection.begin;
                    setSelection(new NTextSelection(0, 0));
                    setCaretPosition(caret);
                }
            } else if (c >= 32) {
                if (selection.length() == 0) {
                    text = text.substring(0, caretPosition) + c + text.substring(caretPosition);
                    setCaretPosition(caretPosition + 1);
                } else {
                    text = text.substring(0, selection.begin) + c + text.substring(selection.end);
                    int caret = selection.begin + 1;
                    setSelection(new NTextSelection(0, 0));
                    setCaretPosition(caret);
                }
            }
        } else {

        }
    }

    @Override
    public void onKeyReleased(NKeyReleasedEvent event) {

    }

    @Override
    public boolean isKeyboardNeeded() {
        return isFocused();
    }


    private static final class DefaultUI implements NUI {
        public static final int DEFAULT_BACKGROUND_COLOR = NColor.WHITE;
        public static final int DEFAULT_FOREGROUND_COLOR = NColor.BLACK;

        public static final int CARET_BLINK_DURATION = 500;

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

            boolean caretVisible = (System.currentTimeMillis() / CARET_BLINK_DURATION) % 2 == 1;
            caretVisible &= textField.isFocused();
            if (backgroundColor == NColor.NONE) backgroundColor = DEFAULT_BACKGROUND_COLOR;
            int foregroundColor = textField.getForegroundColor();
            if (foregroundColor == NColor.NONE) foregroundColor = DEFAULT_FOREGROUND_COLOR;
            int caretColor = foregroundColor;

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
                g.fillRect(caretX, selectionRectangleY, 2, textH);
            }

            g.setColor(foregroundColor);
            g.drawRect(0, 0, w - 1, h - 1);

            if (caretX < 0) {
                textField.setViewOffset(caretX + offset);
            } else if (caretX > w - 1) {
                textField.setViewOffset(caretX + offset - w);
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
    }
}
