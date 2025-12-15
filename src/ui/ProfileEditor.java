package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import ui.component.Navbar;

public class ProfileEditor extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);


    // Fonts
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 28);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 12);

    public ProfileEditor() {
        initialize();
    }

    private void initialize() {
        setTitle("Profile Edit");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        add(mainPanel, BorderLayout.CENTER);

        // ========= BODY ==========
        JPanel body = new JPanel(new GridBagLayout()); // Center the card
        body.setOpaque(false);
        mainPanel.add(body, BorderLayout.CENTER);

        body.add(createEditorCard());
    }

    // ========= EDITOR CARD ==========
    private JPanel createEditorCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);

                // BG
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(900, 550));
        card.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Title
        JLabel title = new JLabel("Edit Profile");
        title.setFont(FONT_TITLE);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Separator line
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(title, BorderLayout.NORTH);
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.LIGHT_GRAY);
        titlePanel.add(sep, BorderLayout.CENTER);

        card.add(titlePanel, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 20, 0, 20);
        gbc.fill = GridBagConstraints.BOTH;

        // --- Left Column: Avatar & Upload ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTH;

        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        JLabel avatarLarge = new JLabel(new ImageIcon(createPlaceholderImage(150, Color.PINK)));
        avatarLarge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton uploadImgBtn = createUploadButton("Upload Image");
        uploadImgBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton uploadFileBtn = createUploadButton("Upload File");
        uploadFileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftCol.add(avatarLarge);
        leftCol.add(Box.createVerticalStrut(20));
        leftCol.add(uploadImgBtn);
        leftCol.add(Box.createVerticalStrut(10));
        leftCol.add(uploadFileBtn);

        content.add(leftCol, gbc);

        // --- Right Column: Form ---
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;

        JPanel rightCol = new JPanel(new GridBagLayout());
        rightCol.setOpaque(false);

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.insets = new Insets(0, 0, 15, 15);
        formGbc.weightx = 0.5;
        formGbc.gridy = 0;

        // Row 1: Display Name & Email
        formGbc.gridx = 0;
        rightCol.add(createLabeledInput("Display Name", "Elysia Athome"), formGbc);

        formGbc.gridx = 1;
        formGbc.insets = new Insets(0, 15, 15, 0);
        rightCol.add(createLabeledInput("Email", "Cyrene.33550336@gmail.com"), formGbc);

        // Row 2: Bio
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formGbc.gridwidth = 2;
        formGbc.weighty = 1.0;
        formGbc.fill = GridBagConstraints.BOTH;
        formGbc.insets = new Insets(10, 0, 0, 0);

        JPanel bioPanel = new JPanel(new BorderLayout(0, 5));
        bioPanel.setOpaque(false);
        JLabel bioLabel = new JLabel("Bio / introduction");
        bioLabel.setFont(FONT_LABEL);

        JTextArea bioArea = new JTextArea(
                "“ผม/ดิฉันเป็นนักศึกษามหาวิทยาลัย [ชื่อมหาวิทยาลัย] สาขา [ชื่อสาขา] ที่มีความสนใจในด้าน\n" +
                        "[ระบุความสนใจ เช่น เทคโนโลยี นวัตกรรม การออกแบบ หรือการตลาด] ชอบการเรียนรู้สิ่งใหม่ ๆ\n" +
                        "และพัฒนาทักษะอย่างต่อเนื่อง\n" +
                        "พร้อมเปิดรับโอกาสในการฝึกงานและประสบการณ์ที่ช่วยต่อยอดสู่การทํางานในอนาคต”");
        bioArea.setFont(FONT_INPUT);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15),
                new EmptyBorder(10, 10, 10, 10)));

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
        JButton saveBtn = createActionButton("SAVE", true);// Should have icon

        cancelBtn.addActionListener(e -> {
            dispose();
        });
        footer.add(cancelBtn);
        footer.add(saveBtn);

        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    // ========= COMPONENTS ==========

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
        field.setFont(FONT_INPUT);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20),
                new EmptyBorder(5, 15, 5, 15)));
        field.setPreferredSize(new Dimension(200, 40));

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JButton createUploadButton(String text) {
        JButton btn = new JButton("  " + text) { // Add space for icon placeholder
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230)); // Light gray
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        // Add icon if possible, for now just text
        // btn.setIcon(...)
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

        if (isSave) {
            // Add save icon logic here if needed
            btn.setText("💾 " + text);
        }

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

    // Helper class for rounded borders
    private static class RoundedBorder extends LineBorder {
        private int radius;

        public RoundedBorder(int radius) {
            super(Color.BLACK, 1, true);
            this.radius = radius;
        }

        public Shape getShape(int x, int y, int w, int h) {
            return new RoundRectangle2D.Float(x, y, w, h, radius, radius);
        }

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
