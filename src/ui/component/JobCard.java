package ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JobCard {

    private static final Font FONT_CARD_TITLE = new Font("Tahoma", Font.BOLD, 16);
    private static final Font FONT_CARD_STATUS = new Font("Tahoma", Font.BOLD, 14);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 14);

    // Changed to 'static' and renamed to 'createCard' for clarity
    public static JPanel createCard(String jobTitle, String status, boolean isCompleted) {
        JPanel card = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);

                // BG
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setPreferredSize(new Dimension(500, 200));

        // Left: Image
        JLabel image = new JLabel(new ImageIcon(createPlaceholderImage(120, new Color(200, 100, 50))));
        image.setPreferredSize(new Dimension(130, 150));
        image.setVerticalAlignment(SwingConstants.TOP);

        card.add(image, BorderLayout.WEST);

        // Center: Details
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);

        JLabel title = new JLabel("<html>" + jobTitle + "</html>");
        title.setFont(FONT_CARD_TITLE);

        // Status label with color
        JLabel statusLabel = new JLabel("<html>Status : <font color='" +
                (isCompleted ? "#4CAF50" : "#FF9800") + "'>" + status + "</font></html>");
        statusLabel.setFont(FONT_CARD_STATUS);

        details.add(title);
        details.add(Box.createVerticalStrut(15));
        details.add(statusLabel);
        details.add(Box.createVerticalGlue());

        card.add(details, BorderLayout.CENTER);

        // Bottom: Details Button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton detailsBtn = new JButton("Details") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.setColor(Color.BLACK);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        detailsBtn.setFont(FONT_BTN);
        detailsBtn.setForeground(Color.BLACK);
        detailsBtn.setContentAreaFilled(false);
        detailsBtn.setBorderPainted(false);
        detailsBtn.setPreferredSize(new Dimension(100, 35));
        detailsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanel.add(detailsBtn);

        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    // Helper must be static to be called by static createCard
    private static Image createPlaceholderImage(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillRect(0, 0, size, size);
        g2.dispose();
        return img;
    }
}