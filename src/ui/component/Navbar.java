package ui.component;

import service.Auth;
import ui.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Navbar {

    public Navbar() {

    }

    public JPanel build() {
        return createHeader();
    }

    private JPanel createHeader() {
        String nameUser = Auth.getAuthUser().getName();

        // สมมติว่า User มี path รูปภาพ (ถ้าไม่มีให้ใส่ path รูป default หรือ null)
        // String imagePath = Auth.getAuthUser().getImagePath();
        String imagePath = "src/assets/";

        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient Pink to Orange
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 130, 140), getWidth(), 0,
                        new Color(255, 210, 160));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(1200, 80));
        header.setBorder(new EmptyBorder(0, 50, 0, 50));

        // Left: Avatar + Name
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        left.setOpaque(false);

        ImageIcon icon = getAvatarImage(imagePath, 50);
        JLabel avatar = new JLabel(icon);

        // 1. เปลี่ยน Cursor เป็นรูปมือเมื่อเอาเมาส์ไปวาง
        avatar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 2. เพิ่ม Event คลิก
        avatar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // เปิดหน้า Profile
                new Profile().setVisible(true); // สมมติว่าคลาสหน้าโปรไฟล์ชื่อ Profile

                // ปิดหน้าต่างปัจจุบัน (Navbar อยู่ในหน้าไหน หน้านั้นจะถูกปิด)
                Window window = SwingUtilities.getWindowAncestor(avatar);
                if (window != null) {
                    window.dispose();
                }
            }
        });
        // -----------------------------

        JLabel name = new JLabel(nameUser);
        name.setFont(new Font("SansSerif", Font.BOLD, 20));
        name.setForeground(Color.WHITE);

        left.add(avatar);
        left.add(name);

        // Right: Beautiful Buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        right.setOpaque(false);

        //btn Navbar
        String addEnvent = Auth.getAuthUser().getStatus();
        String txtBTN;
        String txtBTN2;

        if (addEnvent != null && addEnvent.equals("admin")) { // เพิ่มเช็ค null เพื่อความปลอดภัย
            txtBTN = "Add Event";
            txtBTN2 = "My Job";
        } else {
            txtBTN = "Available Job";
            txtBTN2 = "My Job";
        }

        JButton btn1 = createNavButton(txtBTN);
        JButton btn2 = createNavButton(txtBTN2);

        // Action Listeners (คงเดิม)
        btn1.addActionListener(e -> {
            if (btn1.getText().equals("Add Event")) {
                new AddEvent().setVisible(true);
                Window window = SwingUtilities.getWindowAncestor(btn1);
                if (window != null) window.dispose();
            } else if (btn1.getText().equals("Available Job")) {
                new AvailableJob().setVisible(true);
                Window window = SwingUtilities.getWindowAncestor(btn1);
                if (window != null) window.dispose();
            }
        });

        btn2.addActionListener(e -> {
            String status = Auth.getAuthUser().getStatus();
            if (status != null && status.equalsIgnoreCase("admin")) {
                new Workmenu().setVisible(true);
                Window window = SwingUtilities.getWindowAncestor(btn1);
                if (window != null) window.dispose();
            } else if (status != null && status.equalsIgnoreCase("student")) {
                new MyJob().setVisible(true);
                Window window = SwingUtilities.getWindowAncestor(btn1);
                if (window != null) window.dispose();
            }
        });

        right.add(btn1);
        right.add(btn2);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // --- เมธอดใหม่สำหรับโหลดและตัดรูปวงกลม ---
    private ImageIcon getAvatarImage(String path, int size) {
        try {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                BufferedImage originalImage = ImageIO.read(imgFile);

                // สร้างภาพใหม่ที่เป็นวงกลม
                BufferedImage circledImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = circledImage.createGraphics();

                // เปิด Anti-aliasing เพื่อขอบที่เนียน
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                // สร้างพื้นที่ตัด (Clip) เป็นวงกลม
                g2.setClip(new Ellipse2D.Float(0, 0, size, size));

                // วาดรูปลงไป (ปรับขนาดให้พอดีกับ size)
                g2.drawImage(originalImage, 0, 0, size, size, null);

                // เพิ่มเส้นขอบบางๆ สีขาว (Optional) เพื่อความสวยงาม
                g2.setClip(null);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(1, 1, size-2, size-2);

                g2.dispose();
                return new ImageIcon(circledImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ถ้าโหลดรูปไม่ได้ ให้ใช้ Placeholder เดิม
        return new ImageIcon(createPlaceholderImage(size, Color.WHITE));
    }

    // Create beautiful navigation buttons with modern styling
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background with glassmorphism effect
                if (hovered) {
                    g2.setColor(new Color(255, 255, 255, 230));
                } else {
                    g2.setColor(new Color(255, 255, 255, 180));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                if (!hovered) {
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setText(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(new Color(80, 80, 80));
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(255, 100, 120));
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(80, 80, 80));
                btn.repaint();
            }
        });

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
}