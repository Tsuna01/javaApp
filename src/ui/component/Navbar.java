package ui.component;

import model.Profiles;
import service.API;
import service.Auth;
import service.CompletedAssignment;
import ui.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Navbar {

    public Navbar() {

    }

    public JPanel build() {
        return createHeader();
    }

    private JPanel createHeader() {
        String nameUser = Auth.getAuthUser().getName();

        // --- [จุดที่แก้ไข] การดึง Path รูปภาพ ---
        String imagePath = null;
        ArrayList<Profiles> profiles = API.getProfile(Auth.getAuthUser().getId());

        // ตรวจสอบว่ามีข้อมูลใน List ไหม ก่อนที่จะดึง
        if (profiles != null && !profiles.isEmpty()) {
            // ดึงตัวแรก (index 0) และเรียก getter ของ path รูป
            // **สำคัญ: ใน Class Profiles ต้องมี method getImagePath() หรือชื่อที่ตรงกับใน DB**
            // หากใน Model ชื่อ field คือ image_path ให้ใช้ getter ที่ตรงกัน
            imagePath = profiles.get(0).getImagePath();
        }

        // --------------------------------------

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

        // ส่ง imagePath ไป ถ้าเป็น null หรือหาไฟล์ไม่เจอ เมธอดจะใช้ Placeholder เอง
        ImageIcon icon = getAvatarImage(imagePath, 50);
        JLabel avatar = new JLabel(icon);

        // 1. เปลี่ยน Cursor เป็นรูปมือเมื่อเอาเมาส์ไปวาง
        avatar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 2. เพิ่ม Event คลิก
        avatar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // เปิดหน้า ProfileEditor (หรือ Profile)
                new Profile().setVisible(true);

                CompletedAssignment com = new CompletedAssignment();

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

        if (addEnvent != null && addEnvent.equals("admin")) {
            txtBTN = "Add Event";
            txtBTN2 = "My Job";
        } else {
            txtBTN = "Available Job";
            txtBTN2 = "My Job";
        }

        JButton btn1 = createNavButton(txtBTN);
        JButton btn2 = createNavButton(txtBTN2);

        // Action Listeners
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

    // --- เมธอดสำหรับโหลดและตัดรูปวงกลม ---
    private ImageIcon getAvatarImage(String path, int size) {
        // เพิ่มการเช็คว่า path ไม่เป็นค่าว่างหรือ null
        if (path != null && !path.trim().isEmpty() && !path.equals("null")) {
            try {
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    BufferedImage originalImage = ImageIO.read(imgFile);
                    if (originalImage != null) {
                        // สร้างภาพใหม่ที่เป็นวงกลม
                        BufferedImage circledImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = circledImage.createGraphics();

                        // เปิด Anti-aliasing
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                        // ตัดเป็นวงกลม
                        g2.setClip(new Ellipse2D.Float(0, 0, size, size));
                        g2.drawImage(originalImage, 0, 0, size, size, null);

                        // ขอบขาว
                        g2.setClip(null);
                        g2.setColor(new Color(255, 255, 255, 100));
                        g2.setStroke(new BasicStroke(2));
                        g2.drawOval(1, 1, size - 2, size - 2);

                        g2.dispose();
                        return new ImageIcon(circledImage);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error loading avatar: " + e.getMessage());
            }
        }

        // ถ้าโหลดรูปไม่ได้ หรือไม่มี path ให้ใช้ Placeholder
        return new ImageIcon(createPlaceholderImage(size, new Color(255, 255, 255, 150)));
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(255, 100, 120));
                btn.repaint();
            }
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