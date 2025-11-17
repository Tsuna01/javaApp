package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

//---------- คลาสช่วย: การ์ดมุมมนพร้อมเส้นขอบ ----------
public class RoundedPanel extends JPanel {
    private final int radius;
    private final Color bg;
    private final Color stroke;

    public RoundedPanel(int radius, Color bg, Color stroke) {
        this.radius = radius;
        this.bg = bg;
        this.stroke = stroke;
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);
        g2.setColor(stroke);
        g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
