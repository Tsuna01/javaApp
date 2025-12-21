package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import model.JobAssignment;
import model.Profiles;
import service.API;
import service.Auth;
import service.Student;
import service.WorkerManager;
import ui.component.Navbar;

public class Profile extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);

    // ดึงข้อมูล User (ควรเช็ค null ใน Auth ก่อนใช้จริง)
    public String nameUser = (Auth.getAuthUser() != null) ? Auth.getAuthUser().getName() : "Guest";
    public String statusUser = (Auth.getAuthUser() != null) ? Auth.getAuthUser().getStatus() : "guest";
    public String txtStatusUser;
    public String txtIdUser;
    public String txtEmail = (Auth.getAuthUser() != null) ? Auth.getAuthUser().getEmail() : "";

    // For viewing another user's profile
    private int targetUserId = -1;
    private String targetStdId = null;
    private boolean isViewingOther = false;
    private boolean isMinimalMode = false; // Hide navbar and buttons when true

    // Fonts
    private static final Font FONT_NAME = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_ID = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_THAI = new Font("Tahoma", Font.PLAIN, 14);
    private static final Font FONT_THAI_BOLD = new Font("Tahoma", Font.BOLD, 16);
    private static final Font FONT_BIO = new Font("Tahoma", Font.PLAIN, 14); // ปรับลดขนาดลงเล็กน้อยให้อ่านง่าย

    public Profile() {
        initialize();
    }

    // Constructor for viewing another user's profile by stdId
    public Profile(String stdId) {
        this(stdId, false);
    }

    // Constructor with minimal mode option (hides navbar and buttons)
    public Profile(String stdId, boolean minimalMode) {
        this.targetStdId = stdId;
        this.targetUserId = WorkerManager.getUserIdByStdId(stdId);
        this.isViewingOther = true;
        this.isMinimalMode = minimalMode;

        // Get the target user's info
        String targetName = WorkerManager.getStudentName(stdId);
        this.nameUser = (targetName != null) ? targetName : "Unknown User";
        this.statusUser = "student";
        this.txtIdUser = stdId;
        this.txtEmail = ""; // Email not publicly shown for other users

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
        // Only show navbar if not in minimal mode
        if (!isMinimalMode) {
            mainPanel.add(new Navbar().build(), BorderLayout.NORTH);
        }

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
        backBtn.addActionListener(e -> dispose());
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

        // 1. Prepare Data - use targetUserId if viewing other's profile
        int userIdForQuery = isViewingOther ? targetUserId
                : (Auth.getAuthUser() != null ? Auth.getAuthUser().getId() : -1);
        ArrayList<Profiles> profiles = API.getProfile(userIdForQuery);
        String imagePath = null;
        String bioTextContent = "No comment 101"; // Default Bio

        if (profiles != null && !profiles.isEmpty()) {
            imagePath = profiles.get(0).getImagePath();
            String tempBio = profiles.get(0).getBio();
            if (tempBio != null && !tempBio.equals("null") && !tempBio.trim().isEmpty()) {
                bioTextContent = tempBio;
            }
        }

        if (isViewingOther) {
            txtStatusUser = "นักศึกษา";
            // txtIdUser already set in constructor
        } else if (statusUser.equals("admin")) {
            txtStatusUser = "ผู้ดูแลระบบ";
            txtIdUser = String.valueOf(userIdForQuery);
        } else {
            txtStatusUser = "นักศึกษา";
            if (Auth.getAuthUser() instanceof Student) {
                Student s = (Student) Auth.getAuthUser();
                txtIdUser = s.getStdId();
            } else {
                txtIdUser = String.valueOf(userIdForQuery);
            }
        }

        // --- Left: Avatar ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;

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
        nameRow.add(Box.createHorizontalStrut(20));

        JLabel statusLbl = new JLabel("สถานะ : " + txtStatusUser);
        statusLbl.setFont(FONT_THAI);
        nameRow.add(statusLbl);

        infoPanel.add(nameRow);
        infoPanel.add(Box.createVerticalStrut(10));

        // ID & Email
        JLabel idLbl = new JLabel("ID : " + txtIdUser);
        idLbl.setFont(FONT_ID);
        infoPanel.add(idLbl);

        JLabel emailLbl = new JLabel("Email : " + txtEmail);
        emailLbl.setFont(FONT_ID);
        infoPanel.add(emailLbl);

        card.add(infoPanel, gbc);

        // --- Right Bottom: Bio & Stats ---
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 1.0;

        // Count Jobs Logic
        ArrayList<JobAssignment> jobAss = API.getUserAssign(txtIdUser);
        int countComplete = 0;
        if (jobAss != null) {
            for (JobAssignment i : jobAss) {
                if ("complete".equalsIgnoreCase(i.getStatus())) {
                    countComplete++;
                }
            }
        }

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

        // Bio Content (ใช้ JTextArea จัดการ Wrap text เอง)
        JTextArea bioText = new JTextArea(bioTextContent);
        bioText.setFont(FONT_BIO);
        bioText.setLineWrap(true); // ตัดบรรทัดอัตโนมัติ
        bioText.setWrapStyleWord(true); // ตัดที่ช่องว่างคำ
        bioText.setEditable(false);
        bioText.setOpaque(false);
        bioText.setBorder(new EmptyBorder(10, 0, 0, 0));

        // ใส่ JScrollPane เผื่อ Bio ยาวเกินพื้นที่
        JScrollPane bioScroll = new JScrollPane(bioText);
        bioScroll.setBorder(null);
        bioScroll.setOpaque(false);
        bioScroll.getViewport().setOpaque(false);

        bioPanel.add(bioScroll, BorderLayout.CENTER);
        card.add(bioPanel, gbc);

        // --- Buttons (Bottom) ---
        // Only show buttons if not in minimal mode
        // [Fixed] ย้ายมาเช็คตรงนี้ เพื่อให้ return card เสมอไม่ว่าจะ user ประเภทไหน
        if (!isMinimalMode && "student".equalsIgnoreCase(statusUser)) {
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            btnPanel.setOpaque(false);
            btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            JButton exportBtn = createPillButton("Export");
            exportBtn.addActionListener(e -> {
                new ExportF().setVisible(true);
                dispose();
            });

            JButton editBtn = createPillButton("Edit");
            editBtn.addActionListener(e -> {
                new ProfileEditor().setVisible(true);
                dispose(); // ปิดหน้าเก่า แล้วไปหน้า Edit
            });

            btnPanel.add(exportBtn);
            btnPanel.add(editBtn);

            card.add(btnPanel, gbc);
        }

        // Only show edit button for non-minimal mode
        if (!isMinimalMode) {
            JButton editBtn = createPillButton("Edit");
            editBtn.addActionListener(e -> {
                new ProfileEditor().setVisible(true);
                dispose(); // ปิดหน้าเก่า แล้วไปหน้า Edit
            });
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.weighty = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            btnPanel.setOpaque(false);
            btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

            btnPanel.add(editBtn);

            card.add(btnPanel, gbc);
        }

        return card; // ต้อง return card เสมอ ห้าม return null
    }

    // ========= COMPONENTS & HELPERS ==========

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
                System.err.println("Error loading image: " + e.getMessage());
            }
        }
        return new ImageIcon(createPlaceholderImage(size, new Color(255, 182, 193))); // สีชมพูอ่อน
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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 40));
        return btn;
    }

    private JButton createPillButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Hover Effect
                if (getModel().isRollover()) {
                    g2.setColor(new Color(245, 245, 245));
                } else {
                    g2.setColor(Color.WHITE);
                }
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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 40)); // กว้างขึ้นนิดหน่อย
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