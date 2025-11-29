package ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Cardport extends JPanel {

    // ---------------- Font ------------------
    private static final Font FONT_CARD_TITLE = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_CARD_TEXT = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font FONT_CARD_WARN = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 14);

    public JPanel createJobCard() {

        JPanel card = new JPanel(new BorderLayout(20, 0)) {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 25;
                int shadow = 6;

                // Shadow
                g2.setColor(new Color(0, 0, 0, 35));
                g2.fillRoundRect(shadow, shadow, getWidth() - shadow * 2, getHeight() - shadow * 2, arc, arc);

                // White background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - shadow, getHeight() - shadow, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setPreferredSize(new Dimension(500, 280));

        // ------------- LEFT: IMAGE -------------
        JLabel image = new JLabel(new ImageIcon(createPlaceholderImage(140, 180, new Color(139, 69, 19))));
        image.setPreferredSize(new Dimension(150, 180));
        image.setVerticalAlignment(SwingConstants.TOP);
        card.add(image, BorderLayout.WEST);

        // ------------- CENTER: DETAILS -------------
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);

        JLabel title = new JLabel("<html>กิจกรรมอบรม ในโครงการ<br>KNOCK KNOCK</html>");
        title.setFont(FONT_CARD_TITLE);

        JLabel date = new JLabel(
                "<html>☀ วันอังคารที่ 20 พฤษภาคม 2568<br>&nbsp;&nbsp;&nbsp;เวลา 16:00 - 20:00 น.</html>");
        date.setFont(FONT_CARD_TEXT);

        JLabel loc = new JLabel("<html>📍 ณ อาคารเรียนรวม 1 ห้อง B3102</html>");
        loc.setFont(FONT_CARD_TEXT);

        JLabel warning = new JLabel("<html><font color='red'>🚨 พิเศษรับชั่วโมงชดใช้สังคม 20 ชม.!!</font></html>");
        warning.setFont(FONT_CARD_WARN);

        JLabel hours = new JLabel("<html>🔘 จิตอาสา 5 ชั่วโมง</html>");
        hours.setFont(FONT_CARD_TEXT);

        details.add(title);
        details.add(Box.createVerticalStrut(10));
        details.add(date);
        details.add(Box.createVerticalStrut(5));
        details.add(loc);
        details.add(Box.createVerticalStrut(5));
        details.add(warning);
        details.add(Box.createVerticalStrut(5));
        details.add(hours);

        card.add(details, BorderLayout.CENTER);

        // ------------- BOTTOM: BUTTON AREA -------------
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // ---- Details Button ----
        JButton detailsBtn = new JButton("Details") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.BLACK);
                g2.drawRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        detailsBtn.setFont(FONT_BTN);
        detailsBtn.setForeground(Color.BLACK);
        detailsBtn.setContentAreaFilled(false);
        detailsBtn.setBorderPainted(false);
        detailsBtn.setFocusPainted(false);
        detailsBtn.setPreferredSize(new Dimension(110, 36));

        // ---- Accept Button ----
        JButton acceptBtn = new JButton("Accept Job") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 140, 100)); // Soft Orange
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        acceptBtn.setFont(FONT_BTN);
        acceptBtn.setForeground(Color.WHITE);
        acceptBtn.setContentAreaFilled(false);
        acceptBtn.setBorderPainted(false);
        acceptBtn.setFocusPainted(false);
        acceptBtn.setPreferredSize(new Dimension(140, 36));

        btnPanel.add(detailsBtn);
        btnPanel.add(acceptBtn);

        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    // ---------- CREATE PLACEHOLDER IMAGE ----------
    private Image createPlaceholderImage(int w, int h, Color c) {
        Image img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setColor(c);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        return img;
    }
}
