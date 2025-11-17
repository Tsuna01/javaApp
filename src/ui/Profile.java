package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Profile extends JFrame {

    // --- Theme ---
    private static final Color BG_COLOR = new Color(240, 243, 247);
    private static final Color HEADER_LEFT = new Color(255, 174, 201);
    private static final Color HEADER_RIGHT = new Color(255, 214, 165);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(40, 40, 40);

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 26);
    private static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font FONT_THAI = new Font("Tahoma", Font.PLAIN, 14);
    private static final Font FONT_THAI_BOLD = new Font("Tahoma", Font.BOLD, 18);
    public Profile() {
        setTitle("Profile");
        setSize(980, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        setVisible(true);
    }


    // ----------------------------------------------------
    // HEADER (GRADIENT)
    // ----------------------------------------------------
    private JPanel createHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp =
                        new GradientPaint(0, 0, HEADER_LEFT, getWidth(), 0, HEADER_RIGHT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        header.setPreferredSize(new Dimension(0, 65));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 25, 0, 25));

        JLabel title = new JLabel("Elysia Athome");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        right.setOpaque(false);

        JLabel l1 = new JLabel("Available Job");
        l1.setForeground(Color.WHITE);
        JLabel l2 = new JLabel("My Job");
        l2.setForeground(Color.WHITE);

        right.add(l1);
        right.add(l2);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ----------------------------------------------------
    // MAIN CONTENT (BACK + CARD)
    // ----------------------------------------------------
    private JPanel createContent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Modern Back Button
        JButton back = createModernButton("← Back");
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> {
            dispose();
            new SectionMain().setVisible(true);
        });
        panel.add(back);
        panel.add(Box.createVerticalStrut(20));

        JPanel card = createCard();
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(card);

        return panel;
    }

    // ----------------------------------------------------
    // PROFILE CARD (Rounded + Shadow)
    // ----------------------------------------------------
    private JPanel createCard() {

        JPanel card = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 30, 30);

                // card bg
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, 30, 30);
            }
        };

        card.setOpaque(false);
        card.setBorder(new EmptyBorder(35, 35, 35, 35));

        card.add(createTopProfile(), BorderLayout.NORTH);
        card.add(createInfoSection(), BorderLayout.CENTER);
        card.add(createEditButtonBar(), BorderLayout.SOUTH);

        return card;
    }

    // ----------------------------------------------------
    // TOP PROFILE SECTION (AVATAR + TEXT)
    // ----------------------------------------------------
    private JPanel createTopProfile() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // avatar circle
        ImageIcon icon = new ImageIcon("avatar.png");
        Image circleImg = createCircularImage(icon.getImage(), 150);
        JLabel avatar = new JLabel(new ImageIcon(circleImg));
        avatar.setBorder(new EmptyBorder(0, 0, 0, 25));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel name = new JLabel("Elysia Athome");
        name.setFont(FONT_TITLE);
        name.setForeground(TEXT_DARK);

        JLabel id = new JLabel("ID : S33550336");
        id.setFont(FONT_SUBTITLE);

        JLabel email = new JLabel("Email : Cyrene.33550336@gmail.com");
        email.setFont(FONT_SUBTITLE);

        JLabel status = new JLabel("สถานะ : นักศึกษา");
        status.setFont(FONT_THAI);

        textPanel.add(name);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(id);
        textPanel.add(email);
        textPanel.add(status);

        panel.add(avatar, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    // ----------------------------------------------------
    // INFORMATION SECTION
    // ----------------------------------------------------
    private JPanel createInfoSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("ประวัติกิจกรรม");
        title.setFont(FONT_THAI_BOLD);

        JLabel count = new JLabel("งานที่ทำสำเร็จ : 10 งาน");
        count.setFont(FONT_THAI);

        JTextArea bio = new JTextArea("“ผม/ดิฉันเป็นนักศึกษามหาวิทยาลัย ... ”");
        bio.setLineWrap(true);
        bio.setWrapStyleWord(true);
        bio.setEditable(false);
        bio.setOpaque(false);
        bio.setFont(FONT_THAI);

        panel.add(title);
        panel.add(count);
        panel.add(Box.createVerticalStrut(15));
        panel.add(bio);

        return panel;
    }

    // ----------------------------------------------------
    // EDIT BUTTON BAR
    // ----------------------------------------------------
    private JPanel createEditButtonBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        JButton edit = createModernButton("Edit");
        panel.add(edit);

        return panel;
    }

    // ----------------------------------------------------
    // Utility: Modern Button
    // ----------------------------------------------------
    private JButton createModernButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 180, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        btn.setFont(FONT_THAI);
        btn.setForeground(Color.BLACK);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(110, 35));

        return btn;
    }

    // ----------------------------------------------------
    // Utility: Create Circular Image
    // ----------------------------------------------------
    private Image createCircularImage(Image image, int size) {
        BufferedImage output = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();

        g2.setClip(new Ellipse2D.Float(0, 0, size, size));
        g2.drawImage(image, 0, 0, size, size, null);
        g2.dispose();

        return output;
    }
}
