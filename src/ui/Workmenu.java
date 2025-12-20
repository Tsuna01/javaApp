package ui;

import service.API;
import service.Auth;
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
    private JLabel centerLocationLabel;
    private JLabel centerVacanciesLabel;
    private JLabel centerHoursLabel;

    // Store current job for delete action
    private API currentSelectedJob;

    // Store job history list
    private ArrayList<API> jobHistory;

    // Right column worker list panel
    private JPanel workerListPanel;

    public Workmenu() {
        initialize();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
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

        // Initialize default selection
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
        gbc.weightx = 0.25;
        body.add(createLeftColumn(), gbc);

        // Center Column: Job Details
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        body.add(createCenterColumn(), gbc);

        // Right Column: Worker List
        gbc.gridx = 2;
        gbc.weightx = 0.25;
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

        int userId = Auth.getAuthUser().getId();
        // ตรวจสอบว่ามี method getHistoryJobAdmin ใน API.java หรือไม่
        // ถ้าไม่มีอาจต้องเช็คชื่อ method
        jobHistory = API.getHistoryJobAdmin(userId);

        if (jobHistory.isEmpty()) {
            // Show message when no jobs found
            JLabel noJobsLabel = new JLabel("ไม่มีประวัติการจ้างงาน");
            noJobsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            noJobsLabel.setForeground(Color.GRAY);
            noJobsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            listPanel.add(noJobsLabel);
        } else {

            for (int i = 0; i < jobHistory.size(); i++) {
                API job = jobHistory.get(i);
                // Default status for now
                String status = "กำลังทำ";
                Color statusColor = new Color(255, 160, 122);

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
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

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

        // Main Card with shadow (like DetailJob.java)
        JPanel detailCard = new JPanel(new BorderLayout(30, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);

                // Background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
            }
        };
        detailCard.setOpaque(false);
        detailCard.setBorder(new EmptyBorder(30, 10, 30, 10));

        // Image Label (Left side)
        centerImageLabel = new JLabel();
        centerImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerImageLabel.setVerticalAlignment(SwingConstants.TOP);
        centerImageLabel.setPreferredSize(new Dimension(250, 300));
        centerImageLabel.setIcon(loadAndResizeImage(null, 250, 300));
        detailCard.add(centerImageLabel, BorderLayout.WEST);

        // Right Panel (Details + Buttons)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        // Details Panel
        centerDetailsPanel = new JPanel();
        centerDetailsPanel.setLayout(new BoxLayout(centerDetailsPanel, BoxLayout.Y_AXIS));
        centerDetailsPanel.setOpaque(false);

        // Title inside details
        centerCardTitle = new JLabel("เลือกงานเพื่อดูรายละเอียด");
        centerCardTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        centerDetailsPanel.add(centerCardTitle);
        centerDetailsPanel.add(Box.createVerticalStrut(10));

        // Date Label
        centerDateLabel = new JLabel("<html><font color='#FFD700'>☀</font> กรุณาเลือกงาน</html>");
        centerDateLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        centerDetailsPanel.add(centerDateLabel);
        centerDetailsPanel.add(Box.createVerticalStrut(8));

        // Location Label
        centerLocationLabel = new JLabel("<html><font color='red'>📍</font> -</html>");
        centerLocationLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        centerDetailsPanel.add(centerLocationLabel);
        centerDetailsPanel.add(Box.createVerticalStrut(8));

        // Vacancies Label
        centerVacanciesLabel = new JLabel("<html><font color='red'>🚨 รับ: - อัตรา</font></html>");
        centerVacanciesLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        centerDetailsPanel.add(centerVacanciesLabel);
        centerDetailsPanel.add(Box.createVerticalStrut(8));

        // Hours/Type Label
        centerHoursLabel = new JLabel("<html>🔘 ประเภท: - (- ชม.)</html>");
        centerHoursLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        centerDetailsPanel.add(centerHoursLabel);
        centerDetailsPanel.add(Box.createVerticalStrut(15));

        // Description Text
        centerDetailsText = new JTextArea("กรุณาเลือกงานจากรายการด้านซ้ายเพื่อดูรายละเอียด");
        centerDetailsText.setFont(new Font("Tahoma", Font.PLAIN, 14));
        centerDetailsText.setLineWrap(true);
        centerDetailsText.setWrapStyleWord(true);
        centerDetailsText.setEditable(false);
        centerDetailsText.setOpaque(false);
        centerDetailsPanel.add(centerDetailsText);

        rightPanel.add(centerDetailsPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        btnPanel.setOpaque(false);

        // Work Completed Button
        JButton completeBtn = new JButton("Work completed") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 160, 160),
                        getWidth(), 0, new Color(255, 200, 150));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        completeBtn.setForeground(Color.WHITE);
        completeBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        completeBtn.setBorderPainted(false);
        completeBtn.setContentAreaFilled(false);
        completeBtn.setFocusPainted(false);
        completeBtn.setPreferredSize(new Dimension(200, 45));
        completeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Delete Button
        JButton deleteBtn = new JButton("Delete Job") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(234, 85, 98));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        deleteBtn.setBorderPainted(false);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(200, 45));
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add action listener for delete button
        deleteBtn.addActionListener(e -> {
            if (currentSelectedJob != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "คุณแน่ใจหรือไม่ว่าต้องการลบงาน: " + currentSelectedJob.title + "?",
                        "ยืนยันการลบ",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    // TODO: Call API to delete job
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

        // Center the card in the panel
        JPanel cardContainer = new JPanel(new GridBagLayout());
        cardContainer.setOpaque(false);
        cardContainer.add(detailCard);

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

        // Background card for the list
        JPanel listCard = new RoundedPanel(20, Color.WHITE, Color.WHITE);
        listCard.setLayout(new BorderLayout());
        listCard.setBorder(new EmptyBorder(20, 10, 20, 10));

        workerListPanel = new JPanel();
        workerListPanel.setLayout(new BoxLayout(workerListPanel, BoxLayout.Y_AXIS));
        workerListPanel.setOpaque(false);

        // Default message when no job selected
        JLabel noJobLabel = new JLabel("เลือกงานเพื่อดูรายชื่อผู้รับงาน");
        noJobLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noJobLabel.setForeground(Color.GRAY);
        noJobLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        workerListPanel.add(noJobLabel);

        // Add Edit button at bottom
        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(Color.WHITE);
        editBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        editBtn.setPreferredSize(new Dimension(80, 30));

        editBtn.addActionListener(e -> {
            Editperson dialog = new Editperson(Workmenu.this);
            dialog.setVisible(true);
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

        ArrayList<API.WorkerInfo> workers = API.getJobWorkers(jobId);

        if (workers.isEmpty()) {
            JLabel noWorkerLabel = new JLabel("ยังไม่มีผู้รับงาน");
            noWorkerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            noWorkerLabel.setForeground(Color.GRAY);
            noWorkerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            workerListPanel.add(Box.createVerticalStrut(20));
            workerListPanel.add(noWorkerLabel);
        } else {
            for (API.WorkerInfo worker : workers) {
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
        card.setMaximumSize(new Dimension(300, 110));
        card.setPreferredSize(new Dimension(250, 110));

        // Add mouse listener for selection
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                updateJobDetails(job);
            }
        });

        // [Updated] Load image using helper method
        ImageIcon icon = loadAndResizeImage(job.imagePath, 70, 90);
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setPreferredSize(new Dimension(70, 90));

        // Add image to the card
        card.add(imgLabel, BorderLayout.WEST);

        // Text
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        // Pass clicks from children to parent card
        textPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                updateJobDetails(job);
            }
        });

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
        centerCardTitle.setText(" " + job.title);

        // Update Image (larger size for center panel)
        centerImageLabel.setIcon(loadAndResizeImage(job.imagePath, 250, 300));

        // --- Date Formatting (like DetailJob.java) ---
        String dateD = (job.dateTime != null) ? job.dateTime : "2025-01-01 00:00:00";
        String YYYY = "YYYY", MM_Num = "01", DD = "01", HH = "00", MIN = "00";

        try {
            String[] dayD = dateD.split(" ");
            if (dayD.length >= 1) {
                String[] dateParts = dayD[0].split("-");
                if (dateParts.length >= 3) {
                    YYYY = dateParts[0];
                    MM_Num = dateParts[1];
                    DD = dateParts[2];
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
        }

        String MM_Text = "";
        switch (MM_Num) {
            case "1":
            case "01":
                MM_Text = "มกราคม";
                break;
            case "2":
            case "02":
                MM_Text = "กุมภาพันธ์";
                break;
            case "3":
            case "03":
                MM_Text = "มีนาคม";
                break;
            case "4":
            case "04":
                MM_Text = "เมษายน";
                break;
            case "5":
            case "05":
                MM_Text = "พฤษภาคม";
                break;
            case "6":
            case "06":
                MM_Text = "มิถุนายน";
                break;
            case "7":
            case "07":
                MM_Text = "กรกฎาคม";
                break;
            case "8":
            case "08":
                MM_Text = "สิงหาคม";
                break;
            case "9":
            case "09":
                MM_Text = "กันยายน";
                break;
            case "10":
                MM_Text = "ตุลาคม";
                break;
            case "11":
                MM_Text = "พฤศจิกายน";
                break;
            case "12":
                MM_Text = "ธันวาคม";
                break;
            default:
                MM_Text = "ไม่ทราบเดือน";
        }

        // Update Date Label
        centerDateLabel.setText("<html><font color='#FFD700'>☀</font> วันที่: "
                + DD + " " + MM_Text + " " + YYYY
                + "<br>&nbsp;&nbsp;&nbsp;เวลา " + HH + ":" + MIN + " น.</html>");

        // Update Location Label
        centerLocationLabel.setText("<html><font color='red'>📍</font> " + job.location + "</html>");

        // Update Vacancies Label
        centerVacanciesLabel.setText("<html><font color='red'>🚨 รับ: " + job.vacancies + " อัตรา</font></html>");

        // Update Hours/Type Label
        String typeDisplay;
        if (job.jobType != null && job.jobType.equalsIgnoreCase("paid")) {
            typeDisplay = "งานมีค่าตอบแทน";
        } else if (job.jobType != null && job.jobType.equalsIgnoreCase("volunteer")) {
            typeDisplay = "งานจิตอาสา";
        } else {
            typeDisplay = job.jobType;
        }
        centerHoursLabel.setText("<html>🔘 ประเภท: " + typeDisplay + " (" + job.workingHours + " ชม.)</html>");

        // Update Details Text
        centerDetailsText.setText(job.details != null ? job.details : "");

        // Update Worker List for this job
        try {
            int jobIdInt = Integer.parseInt(job.jobId);
            updateWorkerList(jobIdInt);
        } catch (NumberFormatException e) {
            System.err.println("Invalid job ID: " + job.jobId);
        }

        // Refresh UI
        revalidate();
        repaint();
    }

    private ImageIcon loadAndResizeImage(String imagePath, int width, int height) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return new ImageIcon(createPlaceholderImage(width, new Color(139, 69, 19)));
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
            System.err.println("Load image error: " + imagePath);
        }
        return new ImageIcon(createPlaceholderImage(width, new Color(139, 69, 19)));
    }

    private Image createPlaceholderImage(int sizeX, Color color) {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(sizeX, 90,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(color);
        g2.fillRect(0, 0, sizeX, 90);
        g2.dispose();
        return img;
    }

    private JPanel createWorkerItem(String workerName, String status) {
        JPanel item = new RoundedPanel(20, Color.WHITE, new Color(220, 220, 220));
        item.setLayout(new BorderLayout(10, 0));
        item.setMaximumSize(new Dimension(250, 45));
        item.setPreferredSize(new Dimension(220, 40));
        item.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Left side: Avatar + Name
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);

        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 182, 193)); // Pink
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        avatar.setPreferredSize(new Dimension(24, 24));
        avatar.setOpaque(false);

        JLabel nameLabel = new JLabel(workerName);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        leftPanel.add(avatar);
        leftPanel.add(nameLabel);

        // Right side: Status badge
        String statusText;
        Color statusColor;
        if (status != null) {
            switch (status.toLowerCase()) {
                case "pending":
                    statusText = "รอดำเนินการ";
                    statusColor = new Color(255, 193, 7); // Yellow
                    break;
                case "done":
                case "completed":
                    statusText = "เสร็จสิ้น";
                    statusColor = new Color(40, 167, 69); // Green
                    break;
                case "cancelled":
                    statusText = "ยกเลิก";
                    statusColor = new Color(220, 53, 69); // Red
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
}