package ui;

import ui.component.Cardport;
import ui.component.Navbar;

import util.WrapLayout;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class SectionMain extends JFrame {

    public SectionMain() {
        initialize();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SectionMain s = new SectionMain();
            s.setVisible(true);
        });
    }

    private void initialize() {

        // ===== FRAME =====
        setTitle("Main");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // ===== NAVBAR =====
        JPanel navbar = new Navbar().build();
        add(navbar, BorderLayout.NORTH);

        // ===== CONTENT WRAPPER =====
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(245, 245, 245));
        add(content, BorderLayout.CENTER);

        // ===== TITLE SECTION =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 245, 245));

        titlePanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        JLabel title = new JLabel("Title");
        title.setFont(new Font("Tahoma", Font.BOLD, 18));
        titlePanel.add(title, BorderLayout.WEST);

        content.add(titlePanel, BorderLayout.NORTH);

        // ===== GRID SECTION (4 COLUMNS) =====
        JPanel section = new JPanel();
        section.setBackground(new Color(245, 245, 245));

        // padding ขอบรอบ grid
        section.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Grid 4 คอลัมน์, auto rows, spacing 20px
        section.setLayout(new GridLayout(0, 3, 20, 20));

        // เพิ่ม Cardport ตัวอย่าง
        for (int i = 0; i < 15; i++) {
            section.add(new Cardport());
        }

        // ===== SCROLLPANE เพื่อเลื่อนลง =====
        JScrollPane scroll = new JScrollPane(section);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        content.add(scroll, BorderLayout.CENTER);
    }
}
