package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat; // [เพิ่ม] สำหรับจัดการวันที่
import java.util.Date; // [เพิ่ม]
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

import model.JobEntity;
import service.JobManager;
import ui.component.Navbar;

public class AddEvent extends JFrame {

    // Colors & Fonts
    private static final Color BG_COLOR = new Color(245, 247, 250);
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219); // Blue
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    // Components
    private JLabel imageLabel;
    private JTextField titleField, vacanciesField, workingHoursField, paymentField;
    private JTextField timeField; // [แก้ไข 1] เปลี่ยนจาก JSpinner เป็น JTextField
    private JSpinner dateSpinner;
    private JTextArea locationField, detailsArea;
    private JLabel previewText;
    private JLabel previewImage;
    private File selectedImageFile;
    private JRadioButton yesButton, noButton;
    private JLabel lblBaht;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                AddEvent window = new AddEvent();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public AddEvent() {
        initialize();
        setupLivePreview();
    }

    private void initialize() {
        setTitle("Add New Event");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔹 Navbar
        JPanel navbar = new Navbar().build();
        add(navbar, BorderLayout.NORTH);

        // 🔹 Main Content
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(BG_COLOR);
        mainContent.setBorder(new EmptyBorder(20, 40, 20, 40));

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.BOTH;

        // --- Left Panel ---
        JPanel formPanel = createFormPanel();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.6; gbc.weighty = 1.0;
        mainContent.add(formPanel, gbc);

        // --- Right Panel ---
        JPanel sidePanel = createSidePanel();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4; gbc.weighty = 1.0;
        mainContent.add(sidePanel, gbc);

        // --- Bottom Panel ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setBackground(BG_COLOR);

        JButton btnClear = createStyledButton("Clear", new Color(149, 165, 166), Color.WHITE);
        JButton btnPost = createStyledButton("Post Event", PRIMARY_COLOR, Color.WHITE);

        // [Logic ปุ่ม Post]
        btnPost.addActionListener(e -> {
            JobManager jobManager = new JobManager();
            JobEntity newJob = new JobEntity();

            newJob.setTitle(titleField.getText());
            newJob.setDetails(detailsArea.getText());
            newJob.setLocation(locationField.getText());

            try {
                newJob.setVacancies(Integer.parseInt(vacanciesField.getText()));
                newJob.setWorkingHours(Integer.parseInt(workingHoursField.getText()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกตัวเลขในช่องจำนวนคนและชั่วโมงทำงาน");
                return;
            }

            // [แก้ไข 2] รวมวันที่และเวลาเข้าด้วยกันเพื่อบันทึก
            try {
                // ดึงวันที่จาก Spinner
                Date date = (Date) dateSpinner.getValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = dateFormat.format(date);

                // ดึงเวลาจาก TextField
                String timeStr = timeField.getText().trim();
                if (timeStr.isEmpty()) timeStr = "00:00:00"; // กันค่าว่าง
                // ถ้ากรอกมาแค่ HH:mm เติม :00 ให้ครบ format SQL
                if (timeStr.length() == 5) timeStr += ":00";

                // รวมร่าง
                newJob.setDateTime(dateStr + " " + timeStr);

            } catch (Exception ex) {
                newJob.setDateTime("2025-01-01 00:00:00"); // Default case error
            }

            // จัดการรูปภาพ
            if (selectedImageFile != null) {
                // TODO: ต้องเขียน Method saveImageToLocal เหมือนในหน้า ProfileEditor
                // ตอนนี้ Hardcode ไว้ก่อนเพื่อทดสอบ
                newJob.setImagePath("user_images/job_test.jpg");
            } else {
                newJob.setImagePath(null);
            }

            newJob.setJobType(noButton.isSelected() ? "volunteer" : "paid");
            newJob.setUserId(service.Auth.getAuthUser().getId());

            int hourRate = 0;
            if (yesButton.isSelected()) {
                try {
                    hourRate = Integer.parseInt(paymentField.getText());
                } catch (NumberFormatException ex) {
                    hourRate = 0; // หรือแจ้งเตือน error ก็ได้
                }
            }

            boolean success = jobManager.addJob(newJob, hourRate);

            if (success) {
                JOptionPane.showMessageDialog(this, "โพสต์งานสำเร็จ!");
            } else {
                JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดในการบันทึก");
            }
        });

        btnClear.addActionListener(e -> clearForm());

        actionPanel.add(btnClear);
        actionPanel.add(btnPost);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Header
        JLabel header = new JLabel("Event Details");
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(header, gbc);

        gbc.gridwidth = 1;
        int row = 1;

        // Title
        addLabelAndInput(panel, "Title :", titleField = createTextField(), gbc, row++);

        // Vacancies
        vacanciesField = createTextField();
        ((AbstractDocument) vacanciesField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        addLabelAndInput(panel, "Vacancies :", vacanciesField, gbc, row++);

        // Date & Time
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Date & Time :"), gbc);

        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dateTimePanel.setOpaque(false);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        dateSpinner.setPreferredSize(new Dimension(120, 35));
        dateSpinner.setFont(INPUT_FONT);

        // [แก้ไข 3] สร้าง TextField แทน Spinner
        timeField = createTextField();
        timeField.setPreferredSize(new Dimension(80, 35));
        // ใส่ Placeholder หรือ Text เริ่มต้นได้ถ้าต้องการ
        // timeField.setText("00:00");

        dateTimePanel.add(dateSpinner);
        dateTimePanel.add(Box.createHorizontalStrut(10));
        dateTimePanel.add(timeField); // Add Field

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(dateTimePanel, gbc);
        row++;

        // Location
        locationField = new JTextArea(3, 20);
        styleTextArea(locationField);
        JScrollPane locScroll = new JScrollPane(locationField);
        addLabelAndComponent(panel, "Location :", locScroll, gbc, row++);

        // Working Hours
        workingHoursField = createTextField();
        ((AbstractDocument) workingHoursField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        addLabelAndInput(panel, "Working Hours :", workingHoursField, gbc, row++);

        // Payment
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel("Payment :"), gbc);

        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        paymentPanel.setOpaque(false);

        yesButton = new JRadioButton("Yes");
        noButton = new JRadioButton("No");
        styleRadioButton(yesButton);
        styleRadioButton(noButton);

        ButtonGroup group = new ButtonGroup();
        group.add(yesButton);
        group.add(noButton);
        noButton.setSelected(true);

        paymentField = createTextField();
        paymentField.setPreferredSize(new Dimension(100, 35));
        paymentField.setEnabled(false);
        paymentField.setVisible(false);
        ((AbstractDocument) paymentField.getDocument()).setDocumentFilter(new NumericDocumentFilter());

        lblBaht = new JLabel("Baht/Hour");
        lblBaht.setFont(INPUT_FONT);
        lblBaht.setVisible(false);

        paymentPanel.add(noButton);
        paymentPanel.add(yesButton);
        paymentPanel.add(paymentField);
        paymentPanel.add(lblBaht);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(paymentPanel, gbc);

        yesButton.addActionListener(e -> togglePayment(true));
        noButton.addActionListener(e -> togglePayment(false));
        row++;

        // Details
        detailsArea = new JTextArea(5, 20);
        styleTextArea(detailsArea);
        JScrollPane detailScroll = new JScrollPane(detailsArea);
        addLabelAndComponent(panel, "Details :", detailScroll, gbc, row++);

        return panel;
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Image Upload ---
        JPanel uploadPanel = new JPanel(new BorderLayout(0, 10));
        uploadPanel.setOpaque(false);
        uploadPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblImage = new JLabel("Event Image");
        lblImage.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        imageLabel = new JLabel("No Image");
        imageLabel.setPreferredSize(new Dimension(150, 150));
        imageLabel.setMaximumSize(new Dimension(150, 150));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createDashedBorder(Color.GRAY, 2, 5, 3, true));
        imageLabel.setBackground(Color.WHITE);
        imageLabel.setOpaque(true);

        JButton btnImport = createStyledButton("Upload Image", new Color(52, 73, 94), Color.WHITE);
        btnImport.addActionListener(e -> chooseImage());

        JPanel imgWrapper = new JPanel();
        imgWrapper.setOpaque(false);
        imgWrapper.add(imageLabel);

        uploadPanel.add(lblImage, BorderLayout.NORTH);
        uploadPanel.add(imgWrapper, BorderLayout.CENTER);
        uploadPanel.add(btnImport, BorderLayout.SOUTH);

        // --- Live Preview ---
        JLabel lblPreview = new JLabel("Live Preview");
        lblPreview.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblPreview.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel previewCard = new JPanel(new BorderLayout());
        previewCard.setBackground(Color.WHITE);
        previewCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        previewCard.setMaximumSize(new Dimension(350, 400));
        previewCard.setPreferredSize(new Dimension(300, 250));

        previewImage = new JLabel();
        previewImage.setPreferredSize(new Dimension(100, 100));
        previewImage.setHorizontalAlignment(SwingConstants.CENTER);
        previewImage.setBorder(new LineBorder(Color.LIGHT_GRAY));

        JPanel pImgPanel = new JPanel(new BorderLayout());
        pImgPanel.setOpaque(false);
        pImgPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
        pImgPanel.add(previewImage, BorderLayout.NORTH);

        previewText = new JLabel();
        previewText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        previewText.setVerticalAlignment(SwingConstants.TOP);

        JPanel cardButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        cardButtons.setOpaque(false);
        JButton pDetail = createStyledButton("Details", new Color(236, 240, 241), Color.BLACK);
        pDetail.setPreferredSize(new Dimension(70, 25));
        pDetail.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JButton pAccept = createStyledButton("Accept", new Color(255, 107, 107), Color.WHITE);
        pAccept.setPreferredSize(new Dimension(70, 25));
        pAccept.setFont(new Font("SansSerif", Font.PLAIN, 11));

        cardButtons.add(pDetail);
        cardButtons.add(pAccept);

        previewCard.add(pImgPanel, BorderLayout.WEST);
        previewCard.add(previewText, BorderLayout.CENTER);
        previewCard.add(cardButtons, BorderLayout.SOUTH);

        panel.add(uploadPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblPreview);
        panel.add(Box.createVerticalStrut(10));
        panel.add(previewCard);

        return panel;
    }

    // --- Helper Methods ---

    private void addLabelAndInput(JPanel panel, String labelText, JComponent input, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel(labelText), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(input, gbc);
    }

    private void addLabelAndComponent(JPanel panel, String labelText, JComponent comp, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(createLabel(labelText), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(comp, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(INPUT_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private void styleTextArea(JTextArea area) {
        area.setFont(INPUT_FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    private void styleRadioButton(JRadioButton btn) {
        btn.setFont(INPUT_FONT);
        btn.setOpaque(false);
        btn.setFocusPainted(false);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.brighter() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 35));
        return btn;
    }

    // --- Logic ---

    private void togglePayment(boolean show) {
        paymentField.setVisible(show);
        paymentField.setEnabled(show);
        lblBaht.setVisible(show);
        paymentField.getParent().revalidate();
        paymentField.getParent().repaint();
    }

    private void clearForm() {
        titleField.setText("");
        vacanciesField.setText("");
        locationField.setText("");
        workingHoursField.setText("");
        detailsArea.setText("");
        paymentField.setText("");
        timeField.setText(""); // Clear Time
        noButton.setSelected(true);
        togglePayment(false);
        imageLabel.setIcon(null);
        imageLabel.setText("No Image");
        previewImage.setIcon(null);
        selectedImageFile = null;
        updatePreview();
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selectedImageFile);
                if (img != null) {
                    Image scaledUpload = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledUpload));
                    imageLabel.setText("");

                    Image scaledPreview = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    previewImage.setIcon(new ImageIcon(scaledPreview));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage());
            }
        }
    }

    // --- Live Preview ---

    private void setupLivePreview() {
        DocumentListener listener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updatePreview(); }
            public void removeUpdate(DocumentEvent e) { updatePreview(); }
            public void insertUpdate(DocumentEvent e) { updatePreview(); }
        };

        titleField.getDocument().addDocumentListener(listener);
        vacanciesField.getDocument().addDocumentListener(listener);
        locationField.getDocument().addDocumentListener(listener);
        workingHoursField.getDocument().addDocumentListener(listener);
        paymentField.getDocument().addDocumentListener(listener);
        // [แก้ไข 4] เพิ่ม Listener ให้ TextField เวลา
        timeField.getDocument().addDocumentListener(listener);

        dateSpinner.addChangeListener(e -> updatePreview());

        updatePreview();
    }

    private void updatePreview() {
        JSpinner.DateEditor dateEditor = (JSpinner.DateEditor) dateSpinner.getEditor();
        String dateText = dateEditor.getTextField().getText();

        // [แก้ไข 5] ดึงค่าเวลาจาก TextField แทน
        String timeText = timeField.getText();

        String paymentText = noButton.isSelected() ? "Unpaid" : paymentField.getText() + " Baht/hr";

        String html = "<html><body style='width:160px; font-family:SansSerif;'>"
                + "<h3 style='font-size:12px; margin:0 0 5px 0; color:#2c3e50;'>"
                + (titleField.getText().isEmpty() ? "(Event Title)" : titleField.getText()) + "</h3>"
                + "<div style='font-size:10px; color:#555;'>"
                + "<b>Vacancies:</b> " + vacanciesField.getText() + "<br>"
                + "<b>Date:</b> " + dateText + "<br>"
                + "<b>Time:</b> " + timeText + "<br>"
                + "<b>Loc:</b> " + locationField.getText().replace("\n", " ") + "<br>"
                + "<b>Hours:</b> " + workingHoursField.getText() + "<br>"
                + "<b>Pay:</b> <span style='color:" + (noButton.isSelected() ? "red" : "green") + ";'>" + paymentText + "</span>"
                + "</div></body></html>";

        previewText.setText(html);
    }

    static class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("\\d*")) super.insertString(fb, offset, string, attr);
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("\\d*")) super.replace(fb, offset, length, text, attrs);
        }
    }
}