package ui;

import util.ImageUtils;
import util.RoundedLineBorder;
import util.RoundedPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterStd extends JFrame {
    public RegisterStd() {
        setTitle("Sign Up - Myapp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(780, 540));
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

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1.2;
        gc.weighty = 1;
        main.add(left, gc);

        // ------- RIGHT (การ์ดมุมมน) ------- //
        JPanel right = new RoundedPanel(18, Color.WHITE, new Color(229, 231, 235));
        right.setLayout(new GridBagLayout());
        right.setBorder(new EmptyBorder(24, 24, 24, 24));

        GridBagConstraints g2 = new GridBagConstraints();
        g2.gridx = 0;
        g2.fill = GridBagConstraints.HORIZONTAL;
        g2.insets = new Insets(6, 12, 6, 12);
        g2.weightx = 1.0;

        // Header
        JLabel h1 = new JLabel("SIGN UP");
        h1.setFont(h1.getFont().deriveFont(Font.BOLD, 26f));
        h1.setHorizontalAlignment(SwingConstants.CENTER);
        h1.setForeground(new Color(31, 41, 55)); // slate-800
        g2.gridy = 0;
        right.add(h1, g2);

        // Subtitle
        JLabel subtitle = new JLabel("Create your STUDENT ID");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        subtitle.setForeground(new Color(107, 114, 128)); // gray-500
        g2.gridy = 1;
        right.add(subtitle, g2);

        // Email label
        g2.gridy = 2;
        g2.insets = new Insets(8, 12, 2, 12);
        JLabel emailLabel = new JLabel("Student ID");
        emailLabel.setForeground(new Color(71, 85, 105)); // slate-600
        right.add(emailLabel, g2);

        // Email input
        g2.gridy = 3;
        g2.insets = new Insets(2, 12, 6, 12);
        JTextField txtStd = new JTextField(20);
        txtStd.setBorder(compoundRoundedBorder(14, new Color(203, 213, 225), 2,
                new Insets(10, 12, 10, 12)));
        right.add(txtStd, g2);



        // ปุ่ม Create an account
        g2.gridy = 12;
        g2.insets = new Insets(12, 12, 6, 12);
        JButton btnRegister = new JButton("Create an account");
        btnRegister.setFocusPainted(false);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBackground(new Color(251, 146, 60)); // orange-400
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setPreferredSize(new Dimension(0, 44));
        btnRegister.setFont(btnRegister.getFont().deriveFont(Font.BOLD, 14f));

        btnRegister.addActionListener(e -> {
            String Std_id = txtStd.getText().trim();

            // Validation
            if (Std_id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // TODO: Add registration logic here
            JOptionPane.showMessageDialog(this, "Account created successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
            SwingUtilities.invokeLater(() -> new Login().setVisible(true));
        });

        right.add(btnRegister, g2);

        // ดันเนื้อหาขึ้นเล็กน้อยด้วย weighty
        g2.gridy = 14;
        g2.weighty = 1;
        right.add(Box.createVerticalStrut(1), g2);

        // ใส่การ์ดลงคอลัมน์ขวา
        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1.0;
        gc.weighty = 1;
        main.add(right, gc);

        root.add(main, BorderLayout.CENTER);

        getRootPane().setDefaultButton(btnRegister);
    }

    /** รวม RoundedBorder (เส้น) + EmptyBorder (padding ภายใน) */
    private static Border compoundRoundedBorder(int radius, Color lineColor, int thickness, Insets innerPadding) {
        return BorderFactory.createCompoundBorder(
                new RoundedLineBorder(lineColor, thickness, radius),
                new EmptyBorder(innerPadding));
    }

    // ---- main สำหรับรันทดสอบ ----
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterStd().setVisible(true));
    }
}
