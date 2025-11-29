package ui;

import java.awt.*;
import javax.swing.*;

import ui.component.Navbar;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.text.*;
import javax.swing.event.*;

public class AddEvent extends JFrame {

    private JLabel imageLabel;
    private JTextField titleField, vacanciesField, workingHoursField, dateField, timeField;
    private JTextArea locationField;
    private JLabel previewText;
    private JLabel previewImage;
    private File selectedImageFile;

    public static void main(String[] args) {
        EventQueue.invokeLater(AddEvent::run);
    }

    public AddEvent() {
        initialize();

        addLivePreview(titleField);
        addLivePreview(vacanciesField);
        addLivePreview(dateField);
        addLivePreview(timeField);
        addLivePreview(locationField);
        addLivePreview(workingHoursField);
    }

    private static void run() {
        try {
            AddEvent window = new AddEvent();
            window.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        String html = "<html><div style='width:140px;'>"
                + "<h4 style='font-size:11px; margin:0;'>" + titleField.getText() + "</h4>"
                + "🔹 Vacancies: <b>" + vacanciesField.getText() + "</b><br>"
                + "🔹 Date: <b>" + dateField.getText() + "</b><br>"
                + "🔹 Time: <b>" + timeField.getText() + "</b><br>"
                + "🔹 Location: <b>" + locationField.getText().replace("\n", "<br>") + "</b><br>"
                + "🔹 Working Hours: <b>" + workingHoursField.getText() + "</b>" + " Hours"
                + "</div></html>";

        previewText.setText(html);
    }

    private void addLivePreview(JTextComponent field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updatePreview();
            }

            public void removeUpdate(DocumentEvent e) {
                updatePreview();
            }

            public void insertUpdate(DocumentEvent e) {
                updatePreview();
            }
        });
    }

    class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string.matches("\\d+"))
                super.insertString(fb, offset, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text.matches("\\d+"))
                super.replace(fb, offset, length, text, attrs);
        }
    }

    private void initialize() {

        // Frame settings
        setBounds(100, 100, 900, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔹 Navbar
        JPanel navbar = new Navbar().build();
        add(navbar, BorderLayout.NORTH);

        // 🔹 Main Panel
        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel, BorderLayout.CENTER);

        // Heading
        JLabel lblAddNew = new JLabel("Add New Event");
        lblAddNew.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblAddNew.setBounds(128, 50, 200, 25);
        panel.add(lblAddNew);

        // Title
        JLabel lblTitle = new JLabel("Title :");
        lblTitle.setBounds(39, 100, 60, 20);
        panel.add(lblTitle);

        titleField = new JTextField();
        titleField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        titleField.setBounds(85, 100, 250, 25);
        panel.add(titleField);

        // Number of vacancies
        JLabel lblNumberOfVacancies = new JLabel("Number of vacancies :");
        lblNumberOfVacancies.setBounds(350, 100, 130, 20);
        panel.add(lblNumberOfVacancies);

        vacanciesField = new JTextField();
        vacanciesField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        vacanciesField.setBounds(490, 100, 150, 25);
        ((AbstractDocument) vacanciesField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        panel.add(vacanciesField);

        // Date & Time
        JLabel lblDate = new JLabel("Date & Time :");
        lblDate.setBounds(39, 130, 80, 20);
        panel.add(lblDate);
        // Date
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(125, 130, 120, 25);
        panel.add(dateSpinner);
        dateField = dateEditor.getTextField();

        // Time
        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setBounds(260, 130, 75, 25);
        panel.add(timeSpinner);
        timeField = timeEditor.getTextField();

        // Location
        JLabel lblPlace = new JLabel("Location :");
        lblPlace.setBounds(350, 130, 80, 20);
        panel.add(lblPlace);

        locationField = new JTextArea();
        locationField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        locationField.setBounds(420, 130, 220, 50);
        locationField.setLineWrap(true);
        locationField.setWrapStyleWord(true);
        panel.add(locationField);

        // Working Hours
        JLabel lblWorkingHours = new JLabel("Working Hours :");
        lblWorkingHours.setBounds(39, 160, 100, 20);
        panel.add(lblWorkingHours);

        workingHoursField = new JTextField();
        workingHoursField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        workingHoursField.setBounds(145, 160, 190, 25);
        ((AbstractDocument) workingHoursField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        panel.add(workingHoursField);

        // Details
        JLabel lblDetails = new JLabel("Details :");
        lblDetails.setBounds(39, 200, 80, 20);
        panel.add(lblDetails);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
        detailsArea.setBounds(39, 225, 350, 150);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        panel.add(detailsArea);

        // Preview Label
        JLabel lblPreviewOfYour = new JLabel("Preview of your post :");
        lblPreviewOfYour.setBounds(472, 185, 150, 20);
        panel.add(lblPreviewOfYour);

        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BorderLayout());
        previewPanel.setBounds(472, 210, 350, 175);
        previewPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        previewPanel.setBackground(Color.WHITE);
        panel.add(previewPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton detailBtn = new JButton("Details");
        detailBtn.setPreferredSize(new Dimension(80, 25));
        styleButton(detailBtn, new Color(230, 230, 230), Color.BLACK);

        JButton acceptBtn = new JButton("Accept");
        acceptBtn.setPreferredSize(new Dimension(80, 25));
        styleButton(acceptBtn, new Color(255, 153, 153), Color.WHITE);

        buttonPanel.add(detailBtn);
        buttonPanel.add(acceptBtn);
        previewPanel.add(buttonPanel, BorderLayout.SOUTH);

        previewImage = new JLabel();
        previewImage.setPreferredSize(new Dimension(120, 150));
        previewImage.setHorizontalAlignment(SwingConstants.CENTER);
        previewPanel.add(previewImage, BorderLayout.WEST);

        previewText = new JLabel();
        previewText.setFont(new Font("Tahoma", Font.PLAIN, 11));
        previewText.setVerticalAlignment(SwingConstants.TOP);
        previewText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        previewPanel.add(previewText, BorderLayout.CENTER);

        // Image
        JLabel lblImage = new JLabel("Image :");
        lblImage.setBounds(650, 26, 60, 20);
        panel.add(lblImage);

        imageLabel = new JLabel();
        imageLabel.setBounds(700, 26, 125, 125);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.add(imageLabel);

        JButton btnImport = new JButton("Import");
        btnImport.setBounds(720, 160, 84, 25);
        panel.add(btnImport);

        btnImport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg"));

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage img = ImageIO.read(selectedImageFile);
                    Image scaledImg = img.getScaledInstance(125, 125, Image.SCALE_SMOOTH);

                    imageLabel.setIcon(new ImageIcon(scaledImg));
                    previewImage.setIcon(new ImageIcon(scaledImg));

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading image");
                }
            }
        });

        // Buttons bottom
        JButton btnClear = new JButton("Clear");
        btnClear.setBounds(500, 389, 84, 25);
        panel.add(btnClear);

        JButton btnPost = new JButton("Post");
        btnPost.setBounds(700, 389, 84, 25);
        panel.add(btnPost);
    }

    public void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);

    }
}
