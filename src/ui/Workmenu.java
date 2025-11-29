package ui;

import util.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Workmenu extends JFrame {

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
        setSize(1200, 800);
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
        JPanel headerPanel = createHeader();
        topSection.add(headerPanel, BorderLayout.CENTER);

        mainContainer.add(topSection, BorderLayout.NORTH);

        // ===== CONTENT BODY =====
        JPanel contentBody = createContentBody();
        mainContainer.add(contentBody, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 120, 120), getWidth(), 0,
                        new Color(255, 200, 150));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(1200, 80));
        header.setBorder(new EmptyBorder(10, 30, 10, 30));

        // Profile Section (Left)
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        profilePanel.setOpaque(false);

        // Avatar (Round)
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                // Draw placeholder icon
                g2.setColor(Color.PINK);
                g2.fillOval(5, 5, getWidth() - 10, getHeight() - 10);
            }
        };
        avatar.setPreferredSize(new Dimension(50, 50));
        avatar.setOpaque(false);

        JLabel nameLabel = new JLabel("Elysia Athome");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);

        profilePanel.add(avatar);
        profilePanel.add(nameLabel);

        header.add(profilePanel, BorderLayout.WEST);

        return header;
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

        // Add multiple job history items to demonstrate scrolling
        listPanel.add(createJobHistoryCard("กำลังทำ", new Color(255, 160, 122)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createJobHistoryCard("สำเร็จ", new Color(50, 205, 50)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createJobHistoryCard("สำเร็จ", new Color(50, 205, 50)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createJobHistoryCard("กำลังทำ", new Color(255, 160, 122)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createJobHistoryCard("สำเร็จ", new Color(50, 205, 50)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createJobHistoryCard("ยกเลิก", new Color(220, 20, 60)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createJobHistoryCard("สำเร็จ", new Color(50, 205, 50)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createJobHistoryCard("กำลังทำ", new Color(255, 160, 122)));

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

        // Main Card
        JPanel detailCard = new RoundedPanel(20, Color.WHITE, Color.WHITE);
        detailCard.setLayout(new BorderLayout());
        detailCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title inside card
        JLabel cardTitle = new JLabel("กิจกรรมอบรม ในโครงการ KNOCK KNOCK");
        cardTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        cardTitle.setHorizontalAlignment(SwingConstants.CENTER);
        detailCard.add(cardTitle, BorderLayout.NORTH);

        // Content (Image + Text)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Image Placeholder
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(139, 69, 19)); // Brownish placeholder
        contentPanel.add(imagePanel);

        // Text Details
        JTextArea detailsText = new JTextArea(
                "ขอเชิญชวน เพื่อนๆ พี่ๆ น้องๆ ชาว มจธ.\n" +
                        "เข้าร่วมกิจกรรมอบรม ในโครงการ KNOCK KNOCK\n\n" +
                        "วันอังคารที่ 20 พฤษภาคม 2568\n" +
                        "เวลา 16:00-20:00 น.\n\n" +
                        "ณ อาคารเรียนรวม 1 ห้อง B3102\n\n" +
                        "รับสมัครจำนวนจำกัด 20 คน!!\n" +
                        "จิตอาสา 5 ชั่วโมง");
        detailsText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detailsText.setLineWrap(true);
        detailsText.setWrapStyleWord(true);
        detailsText.setEditable(false);
        detailsText.setOpaque(false);
        contentPanel.add(detailsText);

        detailCard.add(contentPanel, BorderLayout.CENTER);

        // Button
        JButton completeBtn = new JButton("Work completed") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        completeBtn.setBackground(new Color(255, 160, 122));
        completeBtn.setForeground(Color.WHITE);
        completeBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        completeBtn.setBorderPainted(false);
        completeBtn.setContentAreaFilled(false);
        completeBtn.setFocusPainted(false);
        completeBtn.setPreferredSize(new Dimension(200, 40));

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(completeBtn);

        detailCard.add(btnPanel, BorderLayout.SOUTH);

        panel.add(detailCard, BorderLayout.CENTER);
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

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (int i = 0; i < 6; i++) {
            listPanel.add(createWorkerItem());
            listPanel.add(Box.createVerticalStrut(10));
        }

        // Add Edit button at bottom
        JButton editBtn = new JButton("Edit");
        editBtn.setBackground(Color.WHITE);
        editBtn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        editBtn.setPreferredSize(new Dimension(80, 30));
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(editBtn);
        listPanel.add(btnPanel);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        listCard.add(scrollPane, BorderLayout.CENTER);
        panel.add(listCard, BorderLayout.CENTER);
        return panel;
    }

    // --- Helper Components ---

    private JPanel createJobHistoryCard(String statusText, Color statusColor) {
        JPanel card = new RoundedPanel(15, Color.WHITE, Color.WHITE);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setMaximumSize(new Dimension(300, 110));
        card.setPreferredSize(new Dimension(250, 110));

        // Image placeholder
        JPanel img = new JPanel();
        img.setBackground(new Color(139, 69, 19));
        img.setPreferredSize(new Dimension(70, 90));
        card.add(img, BorderLayout.WEST);

        // Text
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel title = new JLabel("<html><b>กิจกรรมอบรม</b><br>ในโครงการ KNOCK KNOCK</html>");
        title.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textPanel.add(title);

        JLabel status = new JLabel("Status : " + statusText);
        status.setFont(new Font("SansSerif", Font.BOLD, 12));
        status.setForeground(statusColor);
        textPanel.add(status);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createWorkerItem() {
        JPanel item = new RoundedPanel(20, Color.WHITE, new Color(220, 220, 220));
        item.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        item.setMaximumSize(new Dimension(250, 40));
        item.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        // // Handled by RoundedPanel

        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.PINK);
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        avatar.setPreferredSize(new Dimension(24, 24));
        avatar.setOpaque(false);

        JLabel name = new JLabel("Elysia Athome");
        name.setFont(new Font("SansSerif", Font.PLAIN, 12));

        item.add(avatar);
        item.add(name);

        return item;
    }

}
