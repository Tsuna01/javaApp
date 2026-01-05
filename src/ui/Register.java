package ui;

import util.RoundedLineBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Register extends JFrame {
    public Register() {
        setTitle("Sign Up");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(780, 540));
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
        right.setBorder(new EmptyBorder(30, 50, 30, 50));

        GridBagConstraints g2 = new GridBagConstraints();
        g2.gridx = 0;
        g2.fill = GridBagConstraints.HORIZONTAL;
        g2.insets = new Insets(3, 0, 3, 0);
        g2.weightx = 1.0;

        // Header
        JLabel h1 = new JLabel("SIGN UP");
        h1.setFont(new Font("SansSerif", Font.BOLD, 28));
        h1.setForeground(new Color(31, 41, 55));
        g2.gridy = 0;
        g2.insets = new Insets(0, 0, 2, 0);
        right.add(h1, g2);

        // Subtitle
        JLabel subtitle = new JLabel("Create your account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(107, 114, 128));
        g2.gridy = 1;
        g2.insets = new Insets(0, 0, 16, 0);
        right.add(subtitle, g2);

        // Email input
        g2.gridy = 2;
        g2.insets = new Insets(3, 0, 6, 0);
        JPanel emailPanel = createInputWithIcon("@", "Email");
        JTextField txtEmail = (JTextField) emailPanel.getComponent(1);
        right.add(emailPanel, g2);

        // Name input
        g2.gridy = 3;
        JPanel namePanel = createInputWithIcon("👤", "Name");
        JTextField txtName = (JTextField) namePanel.getComponent(1);
        right.add(namePanel, g2);

        // Password input
        g2.gridy = 4;
        JPanel passPanel = createPasswordInputWithIcon("Create password");
        JPasswordField txtPass = (JPasswordField) passPanel.getComponent(1);
        right.add(passPanel, g2);

        // Confirm Password input
        g2.gridy = 5;
        JPanel confirmPanel = createPasswordInputWithIcon("Confirm password");
        JPasswordField txtConfirm = (JPasswordField) confirmPanel.getComponent(1);
        right.add(confirmPanel, g2);

        // Next Button
        g2.gridy = 6;
        g2.insets = new Insets(12, 0, 12, 0);
        JButton btnRegister = createGradientButton("Next");

        btnRegister.addActionListener(e -> {
            String email = txtEmail.getText().trim();
            String name = txtName.getText().trim();
            String password = new String(txtPass.getPassword());
            String confirm = new String(txtConfirm.getPassword());

            // Reset placeholder check
            if (email.equals("Email"))
                email = "";
            if (name.equals("Name"))
                name = "";

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            dispose();
            String finalEmail = email;
            String finalName = name;
            SwingUtilities.invokeLater(() -> new RegisterStd(finalName, finalEmail, password).setVisible(true));
        });

        right.add(btnRegister, g2);

        // Sign in link
        g2.gridy = 7;
        g2.insets = new Insets(4, 0, 0, 0);
        JPanel opts = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        opts.setOpaque(false);
        JLabel member = new JLabel("Already have an account?");
        member.setFont(new Font("SansSerif", Font.PLAIN, 12));
        member.setForeground(new Color(107, 114, 128));

        JLabel signin = new JLabel("Sign in");
        signin.setFont(new Font("SansSerif", Font.PLAIN, 12));
        signin.setForeground(new Color(255, 120, 80));
        signin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new Login().setVisible(true);
                dispose();
            }

            public void mouseEntered(java.awt.event.MouseEvent e) {
                signin.setText("<html><u>Sign in</u></html>");
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                signin.setText("Sign in");
            }
        });
        opts.add(member);
        opts.add(signin);
        right.add(opts, g2);

        // Spacer
        g2.gridy = 8;
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
                new EmptyBorder(10, 14, 10, 14)));

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

    private JPanel createPasswordInputWithIcon(String placeholder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(new Color(229, 231, 235), 1, 12),
                new EmptyBorder(10, 14, 10, 14)));

        JLabel icon = new JLabel("🔒");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 12));
        icon.setForeground(new Color(156, 163, 175));
        icon.setBorder(new EmptyBorder(0, 0, 0, 10));

        JPasswordField field = new JPasswordField();
        field.setBorder(null);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.setForeground(new Color(156, 163, 175));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(new Color(55, 65, 81));
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
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
        btn.setPreferredSize(new Dimension(0, 46));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Register().setVisible(true));
    }
}
