package ui;

import ui.component.Cardport;
import ui.component.Navbar;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class SectionMain extends JFrame {

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

        setTitle("Main");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------- ใช้ Navbar จาก Navbar.java ---------- //
        JPanel navbar = new Navbar().build();
        add(navbar, BorderLayout.NORTH);

        // ------------ CONTENT WRAPPER ---------------- //
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(new Color(245, 245, 245));
        add(page, BorderLayout.CENTER);

        // ------------ TITLE -------------- //
        JLabel title = new JLabel("Available Jobs (8)");
        title.setFont(new Font("Tahoma", Font.BOLD, 26));
        title.setBorder(new EmptyBorder(20, 30, 20, 10));
        page.add(title, BorderLayout.NORTH);

        // ------------ GRID PANEL (2 คอลัม) ---------------- //
        JPanel grid = new JPanel();
        grid.setBackground(Color.WHITE);

        grid.setLayout(new GridLayout(0, 2, 40, 40)); // 2 คอลัม
        grid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ทดสอบ add card
        for (int i = 0; i < 8; i++) {
            grid.add(new Cardport());
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        page.add(scroll, BorderLayout.CENTER);
    }
}
