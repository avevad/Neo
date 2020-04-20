package com.avevad.neo.ui;

import com.avevad.neo.graphics.*;
import com.avevad.neo.ui.components.*;
import com.avevad.neo.ui.components.parent.NPanel;
import com.avevad.neo.util.NPair;

import static com.avevad.neo.ui.components.NLabel.alignLabel;

public class BasicUI implements
        NButton.NButtonUI, NRadioButton.NRadioButtonUI, NCheckBox.NCheckBoxUI, NPanel.NPanelUI, NLabel.NLabelUI,
        NProgressBar.NProgressBarUI, NTextField.NTextFieldUI {
    private UITweaks tweaks;

    public BasicUI(UITweaks tweaks) {
        this.tweaks = tweaks;
    }

    public void setTweaks(UITweaks tweaks) {
        this.tweaks = tweaks;
    }

    @Override
    public boolean drawButton(NButton button, int layer) {
        NGraphics g = button.getGraphics();

        NDimension size = button.getSize();
        int w = size.w;
        int h = size.h;
        NFont font = button.getFont();
        NFontMetrics fontMetrics = g.getFontMetrics(font);
        NImage i = button.getIcon();
        int iw = i == null ? 0 : i.w;
        String text = NLabel.cutToFit(button.getText(), w - iw, fontMetrics);
        int ascent = fontMetrics.getAscent();
        NPair<NPoint, NPoint> alignment = alignLabel(i, text, size, fontMetrics, NHorizontalTextAlignment.CENTER, NVerticalTextAlignment.CENTER, button.getIconPosition());
        boolean pressed = button.isPressed();
        boolean hovered = button.isHovered();
        boolean focused = button.isFocused();

        int backgroundColor = button.getBackgroundColor();
        if (backgroundColor == NColor.NONE) backgroundColor = tweaks.defaultBackgroundColor;
        int foregroundColor = button.getForegroundColor();
        if (foregroundColor == NColor.NONE) foregroundColor = tweaks.defaultForegroundColor;
        int pressColor = NColor.mix(backgroundColor, foregroundColor, tweaks.pressColorRatio);
        int hoverColor = NColor.mix(backgroundColor, foregroundColor, tweaks.hoverColorRatio);
        int focusColor = NColor.mix(backgroundColor, foregroundColor, tweaks.focusColorRatio);
        if (pressed) backgroundColor = pressColor;
        else if (hovered) backgroundColor = hoverColor;

        g.setOpacity(button.getOpacity());

        g.setColor(backgroundColor);
        g.fillRect(0, 0, w, h);

        g.setColor(foregroundColor);
        g.drawRect(0, 0, w - 1, h - 1);

        if (i != null) g.drawImage(i, alignment.a);

        g.setFont(font);
        g.drawString(text, alignment.b);

        g.setColor(focusColor);
        if (focused) {
            int fx = Integer.min(alignment.b.x, alignment.a.x) / 2;
            int fy = Integer.min(alignment.b.y - ascent, alignment.a.y) / 2;
            g.drawRect(
                    fx, fy,
                    w - fx * 2 - 1, h - fy * 2 - 1
            );
        }

        if (!button.isEnabled()) {
            g.setOpacity(tweaks.disabledCoverOpacity * button.getOpacity());
            g.setColor(tweaks.disabledCoverColor);
            g.fillRect(new NRectangle(NPoint.ZERO, button.getSize()));
        }

        return false;
    }

    @Override
    public boolean drawRadioButton(NRadioButton radioButton, int layer) {
        if (layer > 0) return false;
        NGraphics g = radioButton.getGraphics();

        NDimension size = radioButton.getSize();
        int w = size.w;
        int h = size.h;
        int cx = (int) ((1. - tweaks.radiobuttonCheckRatio) / 2. * w);
        int cy = (int) ((1. - tweaks.radiobuttonCheckRatio) / 2. * h);
        int cw = (int) (tweaks.radiobuttonCheckRatio * w);
        int ch = (int) (tweaks.radiobuttonCheckRatio * h);
        boolean pressed = radioButton.isPressed();
        boolean hovered = radioButton.isHovered();
        boolean focused = radioButton.isFocused();
        boolean checked = radioButton.isChecked();

        int backgroundColor = radioButton.getBackgroundColor();
        if (backgroundColor == NColor.NONE) backgroundColor = tweaks.defaultBackgroundColor;
        int foregroundColor = radioButton.getForegroundColor();
        if (foregroundColor == NColor.NONE) foregroundColor = tweaks.defaultForegroundColor;
        int pressColor = NColor.mix(backgroundColor, foregroundColor, tweaks.pressColorRatio);
        int hoverColor = NColor.mix(backgroundColor, foregroundColor, tweaks.hoverColorRatio);
        int focusColor = NColor.mix(backgroundColor, foregroundColor, tweaks.focusColorRatio);
        if (pressed) backgroundColor = pressColor;
        else if (hovered) backgroundColor = hoverColor;

        g.setOpacity(radioButton.getOpacity());

        g.setColor(backgroundColor);
        g.fillOval(0, 0, w, h);

        if (checked) {
            g.setColor(foregroundColor);
            g.fillOval(cx, cy, cw - 1, ch - 1);
        }

        if (focused) {
            g.setColor(focusColor);
            g.drawOval(cx, cy, cw - 1, ch - 1);
        }

        g.setColor(foregroundColor);
        g.drawOval(0, 0, w - 1, h - 1);

        if (!radioButton.isEnabled()) {
            g.setOpacity(tweaks.disabledCoverOpacity * radioButton.getOpacity());
            g.setColor(tweaks.disabledCoverColor);
            g.fillOval(new NRectangle(NPoint.ZERO, radioButton.getSize()));
        }

        return false;
    }

    @Override
    public boolean drawCheckbox(NCheckBox checkBox, int layer) {
        NGraphics g = checkBox.getGraphics();

        NDimension size = checkBox.getSize();
        int w = size.w;
        int h = size.h;
        int x1 = (int) (w * tweaks.checkboxCheckX1);
        int y1 = (int) (h * tweaks.checkboxCheckY1);
        int x2 = (int) (w * tweaks.checkboxCheckX2);
        int y2 = (int) (h * tweaks.checkboxCheckY2);
        int x3 = (int) (w * tweaks.checkboxCheckX3);
        int y3 = (int) (h * tweaks.checkboxCheckY3);
        int fx1 = Integer.min(Integer.min(x1, x2), x3);
        int fy1 = Integer.min(Integer.min(y1, y2), y3);
        int fx2 = Integer.max(Integer.max(x1, x2), x3);
        int fy2 = Integer.max(Integer.max(y1, y2), y3);
        int fw = fx2 - fx1;
        int fh = fy2 - fy1;
        boolean pressed = checkBox.isPressed();
        boolean hovered = checkBox.isHovered();
        boolean focused = checkBox.isFocused();
        boolean checked = checkBox.isChecked();

        int backgroundColor = checkBox.getBackgroundColor();
        if (backgroundColor == NColor.NONE) backgroundColor = tweaks.defaultBackgroundColor;
        int foregroundColor = checkBox.getForegroundColor();
        if (foregroundColor == NColor.NONE) foregroundColor = tweaks.defaultForegroundColor;
        int pressColor = NColor.mix(backgroundColor, foregroundColor, tweaks.pressColorRatio);
        int hoverColor = NColor.mix(backgroundColor, foregroundColor, tweaks.hoverColorRatio);
        int focusColor = NColor.mix(backgroundColor, foregroundColor, tweaks.focusColorRatio);
        if (pressed) backgroundColor = pressColor;
        else if (hovered) backgroundColor = hoverColor;

        g.setOpacity(checkBox.getOpacity());

        g.setColor(backgroundColor);
        g.fillRect(0, 0, w, h);

        if (checked) {
            g.setColor(foregroundColor);
            g.drawLine(x1, y1, x2, y2);
            g.drawLine(x2, y2, x3, y3);
        }

        if (focused) {
            g.setColor(focusColor);
            g.drawRect(fx1, fy1, fw, fh);
        }

        g.setColor(foregroundColor);
        g.drawRect(0, 0, w - 1, h - 1);

        if (!checkBox.isEnabled()) {
            g.setOpacity(tweaks.disabledCoverOpacity * checkBox.getOpacity());
            g.setColor(tweaks.disabledCoverColor);
            g.fillRect(new NRectangle(NPoint.ZERO, checkBox.getSize()));
        }

        return false;
    }

    @Override
    public boolean drawPanel(NPanel panel, int layer) {
        NGraphics g = panel.getParent().getGraphics();
        if (layer == 0) {
            g.setOpacity(panel.getOpacity());
            g.setColor(panel.getColor() == NColor.NONE ? tweaks.defaultBackgroundColor : panel.getColor());
            g.fillRect(panel.getBounds());
        }
        if (layer == 1 && !panel.isEnabled()) {
            g.setOpacity(tweaks.disabledCoverOpacity * panel.getOpacity());
            g.setColor(tweaks.disabledCoverColor);
            g.fillRect(panel.getBounds());
        }
        return layer < 1;
    }

    @Override
    public boolean drawLabel(NLabel label, int layer) {
        NGraphics g = label.getGraphics();

        NHorizontalTextAlignment hAlign = label.getHorizontalAlignment();
        NVerticalTextAlignment vAlign = label.getVerticalAlignment();
        int color = label.getColor();
        if (color == NColor.NONE) color = tweaks.defaultForegroundColor;
        NFont font = label.getFont();
        NFontMetrics fontMetrics = g.getFontMetrics(font);
        int w = label.getWidth();
        int h = label.getHeight();
        NImage i = label.getIcon();
        int iw = i == null ? 0 : i.w;
        String s = NLabel.cutToFit(label.getText(), w - iw, fontMetrics);
        NPair<NPoint, NPoint> alignment = alignLabel(i, s, label.getSize(), fontMetrics, hAlign, vAlign, label.getIconPosition());

        g.setOpacity(label.getOpacity());

        if(i != null) g.drawImage(i, alignment.a);

        g.setFont(font);
        g.setColor(color);
        g.drawString(s, alignment.b);

        return false;
    }

    @Override
    public boolean drawProgressBar(NProgressBar bar, int layer) {
        NGraphics g = bar.getGraphics();

        NDirection direction = bar.getDirection();
        double progress = bar.getProgress();
        NDimension size = bar.getSize();
        int w = bar.getWidth();
        int h = bar.getHeight();
        int pw = (int) (w * progress);
        if (direction == NDirection.UP || direction == NDirection.DOWN) pw = w;
        int ph = (int) (h * progress);
        if (direction == NDirection.LEFT || direction == NDirection.RIGHT) ph = h;
        int px = 0;
        if (direction == NDirection.LEFT) px = w - pw;
        int py = 0;
        if (direction == NDirection.UP) py = h - ph;

        int backgroundColor = bar.getBackgroundColor();
        if (backgroundColor == NColor.NONE) backgroundColor = tweaks.defaultBackgroundColor;
        int foregroundColor = bar.getForegroundColor();
        if (foregroundColor == NColor.NONE) foregroundColor = tweaks.defaultForegroundColor;
        int textColor = progress > 0.5 ? backgroundColor : foregroundColor;
        int baseColor = progress > 0.5 ? foregroundColor : backgroundColor;

        NFont font = bar.getFont();
        String text = bar.getText();
        text = String.format(text, progress * 100.);
        NFontMetrics fontMetrics = g.getFontMetrics(font);
        int ta = fontMetrics.getAscent();
        int td = fontMetrics.getDescent();
        int tw = fontMetrics.getWidth(text);
        NPoint p = NLabel.alignLabel(null, text, size, fontMetrics, NHorizontalTextAlignment.CENTER, NVerticalTextAlignment.CENTER, null).b;

        g.setOpacity(bar.getOpacity());

        g.setColor(backgroundColor);
        g.fillRect(0, 0, w, h);

        g.setColor(foregroundColor);
        g.fillRect(px, py, pw, ph);

        g.setColor(baseColor);
        g.fillRect(p.x, p.y - ta, tw, ta + td);

        g.setFont(font);
        g.setColor(textColor);
        g.drawString(text, p);

        g.setColor(foregroundColor);
        g.drawRect(0, 0, w - 1, h - 1);

        return false;
    }

    @Override
    public boolean drawTextField(NTextField textField, int layer) {
        NGraphics g = textField.getGraphics();

        String text = textField.getText();
        NFont font = textField.getFont();
        NFontMetrics fontMetrics = g.getFontMetrics(font);
        int w = textField.getWidth();
        int h = textField.getHeight();
        int backgroundColor = textField.getBackgroundColor();
        int offset = textField.getViewOffset();
        NTextField.NTextSelection selection = textField.getSelection();
        int caretPosition = textField.getCaretPosition();
        int selectionLength = selection.length();
        int textY = NLabel.alignLabelY(null, text, h, fontMetrics, NVerticalTextAlignment.CENTER).b;
        int ascent = fontMetrics.getAscent();
        String selectionText = text.substring(selection.begin, selection.end);
        int selectionRectangleX = fontMetrics.getWidth(text.substring(0, selection.begin)) - offset;
        int selectionRectangleY = textY - ascent;
        int selectionRectangleW = fontMetrics.getWidth(selectionText);
        int textH = fontMetrics.getAscent() + fontMetrics.getDescent();
        int caretX = fontMetrics.getWidth(text.substring(0, caretPosition)) - offset;

        boolean caretVisible = ((System.currentTimeMillis() - textField.getLastKeyPressTime()) / tweaks.caretBlinkDuration) % 2 == 0;
        caretVisible &= textField.isKeyboardNeeded();
        if (backgroundColor == NColor.NONE) backgroundColor = tweaks.defaultBackgroundColor;
        int foregroundColor = textField.getForegroundColor();
        if (foregroundColor == NColor.NONE) foregroundColor = tweaks.defaultForegroundColor;
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
            g.setOpacity(tweaks.disabledCoverOpacity * textField.getOpacity());
            g.setColor(tweaks.disabledCoverColor);
            g.fillRect(new NRectangle(NPoint.ZERO, textField.getSize()));
        }

        if (caretX < 2) {
            textField.setViewOffset(caretX + offset);
        } else if (caretX > w - 2) {
            textField.setViewOffset(caretX + offset - w + 2);
        }

        return false;
    }

    public static final class UITweaks {
        public int defaultBackgroundColor;
        public int defaultForegroundColor;
        public double pressColorRatio;
        public double hoverColorRatio;
        public double focusColorRatio;
        public int disabledCoverColor;
        public double disabledCoverOpacity;
        public int caretBlinkDuration;

        public double radiobuttonCheckRatio;

        public double checkboxCheckX1, checkboxCheckY1;
        public double checkboxCheckX2, checkboxCheckY2;
        public double checkboxCheckX3, checkboxCheckY3;


        public UITweaks() { }

        public void setDefaults() {
            pressColorRatio = 0.25;
            hoverColorRatio = 0.75;
            focusColorRatio = 0.5;
            disabledCoverOpacity = 0.5;
            caretBlinkDuration = 500;

            radiobuttonCheckRatio = 0.6;

            checkboxCheckX1 = 0.2;
            checkboxCheckY1 = 0.2;
            checkboxCheckX2 = 0.5;
            checkboxCheckY2 = 0.8;
            checkboxCheckX3 = 0.8;
            checkboxCheckY3 = 0.2;
        }

        public void setDarkColors() {
            defaultBackgroundColor = NColor.BLACK;
            defaultForegroundColor = NColor.WHITE;
            disabledCoverColor = NColor.BLACK;
        }

        public void setLightColors() {
            defaultBackgroundColor = NColor.WHITE;
            defaultForegroundColor = NColor.BLACK;
            disabledCoverColor = NColor.WHITE;
        }
    }
}
