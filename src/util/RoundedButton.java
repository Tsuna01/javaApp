package util;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    private int radius = 12;

    public RoundedButton(String text) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // padding
        setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // BG color
        g2.setColor(new Color(255, 255, 255, 40)); // ขาวโปร่งใส
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Border
        g2.setColor(new Color(255, 255, 255, 120));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}
