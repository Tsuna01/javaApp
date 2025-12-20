package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import model.JobAssignment;
// ตรวจสอบ package ของ Student ให้ตรงกับโปรเจกต์คุณ (model หรือ service)
import service.API;
import service.Auth;
import service.Student;
import service.User;
import ui.component.Navbar;

public class MyJob extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);

    // Fonts
    private static final Font FONT_TITLE = new Font("SansSerif", Font.PLAIN, 28);
    private static final Font FONT_CARD_TITLE = new Font("Tahoma", Font.BOLD, 16);
    private static final Font FONT_CARD_TEXT = new Font("Tahoma", Font.PLAIN, 12);
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

        JLabel title = new JLabel("My Job (งานของฉัน)");
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

        // 1. ดึงรหัสนักศึกษาปัจจุบัน
        String currentStdId = null;
        User currentUser = Auth.getAuthUser();

        if (currentUser instanceof Student) {
            currentStdId = ((Student) currentUser).getStdId();
        } else if (currentUser != null) {
            currentStdId = currentUser.getStd_id();
        }

        // 2. ดึงข้อมูลงานเฉพาะของคนนี้ (ใช้ getUserAssign แทน getJobAssign)
        if (currentStdId != null) {
            ArrayList<JobAssignment> myJobs = API.getUserAssign(currentStdId);

            if (myJobs.isEmpty()) {
                JLabel emptyLabel = new JLabel("คุณยังไม่มีงานที่รับไว้ (No jobs assigned)");
                emptyLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
                grid.add(emptyLabel);
            } else {
                for (JobAssignment work : myJobs) {
                    // [แก้ไขแล้ว] เพิ่มการ์ดลงใน Grid
                    grid.add(createJobCard(work));
                }
            }
        } else {
            grid.add(new JLabel("Please Login first"));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    // ========= JOB CARD ==========
    private JPanel createJobCard(JobAssignment work) {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 20, 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));
        card.setPreferredSize(new Dimension(500, 200));

        // Image - Load from database
        String imagePath = work.getImagePath();
        ImageIcon icon = loadAndResizeImage(imagePath, 100, 100);

        JLabel image = new JLabel(icon);
        image.setVerticalAlignment(SwingConstants.TOP);
        card.add(image, BorderLayout.WEST);

        // Details
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);

        String titleText = safe(work.getTitle());
        String locationText = safe(work.getLocation());
        String dateText = safe(work.getDateTime());

        // Split date and time
        String[] dateParts = splitDateTime(dateText);
        String dateStr = dateParts[0];
        String timeStr = (dateParts.length > 1) ? dateParts[1] : "";

        JLabel title = new JLabel("<html>" + titleText + "</html>");
        title.setFont(FONT_CARD_TITLE);

        JLabel date = new JLabel("<html><font color='#FFD700'>☀</font> วันที่: " + dateStr
                + "<br>&nbsp;&nbsp;&nbsp;เวลา: " + timeStr + "</html>");
        date.setFont(FONT_CARD_TEXT);

        JLabel loc = new JLabel("<html><font color='red'>📍</font> " + locationText + "</html>");
        loc.setFont(FONT_CARD_TEXT);

        // Status label with color
        String statusText = work.getStatus();
        boolean isCompleted = "complete".equalsIgnoreCase(statusText);

        JLabel statusLabel = new JLabel("<html>🔘 Status: <font color='" +
                (isCompleted ? "#4CAF50" : "#FF9800") + "'>" + statusText + "</font></html>");
        statusLabel.setFont(FONT_CARD_STATUS);

        JLabel hoursLabel = new JLabel("<html>⏱ Hours: " + work.getHoursAmount() + " ชม.</html>");
        hoursLabel.setFont(FONT_CARD_TEXT);

        details.add(title);
        details.add(Box.createVerticalStrut(6));
        details.add(date);
        details.add(Box.createVerticalStrut(3));
        details.add(loc);
        details.add(Box.createVerticalStrut(3));
        details.add(statusLabel);
        details.add(Box.createVerticalStrut(3));
        details.add(hoursLabel);

        card.add(details, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton detailsBtn = createRoundedButton("View Job", Color.BLACK, Color.WHITE, false);
        detailsBtn.addActionListener(e -> {
            String jobIdStr = String.valueOf(work.getJobId());
            new DetailJob(jobIdStr).setVisible(true);
            dispose();
        });

        btnPanel.add(detailsBtn);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    // ========= HELPERS ==========

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
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                    g2.setColor(fgColor);
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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        if (dateTime.endsWith(".0")) {
            dateTime = dateTime.substring(0, dateTime.length() - 2);
        }
        String[] parts = dateTime.trim().split("\\s+");
        if (parts.length == 1)
            return new String[] { parts[0], "" };
        return new String[] { parts[0], parts[1] };
    }

    private ImageIcon loadAndResizeImage(String imagePath, int width, int height) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return new ImageIcon(createPlaceholderImage(width, new Color(139, 69, 19)));
        }

        try {
            BufferedImage originalImage = null;

            if (imagePath.startsWith("http")) {
                originalImage = ImageIO.read(new URL(imagePath));
            } else {
                originalImage = ImageIO.read(new File(imagePath));
            }

            if (originalImage != null) {
                Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }

        } catch (Exception e) {
            System.err.println("Load image error: " + imagePath);
        }

        return new ImageIcon(createPlaceholderImage(width, new Color(139, 69, 19)));
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