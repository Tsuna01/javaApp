package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import model.Profiles;
import service.API;
import service.Auth;
import service.User;
import service.ProfileService; // Import Service

public class ProfileEditor extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);
    // Fonts
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 12);

    private JTextField nameField;
    private JTextField emailField;
    private JTextArea bioArea;
    private JLabel avatarLarge;
    private File selectedImageFile; // ไฟล์รูปที่ถูกเลือกเตรียมอัปโหลด

    public ProfileEditor() {
        initialize();
    }

    private void initialize() {
        setTitle("Profile Edit");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        add(mainPanel, BorderLayout.CENTER);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        mainPanel.add(body, BorderLayout.CENTER);

        body.add(createEditorCard());
    }

    private JPanel createEditorCard() {
        // ... (ส่วนการวาด Card เหมือนเดิม ละไว้เพื่อความกระชับ) ...
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(900, 550));
        card.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Title Section
        JLabel title = new JLabel("Edit Profile");
        title.setFont(FONT_TITLE);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.LIGHT_GRAY);
        titlePanel.add(sep, BorderLayout.CENTER);
        card.add(titlePanel, BorderLayout.NORTH);

        // Content Section
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);
        gbc.fill = GridBagConstraints.BOTH;

        // --- Left Column ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTH;
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        // Avatar
        avatarLarge = new JLabel(new ImageIcon(createPlaceholderImage(150, Color.PINK)));
        avatarLarge.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ปุ่ม Upload Image
        JButton uploadImgBtn = createUploadButton("Upload Image");
        uploadImgBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadImgBtn.addActionListener(e -> handleImageUpload()); // เรียกฟังก์ชันเลือกรูป

        JButton uploadFileBtn = createUploadButton("Upload File");
        uploadFileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftCol.add(avatarLarge);
        leftCol.add(Box.createVerticalStrut(20));
        leftCol.add(uploadImgBtn);
        leftCol.add(Box.createVerticalStrut(10));
        leftCol.add(uploadFileBtn);
        content.add(leftCol, gbc);

        // --- Right Column ---
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7; gbc.weighty = 1.0;
        JPanel rightCol = new JPanel(new GridBagLayout());
        rightCol.setOpaque(false);
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.insets = new Insets(0, 0, 15, 15);
        formGbc.weightx = 0.5; formGbc.gridy = 0;

        User currentUser = Auth.getAuthUser();

        // Input Fields
        formGbc.gridx = 0;
        rightCol.add(createLabeledInput("Display Name", currentUser.getName()), formGbc);
        formGbc.gridx = 1; formGbc.insets = new Insets(0, 15, 15, 0);
        rightCol.add(createLabeledInput("Email", currentUser.getEmail()), formGbc);

        // Bio Section
        formGbc.gridx = 0; formGbc.gridy = 1; formGbc.gridwidth = 2;
        formGbc.weighty = 1.0; formGbc.fill = GridBagConstraints.BOTH;
        formGbc.insets = new Insets(10, 0, 0, 0);
        JPanel bioPanel = new JPanel(new BorderLayout(0, 5));
        bioPanel.setOpaque(false);
        JLabel bioLabel = new JLabel("Bio / introduction");
        bioLabel.setFont(FONT_LABEL);

        // Load Bio Logic
        ArrayList<Profiles> profiles = API.getProfile(currentUser.getId());
        String BIO = "No comment 101";
        if (profiles != null && !profiles.isEmpty()) {
            String tempBio = profiles.get(0).getBio();
            if (tempBio != null && !tempBio.equals("null") && !tempBio.trim().isEmpty()) {
                BIO = tempBio;
            }
            // TODO: ถ้ามีรูปเก่า อาจจะโหลดมาแสดงที่ avatarLarge ตรงนี้ด้วย
        }

        bioArea = new JTextArea(BIO);
        bioArea.setFont(FONT_INPUT);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(15), new EmptyBorder(10, 10, 10, 10)));
        bioPanel.add(bioLabel, BorderLayout.NORTH);
        bioPanel.add(bioArea, BorderLayout.CENTER);
        rightCol.add(bioPanel, formGbc);
        content.add(rightCol, gbc);
        card.add(content, BorderLayout.CENTER);

        // Footer Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(20, 0, 0, 0));
        JButton cancelBtn = createActionButton("Cancel", false);
        JButton saveBtn = createActionButton("SAVE", true);

        // ==========================================================
        // ส่วน Logic ปุ่ม SAVE ที่เพิ่มการบันทึกรูป
        // ==========================================================
        saveBtn.addActionListener(e -> {
            // 1. ตรวจสอบข้อมูล Text
            if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อและอีเมล", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. เตรียม Object User สำหรับ Text Update
            User userToUpdate = Auth.getAuthUser();
            userToUpdate.setName(nameField.getText().trim());
            userToUpdate.setEmail(emailField.getText().trim());
            String bioText = bioArea.getText();

            // 3. ทำการบันทึก Text Profile
            boolean textSuccess = ProfileService.updateProfile(userToUpdate, bioText);
            boolean imageSuccess = true;
            if (selectedImageFile != null) {
                String savedPath = saveImageToLocal(selectedImageFile, userToUpdate.getId());

                if (savedPath != null) {
                    // บันทึก Path ลง Database
                    imageSuccess = ProfileService.updateImage(userToUpdate, savedPath);
                } else {
                    imageSuccess = false;
                }
            }

            // 5. แจ้งผลลัพธ์
            if (textSuccess && imageSuccess) {
                JOptionPane.showMessageDialog(this, "บันทึกข้อมูลและรูปภาพสำเร็จ!");
                dispose();
            } else if (textSuccess) {
                JOptionPane.showMessageDialog(this, "บันทึกข้อมูลสำเร็จ แต่รูปภาพมีปัญหา", "Warning", JOptionPane.WARNING_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "บันทึกไม่สำเร็จ กรุณาลองใหม่", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dispose());
        footer.add(cancelBtn);
        footer.add(saveBtn);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    // ==========================================================
    // Method เลือกรูปภาพ
    // ==========================================================
    private void handleImageUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("เลือกรูปภาพโปรไฟล์");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            try {
                BufferedImage originalImage = ImageIO.read(selectedImageFile);
                if (originalImage == null) return;

                // Preview รูปทันที
                Image scaledImage = originalImage.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                avatarLarge.setIcon(new ImageIcon(scaledImage));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // ==========================================================
    // [เพิ่มใหม่] Method บันทึกไฟล์รูปภาพลงเครื่อง (Folder: user_images)
    // ==========================================================
    private String saveImageToLocal(File sourceFile, int userId) {
        try {
            // 1. สร้าง Folder เก็บรูป (ถ้ายังไม่มี)
            File directory = new File("user_images");
            if (!directory.exists()) {
                directory.mkdir();
            }

            // 2. กำหนดชื่อไฟล์ใหม่ (ใช้ ID เพื่อไม่ให้ไฟล์ซ้ำกัน) -> profile_101.png
            String newFileName = "profile_" + userId + ".png";
            File destFile = new File(directory, newFileName);

            // 3. อ่านรูปจากไฟล์ต้นฉบับ
            BufferedImage image = ImageIO.read(sourceFile);

            // 4. เขียนรูปลงไฟล์ใหม่ (บังคับเป็น PNG)
            ImageIO.write(image, "png", destFile);

            // 5. คืนค่า Path เพื่อนำไปลง Database (ใช้ path แบบ relative หรือ absolute ตามต้องการ)
            // ในที่นี้ส่งกลับเป็น Path ของไฟล์ที่เซฟไป
            return destFile.getPath().replace("\\", "/");

        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ========= COMPONENTS (Helper Methods) ==========
    private JPanel createLabeledInput(String labelText, String value) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        JTextField field = new JTextField(value) {
            @Override
            protected void paintComponent(Graphics g) {
                if (!isOpaque() && getBorder() instanceof RoundedBorder) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fill(((RoundedBorder) getBorder()).getShape(0, 0, getWidth() - 1, getHeight() - 1));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        if (labelText.equals("Display Name")) nameField = field;
        else if (labelText.equals("Email")) emailField = field;
        field.setFont(FONT_INPUT);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(20), new EmptyBorder(5, 15, 5, 15)));
        field.setPreferredSize(new Dimension(200, 40));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createUploadButton(String text) {
        JButton btn = new JButton("  " + text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.BLACK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
    }

    private JButton createActionButton(String text, boolean isSave) {
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
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.BLACK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 40));
        if (isSave) btn.setText("💾 " + text);
        return btn;
    }

    private Image createPlaceholderImage(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return img;
    }

    private static class RoundedBorder extends LineBorder {
        private int radius;
        public RoundedBorder(int radius) { super(Color.BLACK, 1, true); this.radius = radius; }
        public Shape getShape(int x, int y, int w, int h) { return new RoundRectangle2D.Float(x, y, w, h, radius, radius); }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(lineColor);
            g2.draw(getShape(x, y, width - 1, height - 1));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProfileEditor().setVisible(true));
    }
}