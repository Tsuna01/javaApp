package ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Editperson extends JDialog {

    private static final Color BG_COLOR = new Color(240, 240, 240);
    private static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 18);
    private static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 13);

    private JPanel participantListPanel;
    private List<ParticipantRow> participantRows;

    public Editperson(Frame parent) {
        super(parent, "Edit Participants", true);
        participantRows = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setSize(680, 500);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // Main panel with rounded corners
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Header with gradient
        mainPanel.add(createHeader(), BorderLayout.NORTH);

        // Content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Column headers
        contentPanel.add(createColumnHeaders(), BorderLayout.NORTH);

        // Scrollable participant list
        participantListPanel = new JPanel();
        participantListPanel.setLayout(new BoxLayout(participantListPanel, BoxLayout.Y_AXIS));
        participantListPanel.setBackground(Color.WHITE);

        // Add sample participants
        addParticipantRow("S33550336", "Elysia Athome");
        addParticipantRow("S33550336", "Elysia Athome");
        addParticipantRow("S33550336", "Elysia Athome");

        JScrollPane scrollPane = new JScrollPane(participantListPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(600, 200));

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Add new section
        contentPanel.add(createAddNewSection(), BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Wrap in a panel with background for rounded effect
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(0, 0, 0, 0));
        wrapper.add(mainPanel);

        setContentPane(wrapper);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 130, 140),
                        getWidth(), 0, new Color(255, 210, 160));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

                // Fill bottom to make it square
                g2.fillRect(0, getHeight() - 20, getWidth(), 20);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(680, 70));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel title = new JLabel("แก้ไขรายชื่อผู้รับงาน(Edit Participants)");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        // Close button
        JButton closeBtn = createCloseButton();

        header.add(title, BorderLayout.WEST);
        header.add(closeBtn, BorderLayout.EAST);

        return header;
    }

    private JButton createCloseButton() {
        JButton btn = new JButton("✕") {
            @Override
            protected void paintComponent(Graphics g) {
                // Don't paint background, just text
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 24));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(255, 255, 255, 200));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });

        btn.addActionListener(e -> dispose());

        return btn;
    }

    private JPanel createColumnHeaders() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 5, 0, 5);

        // ID column
        gbc.gridx = 0;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel idLabel = new JLabel("ID");
        idLabel.setFont(FONT_HEADER);
        headerPanel.add(idLabel, gbc);

        // Name column with underline
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        JPanel namePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(100, 150, 255));
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);
            }
        };
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel("Name", SwingConstants.CENTER);
        nameLabel.setFont(FONT_HEADER);
        nameLabel.setForeground(new Color(100, 150, 255));
        namePanel.add(nameLabel);
        headerPanel.add(namePanel, gbc);

        // Action column
        gbc.gridx = 2;
        gbc.weightx = 0.15;
        JLabel actionLabel = new JLabel("Action");
        actionLabel.setFont(FONT_HEADER);
        headerPanel.add(actionLabel, gbc);

        return headerPanel;
    }

    private void addParticipantRow(String id, String name) {
        ParticipantRow row = new ParticipantRow(id, name);
        participantRows.add(row);
        participantListPanel.add(row.getPanel());
        participantListPanel.revalidate();
        participantListPanel.repaint();
    }

    private JPanel createAddNewSection() {
        JPanel addSection = new JPanel(new BorderLayout());
        addSection.setBackground(Color.WHITE);
        addSection.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(20, 0, 0, 0),
                BorderFactory.createDashedBorder(new Color(150, 150, 150), 2, 5, 5, false)));

        JPanel innerPanel = new JPanel(new GridBagLayout());
        innerPanel.setBackground(Color.WHITE);
        innerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 3;
        JLabel addLabel = new JLabel("เพิ่มผู้เข้าร่วมใหม่(Add New)");
        addLabel.setFont(FONT_NORMAL);
        addLabel.setForeground(new Color(100, 100, 100));
        innerPanel.add(addLabel, gbc);

        // Input fields row
        gbc.gridy = 1;
        gbc.gridwidth = 1;

        // ID input
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JTextField idField = createStyledTextField("ID : B67xxxxx");
        innerPanel.add(idField, gbc);

        // Name input
        gbc.gridx = 1;
        gbc.weightx = 0.45;
        JTextField nameField = createStyledTextField("Name");
        innerPanel.add(nameField, gbc);

        // Add button
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        JButton addBtn = createAddButton();
        addBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            if (!id.isEmpty() && !name.isEmpty() && !id.equals("ID : B67xxxxx") && !name.equals("Name")) {
                addParticipantRow(id, name);
                idField.setText("ID : B67xxxxx");
                nameField.setText("Name");
            }
        });
        innerPanel.add(addBtn, gbc);

        addSection.add(innerPanel);
        return addSection;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));

                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 25, 25));

                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(FONT_NORMAL);
        field.setForeground(new Color(150, 150, 150));
        field.setPreferredSize(new Dimension(150, 40));
        field.setBorder(new EmptyBorder(5, 15, 5, 15));
        field.setOpaque(false);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 150, 150));
                }
            }
        });

        return field;
    }

    private JButton createAddButton() {
        JButton btn = new JButton("+ Add") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Green background
                g2.setColor(new Color(76, 175, 80));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.repaint();
            }
        });

        return btn;
    }

    // Inner class for participant rows
    private class ParticipantRow {
        private JPanel panel;
        private String id;
        private JTextField nameField;

        public ParticipantRow(String id, String name) {
            this.id = id;
            createPanel(name);
        }

        private void createPanel(String name) {
            panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(8, 5, 8, 5));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 5, 0, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Avatar + ID
            gbc.gridx = 0;
            gbc.weightx = 0.25;
            JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            idPanel.setBackground(Color.WHITE);

            JLabel avatar = new JLabel(new ImageIcon(createAvatarImage(40)));
            JLabel idLabel = new JLabel("ID : " + id);
            idLabel.setFont(FONT_NORMAL);

            idPanel.add(avatar);
            idPanel.add(idLabel);
            panel.add(idPanel, gbc);

            // Name field
            gbc.gridx = 1;
            gbc.weightx = 0.5;
            nameField = createNameField(name);
            panel.add(nameField, gbc);

            // Delete button
            gbc.gridx = 2;
            gbc.weightx = 0.15;
            gbc.fill = GridBagConstraints.NONE;
            JButton deleteBtn = createDeleteButton();
            deleteBtn.addActionListener(e -> {
                participantListPanel.remove(panel);
                participantRows.remove(this);
                participantListPanel.revalidate();
                participantListPanel.repaint();
            });
            panel.add(deleteBtn, gbc);
        }

        private JTextField createNameField(String name) {
            JTextField field = new JTextField(name) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));

                    g2.setColor(new Color(200, 200, 200));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 25, 25));

                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            field.setFont(FONT_NORMAL);
            field.setPreferredSize(new Dimension(250, 40));
            field.setBorder(new EmptyBorder(5, 15, 5, 15));
            field.setOpaque(false);
            return field;
        }

        private JButton createDeleteButton() {
            JButton btn = new JButton("🗑") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(new Color(244, 67, 54));
                    g2.fillOval(5, 5, getWidth() - 10, getHeight() - 10);

                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btn.setForeground(Color.WHITE);
            btn.setPreferredSize(new Dimension(45, 45));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return btn;
        }

        public JPanel getPanel() {
            return panel;
        }
    }

    private Image createAvatarImage(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pink gradient circle
        GradientPaint gp = new GradientPaint(0, 0, new Color(255, 182, 193), size, size, new Color(255, 105, 180));
        g2.setPaint(gp);
        g2.fillOval(0, 0, size, size);

        g2.dispose();
        return img;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Editperson dialog = new Editperson(null);
            dialog.setVisible(true);
        });
    }
}
