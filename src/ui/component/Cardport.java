package ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Cardport extends JPanel {

    public Cardport() {

        setLayout(null);
        setOpaque(false);
        setPreferredSize(new Dimension(300, 200));

        UIManager.put("Label.font", new Font("Tahoma", Font.PLAIN, 12));
        UIManager.put("Button.font", new Font("Tahoma", Font.PLAIN, 12));

        // ------- CARD ------- //
        JPanel card = new RoundPanel(20);
        card.setLayout(new BorderLayout());
        card.setBounds(0, 0, 300, 200);
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        // ------- LEFT IMAGE ------- //
        JLabel img = new JLabel();
        img.setPreferredSize(new Dimension(90, 130));

        ImageIcon icon = new ImageIcon("event.jpg");
        Image scaled = icon.getImage().getScaledInstance(90, 130, Image.SCALE_SMOOTH);
        img.setIcon(new ImageIcon(scaled));

        // ------- CENTER TEXT ------- //
        JLabel text = new JLabel(
            "<html>"
            + "<div style='font-size:12px; line-height:1.2;'>"
            + "<b style='font-size:14px;'>งานแข่งมาราธอน ครบรอบ 100 ปี</b><br>"
            + "🌟 20 พ.ค. 2568<br>"
            + "⏱ 16:00–20:00<br>"
            + "📍 B3102<br>"
            + "<span style='color:red;'><b>20 ชั่วโมง</b></span>"
            + "</div></html>"
        );

        // ------- BUTTONS ------- //
        JPanel btnBox = new JPanel();
        btnBox.setBackground(Color.WHITE);
        btnBox.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        JButton detailBtn = new JButton("Details");
        detailBtn.setPreferredSize(new Dimension(70, 28));
        styleButton(detailBtn, new Color(230, 230, 230), Color.BLACK);

        JButton acceptBtn = new JButton("Accept");
        acceptBtn.setPreferredSize(new Dimension(80, 28));
        styleButton(acceptBtn, new Color(255, 153, 153), Color.WHITE);

        btnBox.add(detailBtn);
        btnBox.add(acceptBtn);

        // ADD ALL
        card.add(img, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        card.add(btnBox, BorderLayout.SOUTH);

        add(card);
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(12));
    }

    // Panel มุมมน + Shadow
    class RoundPanel extends JPanel {
        private int radius;

        public RoundPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, radius, radius);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedBorder implements javax.swing.border.Border {
        private int radius;

        RoundedBorder(int r) { radius = r; }

        public Insets getBorderInsets(Component c) { return new Insets(6, 6, 6, 6); }
        public boolean isBorderOpaque() { return false; }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            g.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        }
    }
    
}
