package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import service.Auth;
import service.DatabaseConnection;
import ui.component.Navbar;

public class ExportF extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);

    // Fonts
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_TABLE_HEADER = new Font("Tahoma", Font.BOLD, 12);
    private static final Font FONT_TABLE_CONTENT = new Font("Tahoma", Font.PLAIN, 12);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_INFO = new Font("Tahoma", Font.PLAIN, 12);

    private JPanel contentPanel;
    private JLabel totalAmountLabel;
    private DefaultTableModel model;

    public ExportF() {
        initialize();
    }

    private void initialize() {
        setTitle("Payment Statement - Export");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        add(mainPanel, BorderLayout.CENTER);

        // ========= HEADER ==========
        mainPanel.add(new Navbar().build(), BorderLayout.NORTH);

        // ========= CONTENT ==========
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 50, 20, 50));

        content.add(createButtonSection(), BorderLayout.NORTH);

        contentPanel = createPaymentStatement();

        // โหลดข้อมูล
        loadDataFromDatabase();

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        content.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
    }

    // ========= [ส่วนที่แก้ไข] เพิ่มการกรอง job_type != 'volunteer' ==========
    private void loadDataFromDatabase() {
        if (Auth.getAuthUser() == null)
            return;

        String stdId = Auth.getAuthUser().getStd_id();

        if (model == null)
            return;
        model.setRowCount(0);

        // SQL: เพิ่มเงื่อนไข AND j.job_type != 'volunteer'
        String sql = "SELECT ja.finished_at, j.title, ja.hours_amount, ja.reward_amount " +
                "FROM job_assignment ja " +
                "JOIN job j ON ja.job_id = j.job_id " +
                "WHERE ja.std_id = ? " +
                "AND ja.status = 'complete' " +
                "AND j.job_type != 'volunteer'"; // <-- กรองงานจิตอาสาออก

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, stdId);
            ResultSet rs = pstmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");

            while (rs.next()) {
                Boolean checked = false;
                java.sql.Timestamp ts = rs.getTimestamp("finished_at");
                String dateStr = (ts != null) ? dateFormat.format(ts) : "-";
                String jobName = rs.getString("title");
                String hours = String.valueOf(rs.getInt("hours_amount"));

                double reward = rs.getDouble("reward_amount");
                String amountStr;
                if (rs.wasNull()) {
                    amountStr = "0";
                } else {
                    amountStr = String.valueOf((int) reward);
                }

                model.addRow(new Object[] { checked, dateStr, jobName, hours, amountStr });
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private JPanel createButtonSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JButton backBtn = createRoundedButton("← Back", Color.BLACK, Color.WHITE, false);
        backBtn.addActionListener(e -> {
            dispose();
        });

        JButton printBtn = createRoundedButton("🖨 Print", Color.WHITE, Color.BLACK, true);
        printBtn.addActionListener(e -> printPaymentStatement());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(backBtn);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(printBtn);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createPaymentStatement() {
        JPanel statement = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 20, 20);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, 20, 20);
            }
        };
        statement.setLayout(new BoxLayout(statement, BoxLayout.Y_AXIS));
        statement.setOpaque(false);
        statement.setBorder(new EmptyBorder(30, 40, 30, 40));

        statement.add(createTitleSection());
        statement.add(Box.createVerticalStrut(20));
        statement.add(createPaymentTable());
        statement.add(Box.createVerticalStrut(20));
        statement.add(createTotalSection());
        statement.add(Box.createVerticalStrut(30));
        statement.add(createSignatureSection());

        return statement;
    }

    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel title = new JLabel("หลักฐานการรับเงิน");
        title.setFont(FONT_TITLE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Payment Statement");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(title);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(subtitle);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JLabel payee = new JLabel("ผู้รับเงิน (Payee)");
        payee.setFont(FONT_INFO);
        payee.setAlignmentX(Component.RIGHT_ALIGNMENT);

        String username = "Unknown";
        String stdIdShow = "-";

        if (Auth.getAuthUser() != null) {
            username = Auth.getAuthUser().getName();
            if ("admin".equals(username)) {
                stdIdShow = String.valueOf(Auth.getAuthUser().getId());
            } else {
                stdIdShow = Auth.getAuthUser().getStd_id();
            }
        }

        JLabel id = new JLabel(username);
        id.setFont(FONT_INFO);
        id.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel idNumber = new JLabel(stdIdShow);
        idNumber.setFont(FONT_INFO);
        idNumber.setAlignmentX(Component.RIGHT_ALIGNMENT);

        java.time.LocalDate today = java.time.LocalDate.now();
        int d = today.getDayOfMonth();
        int m = today.getMonthValue();
        int y = today.getYear() + 543;

        JLabel date = new JLabel("วันที่: " + d + "/" + m + "/" + y);
        date.setFont(FONT_INFO);
        date.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(payee);
        rightPanel.add(Box.createVerticalStrut(3));
        rightPanel.add(id);
        rightPanel.add(Box.createVerticalStrut(3));
        rightPanel.add(idNumber);
        rightPanel.add(Box.createVerticalStrut(3));
        rightPanel.add(date);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createPaymentTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        String[] columns = { "", "วันที่", "รายการงาน (Description)", "ชม.", "จำนวนเงิน (บาท)" };
        Object[][] data = {};

        model = new DefaultTableModel(data, columns) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0)
                    return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                calculateTotalAmount(model);
            }
        });

        JTable table = new JTable(model);
        table.setFont(FONT_TABLE_CONTENT);
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 240, 245));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void calculateTotalAmount(DefaultTableModel model) {
        int total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isChecked = (Boolean) model.getValueAt(i, 0);
            if (isChecked != null && isChecked) {
                try {
                    String amountStr = (String) model.getValueAt(i, 4);
                    int amount = Integer.parseInt(amountStr.trim());
                    total += amount;
                } catch (NumberFormatException ex) {
                }
            }
        }
        if (totalAmountLabel != null) {
            totalAmountLabel.setText(String.valueOf(total));
        }
    }

    private JPanel createTotalSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel totalLabel = new JLabel("รวมเงินเงินทั้งสิ้น (Total Amount)");
        totalLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        totalAmountLabel = new JLabel("0");
        totalAmountLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        panel.add(totalLabel);
        panel.add(totalAmountLabel);

        return panel;
    }

    private JPanel createSignatureSection() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 50, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel payerPanel = new JPanel();
        payerPanel.setLayout(new BoxLayout(payerPanel, BoxLayout.Y_AXIS));
        payerPanel.setOpaque(false);
        JLabel payerLabel = new JLabel("ลงชื่อผู้รับเงิน (Payer)");
        payerLabel.setFont(FONT_INFO);
        payerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel payerLine = new JLabel("(...................................................)");
        payerLine.setFont(FONT_INFO);
        payerLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        payerPanel.add(Box.createVerticalStrut(10));
        payerPanel.add(payerLabel);
        payerPanel.add(Box.createVerticalStrut(5));
        payerPanel.add(payerLine);

        JPanel payeePanel = new JPanel();
        payeePanel.setLayout(new BoxLayout(payeePanel, BoxLayout.Y_AXIS));
        payeePanel.setOpaque(false);
        JLabel payeeLabel = new JLabel("ลงชื่อผู้จัดกิจกรรม (Payee)");
        payeeLabel.setFont(FONT_INFO);
        payeeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel payeeLine = new JLabel("(...................................................)");
        payeeLine.setFont(FONT_INFO);
        payeeLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        payeePanel.add(Box.createVerticalStrut(10));
        payeePanel.add(payeeLabel);
        payeePanel.add(Box.createVerticalStrut(5));
        payeePanel.add(payeeLine);

        panel.add(payerPanel);
        panel.add(payeePanel);
        return panel;
    }

    private JButton createRoundedButton(String text, Color fgColor, Color bgColor, boolean filled) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (filled) {
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                } else {
                    g2.setColor(bgColor);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                    g2.setColor(fgColor);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                }
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(fgColor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void printPaymentStatement() {
        // ตรวจสอบว่ามีรายการที่เลือกหรือไม่
        boolean hasSelected = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isChecked = (Boolean) model.getValueAt(i, 0);
            if (isChecked != null && isChecked) {
                hasSelected = true;
                break;
            }
        }

        if (!hasSelected) {
            JOptionPane.showMessageDialog(this, "กรุณาเลือกรายการที่ต้องการ Export อย่างน้อย 1 รายการ", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Render contentPanel เป็นรูปภาพ
        int width = contentPanel.getWidth();
        int height = contentPanel.getHeight();

        if (width <= 0 || height <= 0) {
            width = 900;
            height = 700;
            contentPanel.setSize(width, height);
            contentPanel.doLayout();
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // ตั้งค่า rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // วาด contentPanel ลงบน image
        contentPanel.paint(g2d);
        g2d.dispose();

        // บันทึกไฟล์
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("บันทึกเป็น PNG");
        fileChooser.setSelectedFile(new File("PaymentStatement.png"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".png")) {
                fileToSave = new File(filePath + ".png");
            }

            try {
                ImageIO.write(image, "png", fileToSave);
                JOptionPane.showMessageDialog(this, "บันทึกสำเร็จ!\n" + fileToSave.getAbsolutePath(), "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ExportF frame = new ExportF();
            frame.setVisible(true);
        });
    }
}