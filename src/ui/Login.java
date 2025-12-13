package ui;

import service.Auth;
import util.ImageUtils;
import util.RoundedLineBorder;
import util.RoundedPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class Login extends JFrame {
    public Login() {
        setTitle("Myapp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 540);
        setMinimumSize(new Dimension(780, 480));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // ------- MAIN (2 คอลัมน์) ------- //
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(248, 250, 252));
        main.setBorder(new EmptyBorder(24, 24, 24, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(12, 12, 12, 12);
        gc.fill = GridBagConstraints.BOTH;

        // ------- LEFT ------- //
        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(Color.WHITE);
        left.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel sut = new JLabel(ImageUtils.loadScaled("assets/hero.png", 360, 280));
        sut.setHorizontalAlignment(SwingConstants.CENTER);
        left.add(sut, BorderLayout.CENTER);

        gc.gridx = 0; gc.gridy = 0;
        gc.weightx = 1.2; gc.weighty = 1;
        main.add(left, gc);

        // ------- RIGHT (การ์ดมุมมน) ------- //
        JPanel right = new RoundedPanel(18, Color.WHITE, new Color(229, 231, 235)); // กรอบมน+เส้นขอบจาง
        right.setLayout(new GridBagLayout());
        right.setBorder(new EmptyBorder(24, 24, 24, 24));

        GridBagConstraints g2 = new GridBagConstraints();
        g2.gridx = 0;
        g2.fill = GridBagConstraints.HORIZONTAL;
        g2.insets = new Insets(6, 12, 6, 12);
        g2.weightx = 1.0;

        // Header
        JLabel h1 = new JLabel("Sign in");
        h1.setFont(h1.getFont().deriveFont(Font.BOLD, 24f));
        h1.setHorizontalAlignment(SwingConstants.CENTER);
        h1.setForeground(new Color(31, 41, 55)); // slate-800
        g2.gridy = 0;
        right.add(h1, g2);

        // Username label
        g2.gridy = 1;
        JLabel userLabel = new JLabel("Email");
        userLabel.setForeground(new Color(71, 85, 105)); // slate-600
        right.add(userLabel, g2);

        // Username input
        g2.gridy = 2;
        JTextField txtUser = new JTextField(20);
        txtUser.setBorder(compoundRoundedBorder(14, new Color(203, 213, 225), 2,  // เส้นขอบ
                                                new Insets(10, 12, 10, 12)));     // padding ข้างใน
        right.add(txtUser, g2);

        // Password label
        g2.gridy = 3;
        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(new Color(71, 85, 105));
        right.add(passLabel, g2);

        // Password input
        g2.gridy = 4;
        JPasswordField txtPass = new JPasswordField(20);
        txtPass.setBorder(compoundRoundedBorder(14, new Color(203, 213, 225), 2,
                                                new Insets(10, 12, 10, 12)));
        right.add(txtPass, g2);



        // ปุ่ม Login
        g2.gridy = 5;
        JButton btn = new JButton("Login");
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(59, 130, 246)); // blue-500
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 44));

        btn.addActionListener(e -> {
            String email = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());

            // เรียกใช้ Auth
            if (Auth.login(email, pass)) {
                JOptionPane.showMessageDialog(this, "ยินดีต้อนรับ " + Auth.getAuthUser().getName());

                // เปิดหน้าหลัก
                new Profile().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "อีเมลหรือรหัสผ่านไม่ถูกต้อง");
            }

        });
        
        right.add(btn, g2);
        
        // แถวตัวเลือกเล็ก ๆ
        g2.gridy = 7;
        JPanel opts = new JPanel(new BorderLayout());
        opts.setOpaque(false);
        JLabel member = new JLabel("Not a member?");
        JLabel forgot = new JLabel("<html><u> Sign up now </u></html>");
        forgot.setForeground(new Color(37, 99, 235)); // blue-600
        forgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new Register().setVisible(true);
                dispose();
            }
        });
        opts.add(member, BorderLayout.WEST);
        opts.add(forgot, BorderLayout.EAST);
        right.add(opts, g2);

        // ดันเนื้อหาขึ้นเล็กน้อยด้วย weighty
        g2.gridy = 8;
        g2.weighty = 1;
        right.add(Box.createVerticalStrut(1), g2);

        // ใส่การ์ดลงคอลัมน์ขวา
        gc.gridx = 1; gc.gridy = 0;
        gc.weightx = 1.0; gc.weighty = 1;
        main.add(right, gc);

        root.add(main, BorderLayout.CENTER);


        
        getRootPane().setDefaultButton(btn);
    }

    /** รวม RoundedBorder (เส้น) + EmptyBorder (padding ภายใน) */
    private static Border compoundRoundedBorder(int radius, Color lineColor, int thickness, Insets innerPadding) {
        return BorderFactory.createCompoundBorder(
                new RoundedLineBorder(lineColor, thickness, radius),
                new EmptyBorder(innerPadding)
        );
    }

    // ---- main สำหรับรันทดสอบ ----
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
