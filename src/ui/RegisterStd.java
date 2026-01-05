package ui;

import service.RegisterManager;
import service.Student;
import service.User;
import util.RoundedLineBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class RegisterStd extends JFrame {

    private String tempName;
    private String tempEmail;
    private String tempPassword;

    public RegisterStd(String name, String email, String password) {
        this.tempName = name;
        this.tempEmail = email;
        this.tempPassword = password;

        setTitle("Sign Up - Student ID");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 540);
        setMinimumSize(new Dimension(780, 480));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 245, 245));
        setContentPane(root);

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(245, 245, 245));
        main.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 0, 0, 0);
        gc.fill = GridBagConstraints.BOTH;

        // ------- LEFT (รูปมีขอบมน) ------- //
        JPanel left = new JPanel(new BorderLayout()) {
            private BufferedImage bgImage;
            {
                try {
                    bgImage = ImageIO.read(new File("src/assets/bg.png"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();
                    int radius = 24;
                    g2d.setClip(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));

                    int imgW = bgImage.getWidth();
                    int imgH = bgImage.getHeight();
                    double scaleX = (double) w / imgW;
                    double scaleY = (double) h / imgH;
                    double scale = Math.max(scaleX, scaleY);
                    int newW = (int) (imgW * scale);
                    int newH = (int) (imgH * scale);
                    int x = (w - newW) / 2;
                    int y = (h - newH) / 2;

                    g2d.drawImage(bgImage, x, y, newW, newH, this);
                    g2d.dispose();
                }
            }
        };
        left.setOpaque(false);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0.45;
        gc.weighty = 1;
        main.add(left, gc);

        // ------- RIGHT (Form) ------- //
        JPanel right = new JPanel();
        right.setLayout(new GridBagLayout());
        right.setBackground(new Color(245, 245, 245));
        right.setBorder(new EmptyBorder(40, 60, 40, 60));

        GridBagConstraints g2 = new GridBagConstraints();
        g2.gridx = 0;
        g2.fill = GridBagConstraints.HORIZONTAL;
        g2.insets = new Insets(4, 0, 4, 0);
        g2.weightx = 1.0;

        // Header
        JLabel h1 = new JLabel("SIGN UP");
        h1.setFont(new Font("SansSerif", Font.BOLD, 32));
        h1.setForeground(new Color(31, 41, 55));
        g2.gridy = 0;
        g2.insets = new Insets(0, 0, 4, 0);
        right.add(h1, g2);

        // Subtitle
        JLabel subtitle = new JLabel("Enter your Student ID");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(107, 114, 128));
        g2.gridy = 1;
        g2.insets = new Insets(0, 0, 24, 0);
        right.add(subtitle, g2);

        // Student ID input
        g2.gridy = 2;
        g2.insets = new Insets(4, 0, 8, 0);
        JPanel stdPanel = createInputWithIcon("🎓", "Student ID");
        JTextField txtStd = (JTextField) stdPanel.getComponent(1);
        right.add(stdPanel, g2);

        // Create Button
        g2.gridy = 3;
        g2.insets = new Insets(16, 0, 16, 0);
        JButton btnRegister = createGradientButton("Create an account");

        btnRegister.addActionListener(e -> {
            String stdId = txtStd.getText().trim();

            if (stdId.isEmpty() || stdId.equals("Student ID")) {
                JOptionPane.showMessageDialog(this, "Please enter Student ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new Student(0, tempName, tempEmail, tempPassword, "Student", stdId);
            boolean success = RegisterManager.register(newUser);

            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                SwingUtilities.invokeLater(() -> new Login().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed (Email might be taken)", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        right.add(btnRegister, g2);

        // Back link
        g2.gridy = 4;
        g2.insets = new Insets(4, 0, 0, 0);
        JLabel back = new JLabel("← Back to previous step");
        back.setFont(new Font("SansSerif", Font.PLAIN, 12));
        back.setForeground(new Color(255, 120, 80));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.setHorizontalAlignment(SwingConstants.CENTER);
        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new Register().setVisible(true);
            }

            public void mouseEntered(java.awt.event.MouseEvent e) {
                back.setText("<html><u>← Back to previous step</u></html>");
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                back.setText("← Back to previous step");
            }
        });
        right.add(back, g2);

        // Spacer
        g2.gridy = 5;
        g2.weighty = 1;
        right.add(Box.createVerticalStrut(1), g2);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 0.55;
        gc.weighty = 1;
        main.add(right, gc);

        root.add(main, BorderLayout.CENTER);
        getRootPane().setDefaultButton(btnRegister);
    }

    private JPanel createInputWithIcon(String iconText, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(229, 231, 235), 1, 12),
                new EmptyBorder(12, 14, 12, 14)));

        JLabel icon = new JLabel(iconText);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 14));
        icon.setForeground(new Color(156, 163, 175));
        icon.setBorder(new EmptyBorder(0, 0, 0, 10));

        JTextField field = new JTextField();
        field.setBorder(null);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setText(placeholder);
        field.setForeground(new Color(156, 163, 175));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(55, 65, 81));
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(156, 163, 175));
                }
            }
        });

        panel.add(icon, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createGradientButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 150, 100),
                        getWidth(), 0, new Color(255, 180, 150));
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 48));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterStd("Test", "test@email.com", "1234").setVisible(true));
    }
}