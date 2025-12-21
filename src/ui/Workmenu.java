package ui;

import service.API;
import service.Auth;
import service.CompletedAssignment;
import service.WorkerManager;
import ui.component.Editperson;
import ui.component.Navbar;
import util.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class Workmenu extends JFrame {

    // Center Panel Components
    private JLabel centerCardTitle;
    private JTextArea centerDetailsText;
    private JLabel centerImageLabel;
    private JPanel centerDetailsPanel;
    private JLabel centerDateLabel;
    private JLabel centerEndDateLabel;
    private JLabel centerLocationLabel;
    private JLabel centerVacanciesLabel;
    private JLabel centerHoursLabel;

    public String statusText;
    public Color statusColor;

    // Store current job for delete action
    private API currentSelectedJob;
    public String Unique = "";

    // Store job history list
    private ArrayList<API> jobHistory;

    // Right column worker list panel
    private JPanel workerListPanel;

    // Buttons to hide when status is complete
    private JButton completeBtn;
    private JButton deleteBtn;
    private JButton editBtn;

    // Thai Month Array for cleaner date formatting
    private static final String[] THAI_MONTHS = {
            "ไม่ทราบเดือน", "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
            "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
    };

    public Workmenu() {
        initialize();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                // Optional: Set system look and feel for better native integration
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Workmenu frame = new Workmenu();
            frame.setVisible(true);
        });
    }

    private void initialize() {
        // ===== FRAME =====
        setTitle("Work MENU");
        setSize(1550, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== MAIN CONTAINER =====
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(245, 245, 245));
        add(mainContainer, BorderLayout.CENTER);

        // ===== TOP SECTION =====
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(new Color(245, 245, 245));

        // Header
        add(new Navbar().build(), BorderLayout.NORTH);
        mainContainer.add(topSection, BorderLayout.NORTH);

        // ===== CONTENT BODY =====
        JPanel contentBody = createContentBody();
        mainContainer.add(contentBody, BorderLayout.CENTER);

        // Initialize default selection safely
        if (jobHistory != null && !jobHistory.isEmpty()) {
            updateJobDetails(jobHistory.get(0));
        }
    }

    private JPanel createContentBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 15, 0, 15);
        gbc.weighty = 1.0;

        // Left Column: Job History
        gbc.gridx = 0;
        gbc.weightx = 0.20; // Adjusted based on your comment
        body.add(createLeftColumn(), gbc);

        // Center Column: Job Details
        gbc.gridx = 1;
        gbc.weightx = 0.45; // Adjusted based on your comment
        body.add(createCenterColumn(), gbc);

        // Right Column: Worker List
        gbc.gridx = 2;
        gbc.weightx = 0.35; // Increased width for better visibility
        body.add(createRightColumn(), gbc);

        return body;
    }

    private JPanel createLeftColumn() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("ประวัติการจ้างงาน");
        title.setFont(new Font("SansSerif", Font.PLAIN, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(0, 30, 15, 30));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        // Null safety for Auth
        int userId = (Auth.getAuthUser() != null) ? Auth.getAuthUser().getId() : 0;
        jobHistory = API.getHistoryJobAdmin(userId);

        if (jobHistory == null || jobHistory.isEmpty()) {
            JLabel noJobsLabel = new JLabel("ไม่มีประวัติการจ้างงาน");
            noJobsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            noJobsLabel.setForeground(Color.GRAY);
            noJobsLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Wrapper for centering
            JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            centerWrapper.setOpaque(false);
            centerWrapper.add(noJobsLabel);
            listPanel.add(centerWrapper);
        } else {
            for (int i = 0; i < jobHistory.size(); i++) {

                API job = jobHistory.get(i);
                if (job.status != null) {
                    switch (job.status.toLowerCase()) {
                        case "pending":
                            statusText = "รอดำเนินการ";
                            statusColor = new Color(250, 216, 61);
                            break;
                        case "done":
                        case "complete":
                            statusText = "เสร็จสิ้น";
                            statusColor = new Color(40, 167, 69);
                            break;
                        case "cancelled":
                            statusText = "ยกเลิก";
                            statusColor = new Color(220, 53, 69);
                            break;
                        default:
                            statusText = job.status;
                            statusColor = Color.GRAY;
                    }
                } else {
                    statusText = "รอดำเนินการ";
                    statusColor = new Color(250, 216, 61);
                }
                String status = statusText;

                JPanel card = createJobHistoryCard(status, job, statusColor);
                listPanel.add(card);

                if (i < jobHistory.size() - 1) {
                    listPanel.add(Box.createVerticalStrut(15));
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCenterColumn() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("รายละเอียดงาน");
        title.setFont(new Font("SansSerif", Font.PLAIN, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel detailCard = new JPanel(new BorderLayout(30, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
            }
        };
        detailCard.setOpaque(false);
        detailCard.setBorder(new EmptyBorder(30, 10, 30, 10));

        centerImageLabel = new JLabel();
        centerImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerImageLabel.setVerticalAlignment(SwingConstants.TOP);
        centerImageLabel.setPreferredSize(new Dimension(250, 300));
        centerImageLabel.setIcon(loadAndResizeImage(null, 250, 300));
        detailCard.add(centerImageLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        centerDetailsPanel = new JPanel();
        centerDetailsPanel.setLayout(new BoxLayout(centerDetailsPanel, BoxLayout.Y_AXIS));
        centerDetailsPanel.setOpaque(false);

        centerCardTitle = new JLabel("เลือกงานเพื่อดูรายละเอียด");
        centerCardTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        centerDetailsPanel.add(centerCardTitle);
        centerDetailsPanel.add(Box.createVerticalStrut(10));

        centerDateLabel = new JLabel("<html><font color='#FFD700'>☀</font> กรุณาเลือกงาน</html>");
        centerDateLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        centerDetailsPanel.add(centerDateLabel);
        centerDetailsPanel.add(Box.createVerticalStrut(5));

        centerEndDateLabel = new JLabel("<html><font color='#4CAF50'>🏁</font> วันสิ้นสุด: -</html>");
        centerEndDateLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        centerDetailsPanel.add(centerEndDateLabel);
        centerDetailsPanel.add(Box.createVerticalStrut(8));

        centerLocationLabel = new JLabel("<html><font color='red'>📍</font> -</html>");
        centerLocationLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        centerDetailsPanel.add(centerLocationLabel);
        centerDetailsPanel.add(Box.createVerticalStrut(8));

        centerVacanciesLabel = new JLabel("<html><font color='red'>🚨 รับ: - อัตรา</font></html>");
        centerVacanciesLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        centerDetailsPanel.add(centerVacanciesLabel);
        centerDetailsPanel.add(Box.createVerticalStrut(8));

        centerHoursLabel = new JLabel("<html>🔘 ประเภท: - (- ชม.)</html>");
        centerHoursLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        centerDetailsPanel.add(centerHoursLabel);
        centerDetailsPanel.add(Box.createVerticalStrut(15));

        centerDetailsText = new JTextArea("กรุณาเลือกงานจากรายการด้านซ้ายเพื่อดูรายละเอียด");
        centerDetailsText.setFont(new Font("Tahoma", Font.PLAIN, 14));
        centerDetailsText.setLineWrap(true);
        centerDetailsText.setWrapStyleWord(true);
        centerDetailsText.setEditable(false);
        centerDetailsText.setOpaque(false);
        centerDetailsPanel.add(centerDetailsText);

        rightPanel.add(centerDetailsPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        btnPanel.setOpaque(false);

        completeBtn = createGradientButton("Work completed", new Color(255, 160, 160), new Color(255, 200, 150));
        deleteBtn = createSolidButton("Delete Job", new Color(234, 85, 98));

        completeBtn.addActionListener(e -> {
            if (currentSelectedJob == null)
                return;
            CompletedAssignment com = new CompletedAssignment();
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "คุณแน่ใจหรือไม่: " + currentSelectedJob.title + "?",
                    "ยืนยันการทำงาน",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                com.submitComplete(Unique);
            }
        });

        deleteBtn.addActionListener(e -> {
            if (currentSelectedJob != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "คุณแน่ใจหรือไม่ว่าต้องการลบงาน: " + currentSelectedJob.title + "?",
                        "ยืนยันการลบ",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(this, "ฟีเจอร์ลบงานยังไม่เปิดใช้งาน (Demo)");
                }
            } else {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกงานก่อนลบ");
            }
        });

        btnPanel.add(completeBtn);
        btnPanel.add(deleteBtn);

        rightPanel.add(btnPanel, BorderLayout.SOUTH);
        detailCard.add(rightPanel, BorderLayout.CENTER);

        JPanel cardContainer = new JPanel(new GridBagLayout());
        cardContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        cardContainer.add(detailCard, gbc);

        panel.add(cardContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRightColumn() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("รายชื่อผู้รับงาน");
        title.setFont(new Font("SansSerif", Font.PLAIN, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel listCard = new RoundedPanel(20, Color.WHITE, Color.WHITE);
        listCard.setLayout(new BorderLayout());
        listCard.setBorder(new EmptyBorder(20, 10, 20, 10));

        workerListPanel = new JPanel();
        workerListPanel.setLayout(new BoxLayout(workerListPanel, BoxLayout.Y_AXIS));
        workerListPanel.setOpaque(false);

        JLabel noJobLabel = new JLabel("เลือกงานเพื่อดูรายชื่อผู้รับงาน");
        noJobLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noJobLabel.setForeground(Color.GRAY);
        noJobLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        workerListPanel.add(noJobLabel);

        editBtn = new JButton("Edit");
        editBtn.setBackground(Color.WHITE);
        editBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        editBtn.setPreferredSize(new Dimension(80, 30));
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        editBtn.addActionListener(e -> {
            if (currentSelectedJob != null) {
                try {
                    int jobId = Integer.parseInt(currentSelectedJob.jobId);
                    Editperson dialog = new Editperson(Workmenu.this, jobId);
                    dialog.setVisible(true);
                    // Refresh worker list after dialog closes
                    updateWorkerList(jobId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Job ID");
                }
            } else {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกงานก่อน");
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(editBtn);

        JScrollPane scrollPane = new JScrollPane(workerListPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        listCard.add(scrollPane, BorderLayout.CENTER);
        listCard.add(btnPanel, BorderLayout.SOUTH);
        panel.add(listCard, BorderLayout.CENTER);
        return panel;
    }

    private void updateWorkerList(int jobId) {
        workerListPanel.removeAll();

        ArrayList<WorkerManager> workers = WorkerManager.getJobWorkers(jobId);

        if (workers == null || workers.isEmpty()) {
            JLabel noWorkerLabel = new JLabel("ยังไม่มีผู้รับงาน");
            noWorkerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            noWorkerLabel.setForeground(Color.GRAY);
            noWorkerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            workerListPanel.add(Box.createVerticalStrut(20));
            workerListPanel.add(noWorkerLabel);
        } else {
            for (WorkerManager worker : workers) {
                workerListPanel.add(createWorkerItem(worker.name, worker.status));
                workerListPanel.add(Box.createVerticalStrut(10));
            }
        }

        workerListPanel.revalidate();
        workerListPanel.repaint();
    }

    // --- Helper Components ---

    private JPanel createJobHistoryCard(String statusText, API job, Color statusColor) {
        JPanel card = new RoundedPanel(15, Color.WHITE, Color.WHITE);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Use standard dimensions but allow flexibility
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        card.setPreferredSize(new Dimension(250, 110));

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Mouse listener logic extracted
        java.awt.event.MouseAdapter selectionListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                updateJobDetails(job);
            }
        };

        card.addMouseListener(selectionListener);

        ImageIcon icon = loadAndResizeImage(job.imagePath, 70, 90);
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setPreferredSize(new Dimension(70, 90));

        card.add(imgLabel, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.addMouseListener(selectionListener);

        JLabel title = new JLabel("<html><b>" + job.title + "</b><br></html>");
        title.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textPanel.add(title);

        JLabel status = new JLabel("Status : " + statusText);
        status.setFont(new Font("SansSerif", Font.BOLD, 12));
        status.setForeground(statusColor);
        textPanel.add(status);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private void updateJobDetails(API job) {
        this.currentSelectedJob = job;
        Unique = job.jobId;
        centerCardTitle.setText(" " + job.title);

        centerImageLabel.setIcon(loadAndResizeImage(job.imagePath, 250, 300));

        // --- Refactored Date Logic ---
        String YYYY = "YYYY", DD = "01", HH = "00", MIN = "00";
        String MM_Text = "ไม่ทราบเดือน";

        if (job.dateTime != null) {
            try {
                // Expected format handling (splitting logic)
                String[] dayD = job.dateTime.split(" ");
                if (dayD.length >= 1) {
                    String[] dateParts = dayD[0].split("-");
                    if (dateParts.length >= 3) {
                        YYYY = dateParts[0];
                        int mmIndex = Integer.parseInt(dateParts[1]);
                        DD = dateParts[2];
                        if (mmIndex >= 1 && mmIndex <= 12) {
                            MM_Text = THAI_MONTHS[mmIndex];
                        }
                    }
                }
                if (dayD.length >= 2) {
                    String[] timeParts = dayD[1].split(":");
                    if (timeParts.length >= 2) {
                        HH = timeParts[0];
                        MIN = timeParts[1];
                    }
                }
            } catch (Exception e) {
                System.err.println("Date parsing error: " + e.getMessage());
            }
        }

        centerDateLabel.setText("<html><font color='#FFD700'>☀</font> วันเริ่ม: "
                + DD + " " + MM_Text + " " + YYYY
                + "<br>&nbsp;&nbsp;&nbsp;เวลา " + HH + ":" + MIN + " น.</html>");

        String endDateDisplay = (job.endDate != null && !job.endDate.isEmpty()) ? job.endDate : "-";
        centerEndDateLabel.setText("<html><font color='#4CAF50'>🏁</font> วันสิ้นสุด: " + endDateDisplay + "</html>");
        centerLocationLabel.setText("<html><font color='red'>📍</font> " + job.location + "</html>");
        centerVacanciesLabel.setText("<html><font color='red'>🚨 รับ: " + job.vacancies + " อัตรา</font></html>");

        String typeDisplay = (job.jobType == null) ? "-" : job.jobType;
        if (typeDisplay.equalsIgnoreCase("paid"))
            typeDisplay = "งานมีค่าตอบแทน";
        else if (typeDisplay.equalsIgnoreCase("volunteer"))
            typeDisplay = "งานจิตอาสา";

        centerHoursLabel.setText("<html>🔘 ประเภท: " + typeDisplay + " (" + job.workingHours + " ชม.)</html>");
        centerDetailsText.setText(job.details != null ? job.details : "");

        // Hide buttons when status is complete or done
        boolean isComplete = job.status != null &&
                (job.status.equalsIgnoreCase("complete") || job.status.equalsIgnoreCase("done"));
        completeBtn.setVisible(!isComplete);
        deleteBtn.setVisible(!isComplete);
        editBtn.setVisible(!isComplete);

        try {
            int jobIdInt = Integer.parseInt(job.jobId);
            updateWorkerList(jobIdInt);
        } catch (NumberFormatException e) {
            System.err.println("Invalid job ID for worker list: " + job.jobId);
        }

        revalidate();
        repaint();
    }

    private ImageIcon loadAndResizeImage(String imagePath, int width, int height) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return new ImageIcon(createPlaceholderImage(width, height, new Color(139, 69, 19)));
        }
        try {
            java.awt.image.BufferedImage originalImage = null;
            if (imagePath.startsWith("http")) {
                originalImage = javax.imageio.ImageIO.read(new java.net.URL(imagePath));
            } else {
                java.io.File f = new java.io.File(imagePath);
                if (f.exists()) {
                    originalImage = javax.imageio.ImageIO.read(f);
                } else {
                    java.io.File retry = new java.io.File("user_images/" + imagePath);
                    if (retry.exists())
                        originalImage = javax.imageio.ImageIO.read(retry);
                }
            }
            if (originalImage != null) {
                Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            // Quiet fail
        }
        return new ImageIcon(createPlaceholderImage(width, height, new Color(139, 69, 19)));
    }

    private Image createPlaceholderImage(int width, int height, Color color) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(width, height,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(color);
        g2.fillRect(0, 0, width, height);
        g2.dispose();
        return img;
    }

    private JPanel createWorkerItem(String workerName, String status) {
        JPanel item = new RoundedPanel(20, Color.WHITE, new Color(220, 220, 220));
        item.setLayout(new BorderLayout(10, 0));

        // [FIX] Changed Width Max to Integer.MAX_VALUE to stretch fully in BoxLayout
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        item.setPreferredSize(new Dimension(220, 40));
        item.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 182, 193));
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        avatar.setPreferredSize(new Dimension(24, 24));
        avatar.setOpaque(false);

        JLabel nameLabel = new JLabel(workerName);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        leftPanel.add(avatar);
        leftPanel.add(nameLabel);

        if (status != null) {
            switch (status.toLowerCase()) {
                case "pending":
                    statusText = "รอดำเนินการ";
                    statusColor = new Color(255, 193, 7);
                    break;
                case "done":
                case "complete":
                    statusText = "เสร็จสิ้น";
                    statusColor = new Color(40, 167, 69);
                    break;
                case "cancelled":
                    statusText = "ยกเลิก";
                    statusColor = new Color(220, 53, 69);
                    break;
                default:
                    statusText = status;
                    statusColor = Color.GRAY;
            }
        } else {
            statusText = "รอดำเนินการ";
            statusColor = new Color(255, 193, 7);
        }

        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 9));
        statusLabel.setForeground(statusColor);

        item.add(leftPanel, BorderLayout.CENTER);
        item.add(statusLabel, BorderLayout.EAST);

        return item;
    }

    // Helper to create buttons cleaner
    private JButton createGradientButton(String text, Color start, Color end) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, start, getWidth(), 0, end);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        styleButton(btn);
        return btn;
    }

    private JButton createSolidButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        styleButton(btn);
        return btn;
    }

    private void styleButton(JButton btn) {
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}