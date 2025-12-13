package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

import service.Auth;
import service.User;
import ui.component.Navbar;

public class Profile extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240); // Light gray background

    public String nameUser = Auth.getAuthUser().getName();
    public String statusUser = Auth.getAuthUser().getStatus();
    public String txtStatusUser;

    // Fonts
    private static final Font FONT_NAME = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_ID = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_THAI = new Font("Tahoma", Font.PLAIN, 14);
    private static final Font FONT_THAI_BOLD = new Font("Tahoma", Font.BOLD, 16);
    private static final Font FONT_BIO = new Font("Tahoma", Font.PLAIN, 16);

    public Profile() {
        initialize();
    }

    private void initialize() {
        setTitle("Profile");
        setSize(1200, 800); // Adjusted size to match wide design
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());


        // Main Container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        add(mainPanel, BorderLayout.CENTER);

        // ========= HEADER ==========
        mainPanel.add(new Navbar().build(), BorderLayout.NORTH);

        // ========= BODY ==========
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(30, 100, 50, 100)); // Margins
        mainPanel.add(body, BorderLayout.CENTER);

        // Back Button Area
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton backBtn = createBackButton();
        backBtn.addActionListener(e -> {
            dispose();

        });
        topBar.add(backBtn);

        body.add(topBar, BorderLayout.NORTH);

        // Profile Card
        body.add(createProfileCard(), BorderLayout.CENTER);
    }

    // ========= PROFILE CARD ==========
    private JPanel createProfileCard() {

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 40, 40);

                // BG
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 40, 40);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);

        // --- Left: Avatar ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel avatarLarge = new JLabel(new ImageIcon(createPlaceholderImage(180, Color.PINK)));
        card.add(avatarLarge, gbc);

        // --- Right Top: Info ---
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Name Row
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nameRow.setOpaque(false);
        JLabel nameLbl = new JLabel(nameUser);
        nameLbl.setFont(FONT_NAME);
        nameRow.add(nameLbl);

        // Spacer
        nameRow.add(Box.createHorizontalStrut(50));

        if(statusUser.equals("admin")){
            txtStatusUser = "ผู้ดูแลระบบ";
        }else {
            txtStatusUser = "นักศึกษา";
        }

        // Status
        JLabel statusLbl = new JLabel("สถานะ : "+txtStatusUser);
        statusLbl.setFont(FONT_THAI);
        nameRow.add(statusLbl);

        infoPanel.add(nameRow);
        infoPanel.add(Box.createVerticalStrut(10));

        // ID
        JLabel idLbl = new JLabel("ID : S33550336");
        idLbl.setFont(FONT_ID);
        // Underline effect can be done with HTML or custom painting, using HTML for
        // simplicity
        idLbl.setText("<html><u>ID : S33550336</u></html>");
        infoPanel.add(idLbl);

        // Email
        JLabel emailLbl = new JLabel("Email : Cyrene.33550336@gmail.com");
        emailLbl.setFont(FONT_ID);
        emailLbl.setText("<html><u>Email : Cyrene.33550336@gmail.com</u></html>");
        infoPanel.add(emailLbl);

        card.add(infoPanel, gbc);

        // --- Right Bottom: Bio ---
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;

        JPanel bioPanel = new JPanel(new BorderLayout(0, 10));
        bioPanel.setOpaque(false);
        bioPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Bio Header
        JPanel bioHeader = new JPanel(new BorderLayout());
        bioHeader.setOpaque(false);

        JLabel bioTitle = new JLabel("ประวัติกิจกรรม");
        bioTitle.setFont(FONT_THAI_BOLD);
        // Add underline to title
        bioTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        JLabel jobCount = new JLabel("งานที่ทำสำเร็จ : 10 งาน");
        jobCount.setFont(FONT_THAI);

        JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleWrapper.setOpaque(false);
        titleWrapper.add(bioTitle);

        bioHeader.add(titleWrapper, BorderLayout.NORTH);
        bioHeader.add(jobCount, BorderLayout.SOUTH);

        bioPanel.add(bioHeader, BorderLayout.NORTH);

        // Bio Text
        JTextArea bioText = new JTextArea("“ผม/ดิฉันเป็นนักศึกษามหาวิทยาลัย [ชื่อมหาวิทยาลัย] สาขา [ชื่อสาขา]\n" +
                "ที่มีความสนใจในด้าน [ระบุความสนใจ เช่น เทคโนโลยี นวัตกรรม\n" +
                "การออกแบบ หรือการตลาด] ชอบการเรียนรู้สิ่งใหม่ ๆ\n" +
                "และพัฒนาทักษะอย่างต่อเนื่อง\n" +
                "พร้อมเปิดรับโอกาสในการฝึกงานและประสบการณ์ที่ช่วยต่อยอดสู่การท\n" +
                "ํางานในอนาคต”");
        bioText.setFont(FONT_BIO);
        bioText.setLineWrap(true);
        bioText.setWrapStyleWord(true);
        bioText.setEditable(false);
        bioText.setOpaque(false);
        bioText.setBorder(new EmptyBorder(10, 0, 0, 0));

        bioPanel.add(bioText, BorderLayout.CENTER);

        card.add(bioPanel, gbc);

        // --- Edit Button (Bottom Right) ---
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;

        JButton editBtn = createPillButton("Edit");
        card.add(editBtn, gbc);

        return card;
    }

    // ========= COMPONENTS ==========

    private JButton createBackButton() {
        JButton btn = new JButton("← Back") {
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
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.BLACK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 40));
        return btn;
    }

    private JButton createPillButton(String text) {
        JButton btn = new JButton(text) {
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
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.BLACK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 40));
        return btn;
    }

    public Image createPlaceholderImage(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return img;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Profile().setVisible(true));
    }


}
