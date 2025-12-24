package ui.component;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FilterUI extends JPanel {

    // --- Color Palette ---
    private static final Color ACCENT_PINK = new Color(255, 127, 127);
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BTN_Selected_BG = new Color(255, 240, 240);
    private static final Color BTN_Selected_BORDER = ACCENT_PINK;

    // --- Components ---
    private JCheckBox cbAll, cbVolunteer, cbPaid;
    private JCheckBox cbEnableDate;

    // [กลับมาใช้ JSpinner] ไม่ต้องโหลด Library เพิ่ม
    private JSpinner spinDateFrom, spinDateTo;

    private List<SelectableButton> sortDateButtons = new ArrayList<>();
    private List<SelectableButton> sortHourButtons = new ArrayList<>();

    private boolean isProgrammaticChange = false;

    // [เพิ่ม] เก็บ reference ของ Apply button
    private JButton applyBtn;

    public FilterUI() {
        // [สำคัญ] บังคับ Locale เป็นภาษาอังกฤษ (ค.ศ.) เพื่อแก้ปัญหาปี พ.ศ. (2568)
        Locale.setDefault(Locale.ENGLISH);

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(380, 550));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Date Section
        mainPanel.add(createHeader("Date Filter"));
        mainPanel.add(Box.createVerticalStrut(8));

        // 1.1 Checkbox
        cbEnableDate = createCustomCheckbox("Filter by Date Range");
        // [Default] ไม่ติ๊กวันที่ (ปิดไว้ก่อน)
        cbEnableDate.setSelected(false);
        cbEnableDate.addActionListener(e -> toggleDateSpinners(cbEnableDate.isSelected()));

        mainPanel.add(cbEnableDate);
        mainPanel.add(Box.createVerticalStrut(5));

        // 1.2 Date Spinner Panel
        JPanel dateRow = new JPanel(new GridLayout(1, 2, 15, 0));
        dateRow.setBackground(BG_COLOR);
        dateRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        // สร้าง Date Spinner
        spinDateFrom = createDateSpinner();
        spinDateTo = createDateSpinner();

        // ปิดการใช้งานไว้ก่อน (ตาม Default Checkbox)
        toggleDateSpinners(false);

        dateRow.add(createFieldWrapper("From", spinDateFrom));
        dateRow.add(createFieldWrapper("To", spinDateTo));
        mainPanel.add(dateRow);
        mainPanel.add(Box.createVerticalStrut(20));

        // 2. Job Type Section
        mainPanel.add(createHeader("Job type"));
        mainPanel.add(Box.createVerticalStrut(8));
        JPanel jobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        jobPanel.setBackground(BG_COLOR);

        cbAll = createCustomCheckbox("All");
        cbVolunteer = createCustomCheckbox("Volunteer");
        cbPaid = createCustomCheckbox("Paid");

        // [Default] เลือก Job Type ทั้งหมด
        cbAll.setSelected(true);
        cbVolunteer.setSelected(true);
        cbPaid.setSelected(true);

        setupJobTypeLogic();

        jobPanel.add(cbAll);
        jobPanel.add(cbVolunteer);
        jobPanel.add(cbPaid);

        JPanel jobWrapper = new JPanel(new BorderLayout());
        jobWrapper.setBackground(BG_COLOR);
        jobWrapper.add(jobPanel, BorderLayout.CENTER);
        jobWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        mainPanel.add(jobWrapper);
        mainPanel.add(Box.createVerticalStrut(20));

        // 3. Sort by Date
        mainPanel.add(createHeader("Sort by Date"));
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(createSortGroup(sortDateButtons, "Default", "Oldest", "Newest"));
        mainPanel.add(Box.createVerticalStrut(20));

        // 4. Sort by Hours
        mainPanel.add(createHeader("Sort by Hours"));
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(createSortGroup(sortHourButtons, "Default", "Low -> High", "High -> Low"));
        mainPanel.add(Box.createVerticalStrut(25));

        // --- Footer ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BG_COLOR);
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        footerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel resetLabel = new JLabel("↻ Reset Filters");
        resetLabel.setForeground(Color.GRAY);
        resetLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        resetLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resetFilters();
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                resetLabel.setForeground(ACCENT_PINK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                resetLabel.setForeground(Color.GRAY);
            }
        });

        this.applyBtn = new GradientButton("Apply Filter");
        this.applyBtn.setPreferredSize(new Dimension(150, 45));
        this.applyBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        this.applyBtn.setForeground(Color.WHITE);

        footerPanel.add(resetLabel, BorderLayout.WEST);
        footerPanel.add(this.applyBtn, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        add(scrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // --- Methods ---

    public FilterData getFilterData() {
        String dFrom = "";
        String dTo = "";

        // ถ้าติ๊กถูกให้กรองวันที่ จึงค่อยดึงค่าออกมา
        if (cbEnableDate.isSelected()) {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yy", Locale.ENGLISH);
            dFrom = sdf.format(spinDateFrom.getValue());
            dTo = sdf.format(spinDateTo.getValue());
        }

        boolean isVol = cbVolunteer.isSelected();
        boolean isPaid = cbPaid.isSelected();
        if (cbAll.isSelected()) {
            isVol = true;
            isPaid = true;
        }

        String sDate = getSelectedSort(sortDateButtons);
        String sHour = getSelectedSort(sortHourButtons);

        return new FilterData(dFrom, dTo, isVol, isPaid, sDate, sHour);
    }

    public void setOnApplyListener(ActionListener listener) {
        // [แก้ไข] ใช้ field โดยตรง แทนการหาด้วย getComponent
        if (this.applyBtn != null) {
            this.applyBtn.addActionListener(listener);
        }
    }

    private String getSelectedSort(List<SelectableButton> buttons) {
        for (SelectableButton btn : buttons)
            if (btn.isSelected)
                return btn.getText();
        return "Default";
    }

    private void resetFilters() {
        // [Reset Logic] กลับไปเป็นค่าเริ่มต้นตามที่ขอ
        cbEnableDate.setSelected(false);
        toggleDateSpinners(false);

        spinDateFrom.setValue(new Date());
        spinDateTo.setValue(new Date());

        // Reset Job Type กลับไปเลือก All
        isProgrammaticChange = true;
        cbAll.setSelected(true);
        cbVolunteer.setSelected(true);
        cbPaid.setSelected(true);
        isProgrammaticChange = false;

        resetSortGroup(sortDateButtons);
        resetSortGroup(sortHourButtons);
    }

    private void resetSortGroup(List<SelectableButton> group) {
        for (SelectableButton btn : group)
            btn.setSelected(btn.getText().equals("Default"));
    }

    // --- Helper UI Methods ---

    // [สร้าง JSpinner] แบบมาตรฐาน ไม่ต้องใช้ Library
    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        model.setValue(new Date());

        JSpinner spinner = new JSpinner(model);
        // ตั้งรูปแบบเป็น วัน/เดือน/ปี (เช่น 04/11/25)
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yy");
        spinner.setEditor(editor);

        // ปรับแต่ง UI
        JComponent editorComponent = spinner.getEditor();
        if (editorComponent instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editorComponent).getTextField();
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setForeground(TEXT_COLOR);
            tf.setBackground(Color.WHITE);
        }
        spinner.setBorder(new RoundedBorder(10, Color.LIGHT_GRAY));
        spinner.setPreferredSize(new Dimension(100, 35));

        return spinner;
    }

    private void toggleDateSpinners(boolean enable) {
        spinDateFrom.setEnabled(enable);
        spinDateTo.setEnabled(enable);
    }

    private JPanel createSortGroup(List<SelectableButton> groupList, String... options) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setBackground(BG_COLOR);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        for (int i = 0; i < options.length; i++) {
            boolean isFirst = (i == 0);
            SelectableButton btn = new SelectableButton(options[i], isFirst);
            btn.addActionListener(e -> {
                for (SelectableButton b : groupList)
                    b.setSelected(false);
                btn.setSelected(true);
            });
            groupList.add(btn);
            panel.add(btn);
        }
        return panel;
    }

    private void setupJobTypeLogic() {
        cbAll.addActionListener(e -> {
            if (isProgrammaticChange)
                return;
            isProgrammaticChange = true;
            boolean state = cbAll.isSelected();
            cbVolunteer.setSelected(state);
            cbPaid.setSelected(state);
            isProgrammaticChange = false;
        });
        ActionListener singleCheckListener = e -> {
            if (isProgrammaticChange)
                return;
            isProgrammaticChange = true;
            if (cbVolunteer.isSelected() && cbPaid.isSelected())
                cbAll.setSelected(true);
            else
                cbAll.setSelected(false);
            isProgrammaticChange = false;
        };
        cbVolunteer.addActionListener(singleCheckListener);
        cbPaid.addActionListener(singleCheckListener);
    }

    private JPanel createHeader(String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(BG_COLOR);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(5, 18));
        bar.setBackground(ACCENT_PINK);
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(TEXT_COLOR);
        panel.add(bar);
        panel.add(label);
        return panel;
    }

    private JPanel createFieldWrapper(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(BG_COLOR);
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(Color.GRAY);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JCheckBox createCustomCheckbox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setBackground(BG_COLOR);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setFocusPainted(false);
        cb.setForeground(TEXT_COLOR);
        return cb;
    }

    // --- Custom Components ---

    class SelectableButton extends JButton {
        private boolean isSelected = false;

        public SelectableButton(String text, boolean defaultSelected) {
            super(text);
            this.isSelected = defaultSelected;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("SansSerif", Font.PLAIN, 12));
            setPreferredSize(new Dimension(100, 32));
        }

        public void setSelected(boolean b) {
            this.isSelected = b;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isSelected) {
                g2.setColor(BTN_Selected_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(BTN_Selected_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                setForeground(ACCENT_PINK);
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Color.LIGHT_GRAY);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                setForeground(TEXT_COLOR);
                setFont(getFont().deriveFont(Font.PLAIN));
            }
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class GradientButton extends JButton {
        public GradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c1 = new Color(255, 138, 140);
            Color c2 = new Color(255, 204, 153);
            if (getModel().isRollover())
                c1 = c1.darker();
            GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
            super.paintComponent(g);
            g2.dispose();
        }
    }

    class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 10, 4, 10);
        }
    }

    class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(200, 200, 200);
            this.trackColor = Color.WHITE;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton j = new JButton();
            j.setPreferredSize(new Dimension(0, 0));
            return j;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
            g2.dispose();
        }
    }

    public static class FilterData {
        public String dateFrom, dateTo, sortByDate, sortByHours;
        public boolean isVolunteer, isPaid;

        public FilterData(String df, String dt, boolean v, boolean p, String sd, String sh) {
            this.dateFrom = df;
            this.dateTo = dt;
            this.isVolunteer = v;
            this.isPaid = p;
            this.sortByDate = sd;
            this.sortByHours = sh;
        }

        @Override
        public String toString() {
            return String.format("Date: %s-%s | Vol:%s Paid:%s", dateFrom, dateTo, isVolunteer, isPaid);
        }
    }
}