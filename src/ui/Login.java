package ui;

import service.Auth;
import util.RoundedLineBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Login extends JFrame {
    public Login() {
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 540);
        setMinimumSize(new Dimension(780, 480));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 245, 245));
        setContentPane(root);

        // ------- MAIN (2 คอลัมน์) ------- //
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
                    bgImage = ImageIO.read(new File("src/assets/oop.png"));
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

                    // Clip เป็นมุมมน
                    g2d.setClip(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));

                    // Cover scaling
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

        // Header "LOGIN"
        JLabel h1 = new JLabel("LOGIN");
        h1.setFont(new Font("SansSerif", Font.BOLD, 32));
        h1.setForeground(new Color(31, 41, 55));
        g2.gridy = 0;
        g2.insets = new Insets(0, 0, 4, 0);
        right.add(h1, g2);

        // Subtitle
        JLabel subtitle = new JLabel("Enter your Email to access your account.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(107, 114, 128));
        g2.gridy = 1;
        g2.insets = new Insets(0, 0, 20, 0);
        right.add(subtitle, g2);

        // Email input with icon
        g2.gridy = 2;
        g2.insets = new Insets(4, 0, 8, 0);
        JPanel emailPanel = createInputWithIcon("@", "Email");
        JTextField txtUser = (JTextField) emailPanel.getComponent(1);
        right.add(emailPanel, g2);

        // Password input with icon
        g2.gridy = 3;
        g2.insets = new Insets(4, 0, 16, 0);
        JPanel passPanel = createPasswordInputWithIcon();
        JPasswordField txtPass = (JPasswordField) passPanel.getComponent(1);
        right.add(passPanel, g2);

        // Gradient Login Button
        g2.gridy = 4;
        g2.insets = new Insets(8, 0, 16, 0);
        JButton btn = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient สีส้ม-ชมพู
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 150, 100),
                        getWidth(), 0, new Color(255, 180, 150));
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

                // Text
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

        btn.addActionListener(e -> {
            String email = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (Auth.login(email, pass)) {
                JOptionPane.showMessageDialog(this, "ยินดีต้อนรับ " + Auth.getAuthUser().getName());
                new Profile().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "อีเมลหรือรหัสผ่านไม่ถูกต้อง");
            }
        });
        right.add(btn, g2);

        // "Not a member? Sign up now"
        g2.gridy = 5;
        g2.insets = new Insets(4, 0, 0, 0);
        JPanel opts = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        opts.setOpaque(false);
        JLabel member = new JLabel("Not a member?");
        member.setFont(new Font("SansSerif", Font.PLAIN, 12));
        member.setForeground(new Color(107, 114, 128));

        JLabel signup = new JLabel("Sign up now");
        signup.setFont(new Font("SansSerif", Font.PLAIN, 12));
        signup.setForeground(new Color(255, 120, 80));
        signup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signup.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new Register().setVisible(true);
                dispose();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                signup.setText("<html><u>Sign up now</u></html>");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                signup.setText("Sign up now");
            }
        });
        opts.add(member);
        opts.add(signup);
        right.add(opts, g2);

        // Spacer
        g2.gridy = 6;
        g2.weighty = 1;
        right.add(Box.createVerticalStrut(1), g2);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 0.55;
        gc.weighty = 1;
        main.add(right, gc);

        root.add(main, BorderLayout.CENTER);
        getRootPane().setDefaultButton(btn);
    }

    // สร้าง Input พร้อม Icon
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
        field.setForeground(new Color(55, 65, 81));

        // Placeholder
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

    private JPanel createPasswordInputWithIcon() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(229, 231, 235), 1, 12),
                new EmptyBorder(12, 14, 12, 14)));

        JLabel icon = new JLabel("🔒");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 12));
        icon.setForeground(new Color(156, 163, 175));
        icon.setBorder(new EmptyBorder(0, 0, 0, 10));

        JPasswordField field = new JPasswordField();
        field.setBorder(null);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(new Color(55, 65, 81));
        field.setEchoChar((char) 0);

        // Placeholder
        field.setText("Password");
        field.setForeground(new Color(156, 163, 175));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (new String(field.getPassword()).equals("Password")) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(new Color(55, 65, 81));
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setText("Password");
                    field.setForeground(new Color(156, 163, 175));
                }
            }
        });

        panel.add(icon, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
