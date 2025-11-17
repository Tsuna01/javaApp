package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.Border;

public class RoundedLineBorder implements Border {
    private final Color color;
    private final int thickness;
    private final int radius;

    public RoundedLineBorder(Color color, int thickness, int radius) {
        this.color = color;
        this.thickness = thickness;
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }

    @Override
    public boolean isBorderOpaque() { return false; }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(color);
        // -1 ให้เส้นไม่ถูกตัดขอบขวา/ล่าง
        g2.drawRoundRect(x + thickness/2, y + thickness/2,
                         w - thickness, h - thickness, radius, radius);
        g2.dispose();
    }
}
