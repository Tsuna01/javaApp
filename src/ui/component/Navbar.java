package ui.component;

import util.RoundedButton;
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

        JLabel title = new JLabel("Elysia Athome");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        right.setOpaque(false);

        // =============== MENU BUTTONS ===============
        RoundedButton btnAvailable = new RoundedButton("Available Job");
        RoundedButton btnMyJob     = new RoundedButton("My Job");
        RoundedButton btnLogout    = new RoundedButton("Logout");

        btnAvailable.setRadius(15);
        btnMyJob.setRadius(15);
        btnLogout.setRadius(15);

        right.add(btnAvailable);
        right.add(btnMyJob);
        right.add(btnLogout);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }
}
