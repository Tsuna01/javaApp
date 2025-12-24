package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import service.API;
import service.Auth;
import service.Student;
import service.User;
import ui.component.Navbar;

import java.util.ArrayList;

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

    // [New] ประกาศตัวแปร label ยอดเงินไว้ตรงนี้ เพื่อให้ method อื่นเรียกใช้ได้
    private JLabel totalAmountLabel;

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

        // Button Section (Back & Print)
        content.add(createButtonSection(), BorderLayout.NORTH);

        // Payment Statement Content
        contentPanel = createPaymentStatement();
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        content.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
    }

    // ... (createButtonSection code remains the same) ...
    private JPanel createButtonSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JButton backBtn = createRoundedButton("← Back", Color.BLACK, Color.WHITE, false);
        backBtn.addActionListener(e -> {
            new Profile().setVisible(true);
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

    // ========= PAYMENT STATEMENT ==========
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

        statement.add(createPaymentTable()); // สร้างตาราง
        statement.add(Box.createVerticalStrut(20));

        statement.add(createTotalSection()); // สร้างส่วนแสดงผลรวม
        statement.add(Box.createVerticalStrut(30));

        statement.add(createSignatureSection());

        return statement;
    }

    // ... (createTitleSection remains the same) ...
    private JPanel createTitleSection() {
        // (Copy เดิมมาได้เลย หรือใช้ของเดิม)
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

        String username = Auth.getAuthUser().getName();
        String STD = Auth.getAuthUser().getStd_id();
        if (username.equals("admin")) {
            STD = String.valueOf(Auth.getAuthUser().getId());
        } else {
            STD = Auth.getAuthUser().getStd_id();
        }

        JLabel id = new JLabel(username);
        id.setFont(FONT_INFO);
        id.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel idNumber = new JLabel(STD);
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

    // ========= PAYMENT TABLE ==========
    private JPanel createPaymentTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        String[] columns = { "", "วันที่", "รายการงาน (Description)", "ชม.", "จำนวนเงิน (บาท)" };

        // ดึงข้อมูลจากฐานข้อมูลโดยใช้ generateExportData pattern
        Object[][] data = loadExportDataFromDB();

        DefaultTableModel model = new DefaultTableModel(data, columns) {
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

        // [New] เพิ่ม Listener เพื่อดักจับการเปลี่ยนแปลงค่าในตาราง (Checkbox)
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // ถ้ามีการอัพเดทค่า (e.getType() == TableModelEvent.UPDATE)
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

        // Styling headers and columns...
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

    /**
     * ดึงข้อมูลสำหรับ Export จากฐานข้อมูล โดยใช้ generateExportData pattern
     */
    private Object[][] loadExportDataFromDB() {
        // ดึง std_id ของ user ที่ login อยู่
        String stdId = null;
        User currentUser = Auth.getAuthUser();

        if (currentUser instanceof Student) {
            stdId = ((Student) currentUser).getStdId();
        } else if (currentUser != null) {
            stdId = currentUser.getStd_id();
        }

        // ถ้าไม่มี std_id ให้ return empty array
        if (stdId == null || stdId.isEmpty()) {
            return new Object[][] {
                    { false, "-", "ไม่พบข้อมูล (กรุณา Login ด้วยบัญชีนักศึกษา)", "-", "0" }
            };
        }

        // ดึงข้อมูลจาก API
        ArrayList<String[]> assignments = API.getCompletedAssignmentsForExport(stdId);

        if (assignments == null || assignments.isEmpty()) {
            return new Object[][] {
                    { false, "-", "ยังไม่มีงานที่เสร็จสิ้น", "0", "0" }
            };
        }

        // แปลง ArrayList<String[]> เป็น Object[][]
        Object[][] data = new Object[assignments.size()][5];
        for (int i = 0; i < assignments.size(); i++) {
            String[] row = assignments.get(i);
            data[i][0] = false; // Checkbox
            data[i][1] = row[0]; // วันที่
            data[i][2] = row[1]; // ชื่องาน
            data[i][3] = row[2]; // ชั่วโมง
            data[i][4] = row[3]; // จำนวนเงิน
        }

        return data;
    }

    // [New] ฟังก์ชันคำนวณเงินรวม
    private void calculateTotalAmount(DefaultTableModel model) {
        int total = 0;
        // วนลูปทุกแถว
        for (int i = 0; i < model.getRowCount(); i++) {
            // ดึงค่า Checkbox (Column 0)
            Boolean isChecked = (Boolean) model.getValueAt(i, 0);

            if (isChecked != null && isChecked) {
                // ถ้าติ๊กถูก ให้ดึงค่าเงิน (Column 4) มาบวก
                try {
                    String amountStr = (String) model.getValueAt(i, 4);
                    // ลบช่องว่างหรือตัวอักษรที่ไม่ใช่ตัวเลขออก (ถ้ามี) แล้วแปลงเป็น int
                    int amount = Integer.parseInt(amountStr.trim());
                    total += amount;
                } catch (NumberFormatException ex) {
                    System.err.println("Error parsing amount at row " + i);
                }
            }
        }

        // อัพเดท Label ยอดรวม
        if (totalAmountLabel != null) {
            totalAmountLabel.setText(String.valueOf(total));
        }
    }

    // ========= TOTAL SECTION ==========
    private JPanel createTotalSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel totalLabel = new JLabel("รวมเงินเงินทั้งสิ้น (Total Amount)");
        totalLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        // [Modified] ใช้ตัวแปร Global แทน และตั้งค่าเริ่มต้นเป็น 0
        totalAmountLabel = new JLabel("0");
        totalAmountLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        panel.add(totalLabel);
        panel.add(totalAmountLabel);

        return panel;
    }

    // ... (createSignatureSection, createRoundedButton, printPaymentStatement code
    // remains same) ...
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
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0)
                    return NO_SUCH_PAGE;
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                double scaleX = pageFormat.getImageableWidth() / contentPanel.getWidth();
                double scaleY = pageFormat.getImageableHeight() / contentPanel.getHeight();
                double scale = Math.min(scaleX, scaleY);
                g2d.scale(scale, scale);
                contentPanel.printAll(graphics);
                return PAGE_EXISTS;
            }
        });
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "พิมพ์เอกสารสำเร็จ!", "Print Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดในการพิมพ์: " + e.getMessage(), "Print Error",
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