package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// [Import สำหรับ Database]
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import service.*;
import ui.component.FilterUI;
import ui.component.Navbar;

public class AvailableJob extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);

    // Fonts
    private static final Font FONT_TITLE = new Font("SansSerif", Font.PLAIN, 28);
    private static final Font FONT_CARD_TITLE = new Font("Tahoma", Font.BOLD, 16);
    private static final Font FONT_CARD_TEXT = new Font("Tahoma", Font.PLAIN, 12);
    private static final Font FONT_CARD_WARN = new Font("Tahoma", Font.BOLD, 12);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 13);

    // Data Management
    private ArrayList<API> allJobs;
    private JPanel gridPanel;
    private JTextField searchField;

    public AvailableJob() {
        // 1. โหลดข้อมูลงานทั้งหมดมาก่อน
        allJobs = API.getJobs();
        if (allJobs == null) allJobs = new ArrayList<>();

        // 2. กรองงานที่สมัครไปแล้วทิ้ง ก่อนจะเริ่มวาดหน้าจอ
        filterAppliedJobs();

        initialize();
    }

    // ฟังก์ชันสำหรับกรองงานที่ user ปัจจุบันสมัครไปแล้ว
    private void filterAppliedJobs() {
        // ถ้ายังไม่ได้ Login ให้ข้ามไป
        if (Auth.getAuthUser() == null) return;

        List<String> appliedJobIds = new ArrayList<>();
        String currentStdId = null;

        // เช็คว่าเป็น Student หรือไม่ เพื่อดึง ID ให้ถูกต้อง
        if (Auth.getAuthUser() instanceof Student) {
            Student s = (Student) Auth.getAuthUser();
            currentStdId = s.getStdId();
        } else {
            currentStdId = Auth.getAuthUser().getStd_id();
        }

        System.out.println("Filtering jobs for Student ID: " + currentStdId);

        if (currentStdId == null) {
            System.err.println("Warning: std_id is null for user " + Auth.getAuthUser().getName());
            return;
        }

        String sql = "SELECT job_id FROM job_assignment WHERE std_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentStdId);
            ResultSet rs = pstmt.executeQuery();

            // เก็บ ID ของงานที่สมัครแล้วไว้ใน List
            while (rs.next()) {
                appliedJobIds.add(String.valueOf(rs.getInt("job_id")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ลบงานที่มี ID ตรงกับใน appliedJobIds ออกจาก allJobs
        allJobs.removeIf(job -> appliedJobIds.contains(job.jobId));
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

        // Job Grid Area
        content.add(createJobGridScroll(), BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
    }

    // ========= TITLE & SEARCH ==========
    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel title = new JLabel("Available Job");
        title.setFont(FONT_TITLE);

        // Container for search field and filter button
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchContainer.setOpaque(false);

        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(220, 40));

        searchField = new JTextField("  Search") {
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

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearchAndFilter(null);
                }
            }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);

        // Filter Button
        JButton filterBtn = new JButton("☰") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Color.BLACK);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        filterBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        filterBtn.setForeground(Color.BLACK);
        filterBtn.setContentAreaFilled(false);
        filterBtn.setBorderPainted(false);
        filterBtn.setPreferredSize(new Dimension(40, 40));
        filterBtn.setFocusPainted(false);
        filterBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        filterBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Filter Jobs", true);
            dialog.setLayout(new BorderLayout());

            FilterUI filterUI = new FilterUI();

            filterUI.setOnApplyListener(evt -> {
                FilterUI.FilterData data = filterUI.getFilterData();
                performSearchAndFilter(data);
                dialog.dispose();
            });

            dialog.add(filterUI);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        searchContainer.add(searchPanel);
        searchContainer.add(filterBtn);

        panel.add(title, BorderLayout.WEST);
        panel.add(searchContainer, BorderLayout.EAST);

        return panel;
    }

    // ========= LOGIC: SEARCH & FILTER ==========
    private void performSearchAndFilter(FilterUI.FilterData filterData) {
        String searchText = searchField.getText().trim();
        if (searchText.equalsIgnoreCase("Search")) searchText = "";

        List<API> result = new ArrayList<>(allJobs);

        if (!searchText.isEmpty()) {
            String lowerSearch = searchText.toLowerCase();
            result = result.stream()
                    .filter(job -> safe(job.title).toLowerCase().contains(lowerSearch) ||
                            safe(job.location).toLowerCase().contains(lowerSearch))
                    .collect(Collectors.toList());
        }

        if (filterData != null) {
            boolean showVol = filterData.isVolunteer;
            boolean showPaid = filterData.isPaid;

            if (showVol != showPaid) {
                if (showVol) {
                    result = result.stream().filter(j -> isVolunteerJob(j.jobType)).collect(Collectors.toList());
                } else {
                    result = result.stream().filter(j -> !isVolunteerJob(j.jobType)).collect(Collectors.toList());
                }
            }

            if ("Low -> High".equals(filterData.sortByHours)) {
                result.sort(Comparator.comparingInt(j -> j.workingHours));
            } else if ("High -> Low".equals(filterData.sortByHours)) {
                result.sort((j1, j2) -> j2.workingHours - j1.workingHours);
            }
        }

        updateJobGrid(result);
    }

    private boolean isVolunteerJob(String type) {
        return safe(type).toLowerCase().contains("volunteer") || safe(type).toLowerCase().contains("อาสา");
    }

    // ========= JOB GRID SYSTEM ==========
    private JScrollPane createJobGridScroll() {
        gridPanel = new JPanel(new GridLayout(0, 2, 30, 30));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        updateJobGrid(allJobs);

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    private void updateJobGrid(List<API> jobsToShow) {
        gridPanel.removeAll();

        if (jobsToShow == null || jobsToShow.isEmpty()) {
            JLabel empty = new JLabel("ไม่พบงานที่คุณค้นหา (No Jobs Found)");
            empty.setFont(FONT_CARD_WARN);
            empty.setForeground(Color.DARK_GRAY);

            JPanel emptyWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            emptyWrapper.setOpaque(false);
            emptyWrapper.add(empty);
            gridPanel.add(emptyWrapper);
        } else {
            for (API work : jobsToShow) {
                gridPanel.add(createJobCard(work));
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    // ========= JOB CARD ==========
    private JPanel createJobCard(API work) {
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

        // Image
        String path = work.imagePath;
        ImageIcon icon = loadAndResizeImage(path, 100, 100);

        JLabel image = new JLabel(icon);
        image.setVerticalAlignment(SwingConstants.TOP);
        card.add(image, BorderLayout.WEST);

        // Details
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);

        String titleText = safe(work.title);
        String locationText = safe(work.location);
        String typeText = safe(work.jobType);

        String[] dateParts = splitDateTime(work.dateTime);
        String dateStr = dateParts[0];
        String timeStr = (dateParts.length > 1) ? dateParts[1] : "";

        JLabel title = new JLabel("<html>" + titleText + "</html>");
        title.setFont(FONT_CARD_TITLE);

        JLabel date = new JLabel("<html><font color='#FFD700'>☀</font> วันที่: " + dateStr
                + "<br>&nbsp;&nbsp;&nbsp;เวลา: " + timeStr + "</html>");
        date.setFont(FONT_CARD_TEXT);

        JLabel loc = new JLabel("<html><font color='red'>📍</font> " + locationText + "</html>");
        loc.setFont(FONT_CARD_TEXT);

        JLabel warning = new JLabel("<html><font color='red'>🚨 รับ: " + work.vacancies + " อัตรา</font></html>");
        warning.setFont(FONT_CARD_WARN);

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

        // Buttons
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

        acceptBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "คุณต้องการรับงานนี้ใช่หรือไม่?",
                    "ยืนยันการรับงาน",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int id = Integer.parseInt(work.jobId);

                    // [แก้ไขแล้ว] ส่งแค่ id และ workingHours (ไม่ต้องส่ง title)
                    boolean success = JobManager.applyJob(id, work.workingHours);

                    if (success) {
                        JOptionPane.showMessageDialog(this, "รับงานสำเร็จ! (Applied Successfully)");

                        // เมื่อกด OK -> ปิดหน้านี้ -> เปิดหน้า AvailableJob ใหม่
                        // ซึ่งหน้าใหม่นี้จะวิ่งเข้า Constructor -> filterAppliedJobs -> งานจะหายไป
                        new AvailableJob().setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "ไม่สามารถรับงานได้ (อาจเกิดข้อผิดพลาดหรือรับงานไปแล้ว)",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Job ID ไม่ถูกต้อง", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnPanel.add(detailsBtn);
        btnPanel.add(acceptBtn);
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

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            AvailableJob s = new AvailableJob();
            s.setVisible(true);
        });
    }

    private static class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        RoundedBorder(int radius) { this.radius = radius; }
        public Insets getBorderInsets(Component c) { return new Insets(radius + 1, radius + 1, radius + 2, radius); }
        public boolean isBorderOpaque() { return true; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
        public Shape getShape(int x, int y, int w, int h) {
            return new java.awt.geom.RoundRectangle2D.Float(x, y, w, h, radius, radius);
        }
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
}