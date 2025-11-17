package ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Navbar {

    private static final Color HEADER_LEFT = new Color(255, 174, 201);
    private static final Color HEADER_RIGHT = new Color(255, 214, 165);

    public Navbar() {}

    public JPanel build() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(
                        0, 0, HEADER_LEFT,
                        getWidth(), 0, HEADER_RIGHT
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        header.setPreferredSize(new Dimension(0, 65));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 25, 0, 25));

        // LEFT TITLE
        JLabel title = new JLabel("Elysia Athome");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        // RIGHT MENU
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
}
