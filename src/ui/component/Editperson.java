package ui.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import service.WorkerManager;
import ui.Profile;

public class Editperson extends JDialog {

    // ===== Fonts =====
    private static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 18);
    private static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 14);
    private static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 13);

    private JPanel participantListPanel;
    private List<ParticipantRow> participantRows;
    private int currentJobId;

    // Fixed column widths for alignment
    private static final int COL_ID_WIDTH = 130;
    private static final int COL_NAME_WIDTH = 220;
    private static final int COL_VIEW_WIDTH = 90;
    private static final int COL_ACTION_WIDTH = 70;

    public Editperson(Frame parent) {
        this(parent, -1);
    }

    public Editperson(Frame parent, int jobId) {
        super(parent, "Edit Participants", true);
        this.currentJobId = jobId;
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

        // Header
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

        loadParticipantsFromDB();

        JScrollPane scrollPane = new JScrollPane(participantListPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Add new section
        contentPanel.add(createAddNewSection(), BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Wrapper for transparency
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(0, 0, 0, 0));
        wrapper.add(mainPanel);
        setContentPane(wrapper);
    }

    private void loadParticipantsFromDB() {
        participantListPanel.removeAll();
        participantRows.clear();

        if (currentJobId <= 0) {
            JLabel noDataLabel = new JLabel("ไม่มี Job ID - กรุณาเลือกงานก่อน", SwingConstants.CENTER);
            noDataLabel.setFont(FONT_NORMAL);
            noDataLabel.setForeground(new Color(150, 150, 150));
            participantListPanel.add(noDataLabel);
            return;
        }

        ArrayList<WorkerManager> workers = WorkerManager.getJobWorkers(currentJobId);

        if (workers.isEmpty()) {
            JLabel noDataLabel = new JLabel("ยังไม่มีผู้รับงาน / No participants yet", SwingConstants.CENTER);
            noDataLabel.setFont(FONT_NORMAL);
            noDataLabel.setForeground(new Color(150, 150, 150));
            participantListPanel.add(noDataLabel);
        } else {
            for (WorkerManager worker : workers) {
                addParticipantRow(worker.stdId, worker.name);
            }
        }

        participantListPanel.revalidate();
        participantListPanel.repaint();
    }

    // Header with Gradient
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 130, 140),
                        getWidth(), 0, new Color(255, 210, 160));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.fillRect(0, getHeight() - 20, getWidth(), 20); // Square bottom
                g2.dispose();
            }
        };

        header.setPreferredSize(new Dimension(680, 70));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        String titleText = currentJobId > 0
                ? "แก้ไขรายชื่อผู้รับงาน (Job #" + currentJobId + ")"
                : "แก้ไขรายชื่อผู้รับงาน";
        JLabel title = new JLabel(titleText);
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(createCloseButton(), BorderLayout.EAST);
        return header;
    }

    private JButton createCloseButton() {
        JButton btn = new JButton("✕") {
            private boolean isHover = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isHover) {
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillOval(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g2);
                g2.dispose();
            }

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        isHover = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        isHover = false;
                        repaint();
                    }
                });
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 20));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> dispose());
        return btn;
    }

    private JPanel createColumnHeaders() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        // ID Header
        JLabel idLabel = new JLabel("ID", SwingConstants.CENTER);
        idLabel.setFont(FONT_HEADER);
        idLabel.setPreferredSize(new Dimension(COL_ID_WIDTH, 25));
        idLabel.setMinimumSize(new Dimension(COL_ID_WIDTH, 25));
        idLabel.setMaximumSize(new Dimension(COL_ID_WIDTH, 25));
        headerPanel.add(idLabel);

        headerPanel.add(Box.createHorizontalStrut(10));

        // Name Header (with underline)
        JPanel namePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(100, 150, 255));
                g.fillRect(0, getHeight() - 3, getWidth(), 3);
            }
        };
        namePanel.setOpaque(false);
        namePanel.setPreferredSize(new Dimension(COL_NAME_WIDTH, 25));
        namePanel.setMinimumSize(new Dimension(COL_NAME_WIDTH, 25));
        namePanel.setMaximumSize(new Dimension(COL_NAME_WIDTH, 25));
        JLabel nameLabel = new JLabel("Name", SwingConstants.CENTER);
        nameLabel.setFont(FONT_HEADER);
        nameLabel.setForeground(new Color(100, 150, 255));
        namePanel.add(nameLabel, BorderLayout.CENTER);
        headerPanel.add(namePanel);

        headerPanel.add(Box.createHorizontalStrut(10));

        // View Profile Header
        JLabel viewLabel = new JLabel("View Profile", SwingConstants.CENTER);
        viewLabel.setFont(FONT_HEADER);
        viewLabel.setPreferredSize(new Dimension(COL_VIEW_WIDTH, 25));
        viewLabel.setMinimumSize(new Dimension(COL_VIEW_WIDTH, 25));
        viewLabel.setMaximumSize(new Dimension(COL_VIEW_WIDTH, 25));
        headerPanel.add(viewLabel);

        headerPanel.add(Box.createHorizontalStrut(10));

        // Action Header
        JLabel actionLabel = new JLabel("Action", SwingConstants.CENTER);
        actionLabel.setFont(FONT_HEADER);
        actionLabel.setPreferredSize(new Dimension(COL_ACTION_WIDTH, 25));
        actionLabel.setMinimumSize(new Dimension(COL_ACTION_WIDTH, 25));
        actionLabel.setMaximumSize(new Dimension(COL_ACTION_WIDTH, 25));
        headerPanel.add(actionLabel);

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
        JLabel addLabel = new JLabel("เพิ่มผู้เข้าร่วมใหม่ (Add New)");
        addLabel.setFont(FONT_NORMAL);
        addLabel.setForeground(new Color(100, 100, 100));
        innerPanel.add(addLabel, gbc);

        // Inputs
        gbc.gridy = 1;
        gbc.gridwidth = 1;

        // ID Input
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JTextField idField = createStyledTextField("ID : B67xxxxx");
        innerPanel.add(idField, gbc);

        // Name Display
        gbc.gridx = 1;
        gbc.weightx = 0.45;
        JTextField nameField = createStyledTextField("ชื่อจะแสดงอัตโนมัติ");
        nameField.setEditable(false);
        innerPanel.add(nameField, gbc);

        // Add Button
        gbc.gridx = 2;
        gbc.weightx = 0.2;
        JButton addBtn = createAddButton();
        addBtn.addActionListener(e -> {
            String stdId = idField.getText().trim();
            if (stdId.isEmpty() || stdId.equals("ID : B67xxxxx"))
                return;

            // ตรวจสอบว่ามีนักศึกษาคนนี้ในระบบหรือไม่
            if (WorkerManager.isStudentExists(stdId)) {

                // --- 1. ตรวจสอบว่างานเต็มหรือยัง ---
                int vacancies = WorkerManager.getJobVacancies(currentJobId);
                int current = WorkerManager.getCurrentWorkerCount(currentJobId);

                if (current >= vacancies) {
                    JOptionPane.showMessageDialog(this,
                            "งานนี้คนเต็มแล้วครับ (" + current + "/" + vacancies + ")",
                            "Job Full",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // --- 2. ถ้ายังไม่เต็ม ให้ลองเพิ่ม ---
                if (WorkerManager.addWorkerToJob(currentJobId, stdId)) {
                    String name = WorkerManager.getStudentName(stdId);
                    addParticipantRow(stdId, (name != null ? name : "Student " + stdId));
                    idField.setText("ID : B67xxxxx");
                    idField.setForeground(new Color(150, 150, 150));
                    nameField.setText("ชื่อจะแสดงอัตโนมัติ");
                    JOptionPane.showMessageDialog(this, "เพิ่มผู้รับงานสำเร็จ");
                } else {
                    JOptionPane.showMessageDialog(this, "ผู้รับงานนี้มีชื่ออยู่ในงานนี้แล้ว", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "ไม่พบนักศึกษาในระบบ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        innerPanel.add(addBtn, gbc);

        // Focus Logic
        idField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (idField.getText().equals("ID : B67xxxxx")) {
                    idField.setText("");
                    idField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (idField.getText().trim().isEmpty()) {
                    idField.setText("ID : B67xxxxx");
                    idField.setForeground(new Color(150, 150, 150));
                    nameField.setText("ชื่อจะแสดงอัตโนมัติ");
                } else {
                    String name = WorkerManager.getStudentName(idField.getText().trim());
                    nameField.setText(name != null ? name : "ไม่พบนักศึกษา");
                    nameField.setForeground(name != null ? Color.BLACK : Color.RED);
                }
            }
        });

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
        return field;
    }

    // --- BUTTON: Add (Green Gradient + Shadow) ---
    private JButton createAddButton() {
        JButton btn = new JButton("+ Add") {
            private boolean isHover = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(3, 3, w - 6, h - 6, 20, 20);

                // Gradient
                Color c1 = isHover ? new Color(102, 187, 106) : new Color(76, 175, 80);
                Color c2 = isHover ? new Color(67, 160, 71) : new Color(56, 142, 60);
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, h, c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w - 3, h - 3, 20, 20);

                // Text
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - 3 - fm.stringWidth(getText())) / 2;
                int ty = ((h - 3 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        isHover = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent e) {
                        isHover = false;
                        repaint();
                    }
                });
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- BUTTON: View (Eye Icon) ---
    private JButton createViewButton(String stdId) {
        JButton btn = new JButton();
        btn.setToolTipText("View Profile");

        // Add ActionListener to open Profile
        btn.addActionListener(e -> {
            // Hide this Editperson dialog
            setVisible(false);

            // Open Profile in minimal mode (no navbar, no buttons)
            Profile profileFrame = new Profile(stdId, true);

            // When Profile is closed, reopen this Editperson dialog
            profileFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent evt) {
                    setVisible(true);
                }
            });

            profileFrame.setVisible(true);
        });

        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;

                if (b.getModel().isRollover())
                    g2.setColor(new Color(225, 235, 250));
                else
                    g2.setColor(new Color(245, 245, 245));
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 15, 15);

                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, b.getWidth() - 1, b.getHeight() - 1, 15, 15);

                // Eye Icon
                g2.setColor(new Color(100, 150, 255));
                g2.setStroke(new BasicStroke(2f));
                int cx = b.getWidth() / 2, cy = b.getHeight() / 2;
                Path2D path = new Path2D.Double();
                path.moveTo(cx - 10, cy);
                path.curveTo(cx - 5, cy - 7, cx + 5, cy - 7, cx + 10, cy);
                path.curveTo(cx + 5, cy + 7, cx - 5, cy + 7, cx - 10, cy);
                g2.draw(path);
                g2.fillOval(cx - 3, cy - 3, 6, 6); // Pupil
                g2.dispose();
            }
        });
        btn.setPreferredSize(new Dimension(45, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // --- BUTTON: Delete (Trash Icon) ---
    private JButton createDeleteButton() {
        JButton btn = new JButton();
        btn.setToolTipText("Remove Worker");

        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;

                // Red Gradient
                Color c1 = b.getModel().isRollover() ? new Color(255, 82, 82) : new Color(239, 83, 80);
                Color c2 = b.getModel().isRollover() ? new Color(255, 23, 68) : new Color(211, 47, 47);
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, b.getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(2, 2, b.getWidth() - 4, b.getHeight() - 4, 15, 15);

                // Trash Icon
                g2.setColor(Color.WHITE);
                int cx = b.getWidth() / 2, cy = b.getHeight() / 2;
                g2.fillRect(cx - 5, cy - 4, 10, 12); // Body
                g2.fillRect(cx - 7, cy - 7, 14, 2); // Lid
                g2.fillRect(cx - 2, cy - 9, 4, 2); // Handle
                // Lines
                g2.setColor(new Color(255, 255, 255, 150));
                g2.fillRect(cx - 2, cy - 2, 1, 8);
                g2.fillRect(cx + 1, cy - 2, 1, 8);
                g2.dispose();
            }
        });
        btn.setPreferredSize(new Dimension(45, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private class ParticipantRow {
        private JPanel panel;
        private String id;
        private JTextField nameField;

        public ParticipantRow(String id, String name) {
            this.id = id;
            createPanel(name);
        }

        private void createPanel(String name) {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(8, 15, 8, 15));
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

            // Avatar + ID
            JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            idPanel.setBackground(Color.WHITE);
            idPanel.setPreferredSize(new Dimension(COL_ID_WIDTH, 40));
            idPanel.setMinimumSize(new Dimension(COL_ID_WIDTH, 40));
            idPanel.setMaximumSize(new Dimension(COL_ID_WIDTH, 40));
            JLabel avatar = new JLabel(new ImageIcon(createAvatarImage(30)));
            JLabel idLabel = new JLabel(id);
            idLabel.setFont(FONT_NORMAL);
            idPanel.add(avatar);
            idPanel.add(idLabel);
            panel.add(idPanel);

            panel.add(Box.createHorizontalStrut(10));

            // Name
            nameField = new JTextField(name) {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                    g2.setColor(new Color(200, 200, 200));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            nameField.setFont(FONT_NORMAL);
            nameField.setHorizontalAlignment(JTextField.CENTER);
            nameField.setPreferredSize(new Dimension(COL_NAME_WIDTH, 36));
            nameField.setMinimumSize(new Dimension(COL_NAME_WIDTH, 36));
            nameField.setMaximumSize(new Dimension(COL_NAME_WIDTH, 36));
            nameField.setBorder(new EmptyBorder(5, 10, 5, 10));
            nameField.setOpaque(false);
            nameField.setEditable(false);
            panel.add(nameField);

            panel.add(Box.createHorizontalStrut(10));

            // View Btn
            JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            viewPanel.setBackground(Color.WHITE);
            viewPanel.setPreferredSize(new Dimension(COL_VIEW_WIDTH, 40));
            viewPanel.setMinimumSize(new Dimension(COL_VIEW_WIDTH, 40));
            viewPanel.setMaximumSize(new Dimension(COL_VIEW_WIDTH, 40));
            viewPanel.add(createViewButton(id));
            panel.add(viewPanel);

            panel.add(Box.createHorizontalStrut(10));

            // Delete Btn
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            actionPanel.setBackground(Color.WHITE);
            actionPanel.setPreferredSize(new Dimension(COL_ACTION_WIDTH, 40));
            actionPanel.setMinimumSize(new Dimension(COL_ACTION_WIDTH, 40));
            actionPanel.setMaximumSize(new Dimension(COL_ACTION_WIDTH, 40));
            JButton deleteBtn = createDeleteButton();
            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(Editperson.this, "ลบ " + name + "?", "ยืนยัน",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (WorkerManager.removeWorkerFromJob(currentJobId, id)) {
                        participantListPanel.remove(panel);
                        participantRows.remove(this);
                        participantListPanel.revalidate();
                        participantListPanel.repaint();


                    } else {
                        JOptionPane.showMessageDialog(Editperson.this, "ลบไม่สำเร็จ", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            actionPanel.add(deleteBtn);
            panel.add(actionPanel);
        }

        public JPanel getPanel() {
            return panel;
        }
    }

    private Image createAvatarImage(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, new Color(255, 182, 193), size, size, new Color(255, 105, 180));
        g2.setPaint(gp);
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return img;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Editperson(null, 1).setVisible(true);
        });
    }
}