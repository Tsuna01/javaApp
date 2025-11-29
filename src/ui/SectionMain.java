package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import ui.component.Navbar;

public class SectionMain extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);

    // Fonts
    private static final Font FONT_HEADER_LINK = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.PLAIN, 28);
    private static final Font FONT_CARD_TITLE = new Font("Tahoma", Font.BOLD, 18);
    private static final Font FONT_CARD_TEXT = new Font("Tahoma", Font.PLAIN, 14);
    private static final Font FONT_CARD_WARN = new Font("Tahoma", Font.BOLD, 14);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 14);

    public SectionMain() {
        initialize();
    }

    private void initialize() {
        setTitle("Main");
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

    // ========= HEADER ==========
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 130, 140), getWidth(), 0,
                        new Color(255, 210, 160));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(1200, 80));
        header.setBorder(new EmptyBorder(0, 50, 0, 50));

        // Left: Avatar + Name
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        left.setOpaque(false);

        JLabel avatar = new JLabel(new ImageIcon(createPlaceholderImage(50, Color.WHITE)));
        JLabel name = new JLabel("Elysia Athome");
        name.setFont(new Font("SansSerif", Font.BOLD, 20));
        name.setForeground(Color.WHITE);

        left.add(avatar);
        left.add(name);

        // Right: Links
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 20));
        right.setOpaque(false);

        // Available Job Button (Active style)
        JButton availableJobBtn = new JButton("Available Job") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 50)); // Semi-transparent white
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        availableJobBtn.setFont(FONT_HEADER_LINK);
        availableJobBtn.setForeground(Color.WHITE);
        availableJobBtn.setContentAreaFilled(false);
        availableJobBtn.setBorderPainted(false);
        availableJobBtn.setPreferredSize(new Dimension(120, 40));

        JLabel myJobLink = new JLabel("My Job");
        myJobLink.setFont(FONT_HEADER_LINK);
        myJobLink.setForeground(Color.WHITE);

        right.add(availableJobBtn);
        right.add(myJobLink);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ========= TITLE & SEARCH ==========
    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel title = new JLabel("Available Jobs(4)");
        title.setFont(FONT_TITLE);

        // Search Bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(500, 50));

        JTextField searchField = new JTextField("Search ************") {
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
        searchField.setBorder(new RoundedBorder(40));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        panel.add(title, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.EAST);

        return panel;
    }

    // ========= JOB GRID ==========
    private JScrollPane createJobGrid() {
        JPanel grid = new JPanel(new GridLayout(0, 2, 30, 30)); // 2 Columns
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add 4 Job Cards
        for (int i = 0; i < 4; i++) {
            grid.add(createJobCard());
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    // ========= JOB CARD ==========
    private JPanel createJobCard() {
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
        card.setPreferredSize(new Dimension(500, 280));

        // Left: Image
        JLabel image = new JLabel(new ImageIcon(createPlaceholderImage(120, new Color(139, 69, 19)))); // Brown
                                                                                                       // placeholder
        image.setPreferredSize(new Dimension(130, 180));
        image.setVerticalAlignment(SwingConstants.TOP);

        card.add(image, BorderLayout.WEST);

        // Center: Details
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);

        JLabel title = new JLabel("<html>กิจกรรมอบรม ในโครงการ<br>KNOCK KNOCK</html>");
        title.setFont(FONT_CARD_TITLE);

        JLabel date = new JLabel(
                "<html><font color='#FFD700'>☀</font> วันอังคารที่ 20 พฤษภาคม 2568<br>&nbsp;&nbsp;&nbsp;เวลา 16:00-20:00 น.</html>");
        date.setFont(FONT_CARD_TEXT);

        JLabel loc = new JLabel("<html><font color='red'>📍</font> ณ อาคารเรียนรวม 1 ห้อง B3102</html>");
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

        // Bottom: Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
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

        JButton acceptBtn = new JButton("Accept Job") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 160, 122)); // Salmon/Orange color
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        acceptBtn.setFont(FONT_BTN);
        acceptBtn.setForeground(Color.WHITE);
        acceptBtn.setContentAreaFilled(false);
        acceptBtn.setBorderPainted(false);
        acceptBtn.setPreferredSize(new Dimension(120, 35));

        btnPanel.add(detailsBtn);
        btnPanel.add(acceptBtn);

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

    private static class RoundedBorder extends LineBorder {
        private int radius;

        public RoundedBorder(int radius) {
            super(Color.BLACK, 1, true);
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
            SectionMain s = new SectionMain();
            s.setVisible(true);
        });
    }
}
