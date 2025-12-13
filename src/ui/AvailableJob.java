package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import service.API;
import ui.component.Navbar;

public class AvailableJob extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);

    // Fonts
    private static final Font FONT_TITLE = new Font("SansSerif", Font.PLAIN, 28);
    private static final Font FONT_CARD_TITLE = new Font("Tahoma", Font.BOLD, 16);
    private static final Font FONT_CARD_TEXT = new Font("Tahoma", Font.PLAIN, 12);
    private static final Font FONT_CARD_WARN = new Font("Tahoma", Font.BOLD, 12);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 13);

    public AvailableJob() {
        initialize();
    }

    private void initialize() {
        setTitle("Available Job");
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

        // Job Grid (โหลดจาก DB แล้วแสดงจริง)
        content.add(createJobGrid(), BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
    }

    // ========= TITLE & SEARCH ==========
    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel title = new JLabel("Available Job");
        title.setFont(FONT_TITLE);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(220, 40));

        JTextField searchField = new JTextField("  Search") {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof MyJob.RoundedBorder) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fill(((MyJob.RoundedBorder) getBorder()).getShape(0, 0, getWidth() - 1, getHeight() - 1));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        searchField.setOpaque(false);
        searchField.setBorder(new MyJob.RoundedBorder(25)); // ต้องมี class MyJob.RoundedBorder ในโปรเจกต์
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

        // ===== โหลดงานจาก DB =====
        ArrayList<API> jobs = API.getJobs();

        if (jobs == null || jobs.isEmpty()) {
            JLabel empty = new JLabel("ไม่มีงานให้แสดง (No Jobs Found)");
            empty.setFont(FONT_CARD_WARN);
            empty.setForeground(Color.DARK_GRAY);
            grid.add(empty);
        } else {
            for (API work : jobs) {
                // ส่ง object work เข้าไปสร้างการ์ด
                grid.add(createJobCard(work));
            }
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    // ========= JOB CARD ==========
    // แก้ไขให้รับ parameter API work
    private JPanel createJobCard(API work) {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 20, 20);
                // BG
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        card.setPreferredSize(new Dimension(500, 200));

        // --- 1. Left: Image ---
        // ใช้สีส้มน้ำตาลเป็น Placeholder
        JLabel image = new JLabel(new ImageIcon(createPlaceholderImage(100, new Color(139, 69, 19))));
        image.setVerticalAlignment(SwingConstants.TOP);
        card.add(image, BorderLayout.WEST);

        // --- 2. Center: Details ---
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);

        // ดึงข้อมูลจริงจาก Object API work
        String titleText = safe(work.title);
        String locationText = safe(work.location);
        String typeText = safe(work.jobType);

        // แยกวันที่และเวลา
        String[] dateParts = splitDateTime(work.dateTime);
        String dateStr = dateParts[0];
        String timeStr = (dateParts.length > 1) ? dateParts[1] : "";

        // Title Label
        JLabel title = new JLabel("<html>" + titleText + "</html>");
        title.setFont(FONT_CARD_TITLE);

        // Date Label
        JLabel date = new JLabel("<html><font color='#FFD700'>☀</font> วันที่: " + dateStr
                + "<br>&nbsp;&nbsp;&nbsp;เวลา: " + timeStr + "</html>");
        date.setFont(FONT_CARD_TEXT);

        // Location Label
        JLabel loc = new JLabel("<html><font color='red'>📍</font> " + locationText + "</html>");
        loc.setFont(FONT_CARD_TEXT);

        // Warning / Special Label
        JLabel warning = new JLabel("<html><font color='red'>🚨 รับ: " + work.vacancies + " อัตรา</font></html>");
        warning.setFont(FONT_CARD_WARN);

        // Hours / Type Label
        JLabel hours = new JLabel("<html>🔘 ประเภท: " + typeText + " (" + work.workingHours + " ชม.)</html>");
        hours.setFont(FONT_CARD_TEXT);

        details.add(title);
        details.add(Box.createVerticalStrut(6));
        details.add(date);
        details.add(Box.createVerticalStrut(3));
        details.add(loc);
        details.add(Box.createVerticalStrut(3));
        details.add(warning);
        details.add(Box.createVerticalStrut(3));
        details.add(hours);

        card.add(details, BorderLayout.CENTER);

        // --- 3. Bottom: Buttons ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton detailsBtn = createRoundedButton("Details", Color.BLACK, Color.WHITE, false);
        detailsBtn.addActionListener(e -> {
            String detailID = work.jobId;
            SwingUtilities.invokeLater(() -> new DetailJob(detailID).setVisible(true));
            dispose();
        });

        JButton acceptBtn = createRoundedButton("Accept Job", Color.WHITE, new Color(255, 160, 122), true);

        btnPanel.add(detailsBtn);
        btnPanel.add(acceptBtn);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    // ========= HELPERS ==========

    // แยก method สร้างปุ่มออกมาเพื่อให้โค้ดอ่านง่ายขึ้น
    private JButton createRoundedButton(String text, Color fgColor, Color bgColor, boolean filled) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (filled) {
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                } else {
                    g2.setColor(bgColor); // พื้นหลังปุ่ม
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                    g2.setColor(fgColor); // ขอบ
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(fgColor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(110, 32));
        return btn;
    }

    private Image createPlaceholderImage(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillRect(0, 0, size, size);
        g2.dispose();
        return img;
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }

    private String[] splitDateTime(String dateTime) {
        if (dateTime == null || dateTime.trim().isEmpty()) {
            return new String[] { "-", "-" };
        }
        // ตัด .0 ข้างหลังทิ้งก่อน (ถ้ามี)
        if (dateTime.endsWith(".0")) {
            dateTime = dateTime.substring(0, dateTime.length() - 2);
        }
        String[] parts = dateTime.trim().split("\\s+");
        if (parts.length == 1)
            return new String[] { parts[0], "" };
        return new String[] { parts[0], parts[1] };
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            AvailableJob s = new AvailableJob();
            s.setVisible(true);
        });
    }
}