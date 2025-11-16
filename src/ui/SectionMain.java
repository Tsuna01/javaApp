package ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.swing.border.EmptyBorder;

public class SectionMain {

    private JFrame frame;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SectionMain s = new SectionMain();
            s.frame.setVisible(true);
        });
    }

    public SectionMain() {
        initialize();
    }

    private void initialize() {

        frame = new JFrame("Main");
        frame.setSize(1100, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // ------------ NAVBAR (เหมือนเดิม) -------------- //
        JPanel navbar = makeNavbar();
        frame.add(navbar, BorderLayout.NORTH);

        // ------------ CONTENT WRAPPER ---------------- //
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(new Color(245, 245, 245));
        frame.add(page, BorderLayout.CENTER);

        // ------------ TITLE -------------- //
        JLabel title = new JLabel("Available Jobs (8)");
        title.setFont(new Font("Tahoma", Font.BOLD, 26));
        title.setBorder(new EmptyBorder(20, 30, 20, 10));
        page.add(title, BorderLayout.NORTH);

        // ------------ GRID PANEL (2 คอลัม) ---------------- //
        JPanel grid = new JPanel();
        grid.setBackground(Color.WHITE);

        // ⭐ 2 คอลัมแนวตั้ง
        grid.setLayout(new GridLayout(0, 2, 40, 40)); // (rows, cols, hgap, vgap)
        grid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ทดสอบ add card 8 ใบ
        for (int i = 0; i < 50; i++) {
            grid.add(new Cardport());
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        page.add(scroll, BorderLayout.CENTER);
    }

    // ----------- NAVBAR METHOD ------------ //
    private JPanel makeNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setPreferredSize(new Dimension(100, 85));
        navbar.add(new GradientBar(), BorderLayout.CENTER);
        return navbar;
    }

    class GradientBar extends JPanel {
        public GradientBar() {
            setOpaque(false);
            setLayout(new BorderLayout());

            // LEFT SIDE
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            left.setOpaque(false);

            URL img = getClass().getResource("/assets/test1.png");
            JLabel avatar = new JLabel(new ImageIcon(img));

            JLabel name = new JLabel("Elysia Athome");
            name.setFont(new Font("Tahoma", Font.BOLD, 20));
            name.setForeground(Color.WHITE);

            left.add(avatar);
            left.add(name);

            add(left, BorderLayout.WEST);

            // RIGHT BUTTONS
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 25));
            right.setOpaque(false);

            JButton b1 = navBtn("Available Job");
            JButton b2 = navBtn("My Job");

            right.add(b1);
            right.add(b2);

            add(right, BorderLayout.EAST);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(255, 143, 178),
                    getWidth(), 0, new Color(255, 177, 109)
            );

            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private JButton navBtn(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(Color.WHITE);
        b.setPreferredSize(new Dimension(140, 35));
        b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return b;
    }
}
