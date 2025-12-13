package ui.component;

import service.Auth;
import ui.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Navbar {

    public Navbar() {

    }

    public JPanel build() {
        return createHeader();
    }

    private JPanel createHeader() {
        String nameUser = Auth.getAuthUser().getName();

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

        JLabel avatar = new JLabel(new ImageIcon(createPlaceholderImage(50, Color.WHITE))); // Placeholder
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
        if(addEnvent.equals("admin")){ // *ควรใช้ .equals() ในการเปรียบเทียบ String*
            txtBTN = "Add Event";
            txtBTN2 = "My Job";
        }else {
            txtBTN = "Available Job";
            txtBTN2 = "My Job";
        }

        JButton btn1 = createNavButton(txtBTN);
        JButton btn2 = createNavButton(txtBTN2);
        btn1.addActionListener(e -> {

            if (btn1.getText().equals("Add Event")) {

                new AddEvent().setVisible(true);
                Window window = SwingUtilities.getWindowAncestor(btn1);
                if (window != null) {
                    window.dispose();
                }
            } else if (btn1.getText().equals("Available Job")) {
                new AvailableJob().setVisible(true);
                Window window = SwingUtilities.getWindowAncestor(btn1);
                if (window != null) {
                    window.dispose();
                }
            }
        });

        btn2.addActionListener(e -> {
            if (Auth.getAuthUser().getStatus().equalsIgnoreCase("admin")) {

                new Workmenu().setVisible(true);
                Window window = SwingUtilities.getWindowAncestor(btn1);
                if (window != null) {
                    window.dispose();
                }
            } else if (Auth.getAuthUser().getStatus().equalsIgnoreCase("student")) {
                new MyJob().setVisible(true);
                Window window = SwingUtilities.getWindowAncestor(btn1);
                if (window != null) {
                    window.dispose();
                }
            }
        });

        right.add(btn1);
        right.add(btn2);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
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
                    // Hover state - white with slight transparency
                    g2.setColor(new Color(255, 255, 255, 230));
                } else {
                    // Normal state - semi-transparent white
                    g2.setColor(new Color(255, 255, 255, 180));
                }

                // Draw rounded rectangle
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Add subtle shadow/border effect
                if (!hovered) {
                    g2.setColor(new Color(255, 255, 255, 100));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                }

                g2.dispose();

                // Draw text
                super.paintComponent(g);
            }
        };

        btn.setText(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(new Color(80, 80, 80)); // Dark gray initial color
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect with smooth color transition
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(255, 100, 120)); // Pink color on hover
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(80, 80, 80)); // Dark gray normal
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
