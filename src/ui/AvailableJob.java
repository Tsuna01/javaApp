package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import service.API;
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
    private ArrayList<API> allJobs; // เก็บงานทั้งหมดที่โหลดจาก DB ครั้งแรก
    private JPanel gridPanel;       // Panel ที่เก็บ Card งาน (เอาไว้สั่ง removeAll/repaint)
    private JTextField searchField; // ช่องค้นหา

    public AvailableJob() {
        // 1. โหลดข้อมูลเตรียมไว้ก่อน
        allJobs = API.getJobs();
        if (allJobs == null) allJobs = new ArrayList<>();

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

        // [Functional] เพิ่ม Logic ค้นหาเมื่อกด Enter
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearchAndFilter(null); // ค้นหาโดยไม่ใช้ Filter (ใช้ Text อย่างเดียว)
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

        // [Functional] เปิด FilterUI และรับค่ากลับมา
        filterBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Filter Jobs", true);
            dialog.setLayout(new BorderLayout());

            FilterUI filterUI = new FilterUI();

            // ดักจับเมื่อกดปุ่ม Apply ใน FilterUI
            filterUI.setOnApplyListener(evt -> {
                FilterUI.FilterData data = filterUI.getFilterData();
                performSearchAndFilter(data); // ส่งข้อมูลไปกรอง
                dialog.dispose(); // ปิดหน้าต่าง
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

        // 1. กรองด้วย Search Text
        if (!searchText.isEmpty()) {
            String lowerSearch = searchText.toLowerCase();
            result = result.stream()
                    .filter(job -> safe(job.title).toLowerCase().contains(lowerSearch) ||
                            safe(job.location).toLowerCase().contains(lowerSearch))
                    .collect(Collectors.toList());
        }

        // 2. กรองด้วย FilterUI Data
        if (filterData != null) {
            // 2.1 Job Type (Volunteer / Paid)
            boolean showVol = filterData.isVolunteer;
            boolean showPaid = filterData.isPaid;

            // ถ้าไม่เลือกอะไรเลย หรือเลือกทั้งคู่ ให้แสดงหมด แต่ถ้าเลือกอย่างใดอย่างหนึ่งให้กรอง
            if (showVol != showPaid) {
                if (showVol) {
                    result = result.stream().filter(j -> isVolunteerJob(j.jobType)).collect(Collectors.toList());
                } else {
                    result = result.stream().filter(j -> !isVolunteerJob(j.jobType)).collect(Collectors.toList());
                }
            }

            // 2.2 Date Range (ข้ามส่วนนี้ถ้าไม่มี Parsing logic ที่แน่นอน)

            // 2.3 Sorting
            if ("Oldest".equals(filterData.sortByDate)) {
                // TODO: Implement Date Sorting if Date is parsable
            } else if ("Newest".equals(filterData.sortByDate)) {
                // TODO: Implement Date Sorting if Date is parsable
            }

            // [FIXED] ใช้ int ตรงๆ ไม่ต้อง parse
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
        gridPanel = new JPanel(new GridLayout(0, 2, 30, 30)); // 2 Columns
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // แสดงผลครั้งแรก (งานทั้งหมด)
        updateJobGrid(allJobs);

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    // [Functional] Method อัพเดท Grid
    private void updateJobGrid(List<API> jobsToShow) {
        gridPanel.removeAll(); // ลบการ์ดเก่าออก

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

        gridPanel.revalidate(); // คำนวณ Layout ใหม่
        gridPanel.repaint();    // วาดใหม่
    }

    // ========= JOB CARD ==========
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

        // Image
        // [Safety] ใช้ Placeholder แทนถ้า ImagePath มีปัญหา
        JLabel image = new JLabel(new ImageIcon(createPlaceholderImage(100, new Color(139, 69, 19))));
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
        // เพิ่ม Logic Accept Job ตรงนี้ถ้าต้องการ

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
}