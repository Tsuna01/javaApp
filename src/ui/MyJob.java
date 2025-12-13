package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import ui.component.Navbar;

public class MyJob extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);

    // Fonts
    private static final Font FONT_TITLE = new Font("SansSerif", Font.PLAIN, 28);
    private static final Font FONT_CARD_TITLE = new Font("Tahoma", Font.BOLD, 16);
    private static final Font FONT_CARD_STATUS = new Font("Tahoma", Font.BOLD, 14);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 14);

    public MyJob() {
        initialize();
    }

    private void initialize() {
        setTitle("My Job");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        add(mainPanel, BorderLayout.CENTER);

        // ========= HEADER ==========
        mainPanel.add(new Navbar().build(), BorderLayout.NORTH);

        // ========= CONTENT ==========
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Title & Search
        content.add(createTitleSection(), BorderLayout.NORTH);

        // Job Grid
        content.add(createJobGrid(), BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
    }

    // ========= TITLE & SEARCH ==========
    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel title = new JLabel("My Job");
        title.setFont(FONT_TITLE);

        // Search Bar with Filter Icon
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(200, 40));

        JTextField searchField = new JTextField("  Search") {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedBorder) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fill(((RoundedBorder) getBorder()).getShape(0, 0, getWidth() - 1, getHeight() - 1));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        searchField.setOpaque(false);
        searchField.setBorder(new RoundedBorder(25));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        searchPanel.add(searchField, BorderLayout.CENTER);

        panel.add(title, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    // ========= JOB GRID ==========
    private JScrollPane createJobGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 30, 30)); // 2 Columns
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add Job Cards with different statuses
        grid.add(createJobCard("กิจกรรมอบรม ในโครงการ<br>KNOCK KNOCK", "สำเร็จ", true));
        grid.add(createJobCard("กิจกรรมอบรม ในโครงการ<br>KNOCK KNOCK", "กำลังทำ", false));
        grid.add(createJobCard("กิจกรรมอบรม ในโครงการ<br>KNOCK KNOCK", "สำเร็จ", true));
        grid.add(createJobCard("กิจกรรมอบรม ในโครงการ<br>KNOCK KNOCK", "กำลังทำ", false));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    // ========= JOB CARD ==========
    private JPanel createJobCard(String jobTitle, String status, boolean isCompleted) {
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
        detailsBtn.addActionListener(e -> {

        });

        btnPanel.add(detailsBtn);

        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    // ========= HELPERS ==========

    private Image createPlaceholderImage(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillRect(0, 0, size, size);
        g2.dispose();
        return img;
    }

    static class RoundedBorder extends LineBorder {
        private int radius;

        public RoundedBorder(int radius) {
            super(Color.LIGHT_GRAY, 1, true);
            this.radius = radius;
        }

        public Shape getShape(int x, int y, int w, int h) {
            return new RoundRectangle2D.Float(x, y, w, h, radius, radius);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(lineColor);
            g2.draw(getShape(x, y, width - 1, height - 1));
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MyJob m = new MyJob();
            m.setVisible(true);
        });
    }
}
