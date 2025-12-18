package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D; // [เพิ่ม] สำหรับตัดรูปวงกลม
import java.awt.image.BufferedImage;
import java.io.File; // [เพิ่ม] สำหรับอ่านไฟล์
import java.io.IOException; // [เพิ่ม]
import java.util.ArrayList;
import javax.imageio.ImageIO; // [เพิ่ม]

import model.JobAssignment;
import model.Profiles;
import service.API;
import service.Auth;
import service.Student;
import ui.component.Navbar;

public class Profile extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240); // Light gray background

    public String nameUser = Auth.getAuthUser().getName();
    public String statusUser = Auth.getAuthUser().getStatus();
    public String txtStatusUser;
    public String txtIdUser;
    public String txtEmail = Auth.getAuthUser().getEmail();

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
        setSize(1200, 800);
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

        // [แก้ไข] ส่วนการดึงรูปภาพ Avatar
        ArrayList<Profiles> profiles = API.getProfile(Auth.getAuthUser().getId());
        String imagePath = null;
        if (profiles != null && !profiles.isEmpty()) {
            // สมมติว่า getImagePath() คืนค่า path ที่เซฟไว้ (เช่น "user_images/profile_101.png")
            imagePath = profiles.get(0).getImagePath();
        }

        // เรียกเมธอด getAvatarImage เพื่อโหลดรูปและตัดเป็นวงกลม (ถ้าไม่มีจะใช้ Placeholder)
        ImageIcon avatarIcon = getAvatarImage(imagePath, 180);
        JLabel avatarLarge = new JLabel(avatarIcon);

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

        if (statusUser.equals("admin")) {
            txtStatusUser = "ผู้ดูแลระบบ";
            txtIdUser = String.valueOf(Auth.getAuthUser().getId());
        } else {
            txtStatusUser = "นักศึกษา";
            Student s = (Student) Auth.getAuthUser();
            txtIdUser = s.getStdId();
        }

        // Status
        JLabel statusLbl = new JLabel("สถานะ : " + txtStatusUser);
        statusLbl.setFont(FONT_THAI);
        nameRow.add(statusLbl);

        infoPanel.add(nameRow);
        infoPanel.add(Box.createVerticalStrut(10));

        // ID
        JLabel idLbl = new JLabel("ID : " + txtIdUser);
        idLbl.setFont(FONT_ID);
        infoPanel.add(idLbl);

        // Count Jobs
        ArrayList<JobAssignment> jobAss = API.getUserAssign(txtIdUser);
        int countComplete = 0;
        for (JobAssignment i : jobAss) {
            if ("complete".equals(i.getStatus())) {
                countComplete++;
            }
        }

        // Email
        JLabel emailLbl = new JLabel("Email : " + txtEmail);
        emailLbl.setFont(FONT_ID);
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
        bioTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        JLabel jobCount = new JLabel("งานที่ทำสำเร็จ : " + countComplete + " งาน");
        jobCount.setFont(FONT_THAI);

        JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleWrapper.setOpaque(false);
        titleWrapper.add(bioTitle);

        bioHeader.add(titleWrapper, BorderLayout.NORTH);
        bioHeader.add(jobCount, BorderLayout.SOUTH);

        bioPanel.add(bioHeader, BorderLayout.NORTH);

        // ==========================================
        // ส่วนจัดการ BIO (ใช้ข้อมูลที่ดึงมาก่อนหน้านี้)
        // ==========================================
        String BIO = "No comment 101"; // ค่า Default
        if (profiles != null && !profiles.isEmpty()) {
            String tempBio = profiles.get(0).getBio();
            if (tempBio != null && !tempBio.equals("null") && !tempBio.trim().isEmpty()) {
                BIO = tempBio;
            }
        }

        // 2. ตัดคำทุก 100 ตัวอักษร (ตามโค้ดเดิม)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < BIO.length(); i++) {
            sb.append(BIO.charAt(i));
            if ((i + 1) % 100 == 0 && (i + 1) < BIO.length()) {
                sb.append("\n");
            }
        }
        String txtBIO = sb.toString();

        // 3. แสดงผลใน JTextArea
        JTextArea bioText = new JTextArea(txtBIO);
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
        editBtn.addActionListener(e -> {
            new ProfileEditor().setVisible(true);
        });
        card.add(editBtn, gbc);

        return card;
    }

    // ========= COMPONENTS & HELPER METHODS ==========

    // [เพิ่ม] เมธอดโหลดรูปภาพและตัดเป็นวงกลม
    private ImageIcon getAvatarImage(String path, int size) {
        if (path != null && !path.trim().isEmpty() && !path.equals("null")) {
            try {
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    BufferedImage originalImage = ImageIO.read(imgFile);
                    if (originalImage != null) {
                        BufferedImage circledImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = circledImage.createGraphics();

                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setClip(new Ellipse2D.Float(0, 0, size, size));
                        g2.drawImage(originalImage, 0, 0, size, size, null);

                        g2.dispose();
                        return new ImageIcon(circledImage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // ถ้าโหลดไม่ได้ให้ใช้ Placeholder สีชมพูเหมือนเดิม
        return new ImageIcon(createPlaceholderImage(size, Color.PINK));
    }

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