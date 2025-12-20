package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import service.API;
import service.Auth; // เรียกใช้ Auth
import service.JobManager; // เรียกใช้ JobManager ที่เพิ่งแก้
import ui.component.Navbar;

public class DetailJob extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);
    private String jobid;

    // Fonts
    private static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 20);
    private static final Font FONT_TEXT = new Font("Tahoma", Font.PLAIN, 14);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_BACK = new Font("SansSerif", Font.PLAIN, 14);

    public DetailJob(String jobid) {
        this.jobid = jobid;
        initialize();
    }

    private void initialize() {
        setTitle("Detail Job");
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
        content.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Back Button
        content.add(createBackButton(), BorderLayout.NORTH);

        // Job Detail Card
        content.add(createJobDetailCard(), BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
    }

    // ========= BACK BUTTON ==========
    private JPanel createBackButton() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        panel.setOpaque(false);

        JButton backBtn = new JButton("← Back") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(Color.BLACK);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        backBtn.setFont(FONT_BACK);
        backBtn.setForeground(Color.BLACK);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setPreferredSize(new Dimension(100, 35));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            new AvailableJob().setVisible(true);
            dispose();
        });

        panel.add(backBtn);
        return panel;
    }

    // ========= JOB DETAIL CARD ==========
    private JPanel createJobDetailCard() {
        ArrayList<API> job = API.getJobDetail(jobid);
        ArrayList<String> data = new ArrayList<>();
        JPanel cardContainer = new JPanel(new GridBagLayout());
        cardContainer.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(30, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);

                // BG
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(900, 400));

        // Data extraction variables
        String workingHours = "";
        String vacancies = "";
        String imagePath = null;
        int hoursInt = 0; // เก็บค่าชั่วโมงไว้ใช้ตอนสมัคร

        if (job != null && !job.isEmpty()) {
            for (API i : job) {
                data.add(i.title);      // 0
                data.add(i.dateTime);   // 1
                data.add(i.location);   // 2
                workingHours = String.valueOf(i.workingHours);
                hoursInt = i.workingHours; // เก็บค่า int ไว้
                data.add(i.details);    // 3
                vacancies = String.valueOf(i.vacancies);
                data.add(i.jobType);    // 4
                imagePath = i.imagePath;
            }
        } else {
            data.add("No Title"); data.add("2025-01-01 00:00:00"); data.add("-"); data.add("-"); data.add("-");
        }

        ImageIcon icon = loadAndResizeImage(imagePath, 250, 300);
        JLabel image = new JLabel(icon);
        image.setPreferredSize(new Dimension(250, 300));
        image.setVerticalAlignment(SwingConstants.TOP);
        image.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(image, BorderLayout.WEST);

        // Right: Details + Button
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);

        // --- Date Logic ---
        String dateD = (data.size() > 1 && data.get(1) != null) ? data.get(1) : "2025-01-01 00:00:00";
        String YYYY = "YYYY", MM_Num = "01", DD = "01", HH = "00", MIN = "00";

        try {
            String[] dayD = dateD.split(" ");
            if (dayD.length >= 1) {
                String[] dateParts = dayD[0].split("-");
                if (dateParts.length >= 3) {
                    YYYY = dateParts[0]; MM_Num = dateParts[1]; DD = dateParts[2];
                }
            }
            if (dayD.length >= 2) {
                String[] timeParts = dayD[1].split(":");
                if (timeParts.length >= 2) {
                    HH = timeParts[0]; MIN = timeParts[1];
                }
            }
        } catch (Exception e) {}

        String MM_Text;
        switch (MM_Num) {
            case "1": case "01": MM_Text = "มกราคม"; break;
            case "2": case "02": MM_Text = "กุมภาพันธ์"; break;
            case "3": case "03": MM_Text = "มีนาคม"; break;
            case "4": case "04": MM_Text = "เมษายน"; break;
            case "5": case "05": MM_Text = "พฤษภาคม"; break;
            case "6": case "06": MM_Text = "มิถุนายน"; break;
            case "7": case "07": MM_Text = "กรกฎาคม"; break;
            case "8": case "08": MM_Text = "สิงหาคม"; break;
            case "9": case "09": MM_Text = "กันยายน"; break;
            case "10": MM_Text = "ตุลาคม"; break;
            case "11": MM_Text = "พฤศจิกายน"; break;
            case "12": MM_Text = "ธันวาคม"; break;
            default: MM_Text = "ไม่ทราบเดือน";
        }

        String typeRaw = (data.size() > 4) ? data.get(4) : "";
        String typeDisplay;
        if (typeRaw != null && typeRaw.equalsIgnoreCase("paid")) {
            typeDisplay = "งานมีค่าตอบแทน";
        } else if (typeRaw != null && typeRaw.equalsIgnoreCase("volunteer")) {
            typeDisplay = "งานจิตอาสา";
        } else {
            typeDisplay = typeRaw;
        }

        JLabel title = new JLabel(" " + data.get(0));
        title.setFont(FONT_TITLE);

        JLabel date = new JLabel(
                "<html><font color='#FFD700'>☀</font> วันที่: "
                        + DD + " " + MM_Text + " " + YYYY
                        + "<br>&nbsp;&nbsp;&nbsp;เวลา "
                        + HH + ":" + MIN + " น.</html>"
        );
        date.setFont(FONT_TEXT);

        JLabel loc = new JLabel("<html><font color='red'>📍</font> " + data.get(2) + "</html>");
        loc.setFont(FONT_TEXT);

        JLabel warning = new JLabel("<html><font color='red'>🚨 รับ: " + vacancies + " อัตรา</font></html>");
        warning.setFont(new Font("Tahoma", Font.BOLD, 14));

        JLabel hours = new JLabel("<html>🔘 ประเภท: " + typeDisplay + " (" + workingHours + " ชม.)</html>");
        hours.setFont(FONT_TEXT);

        JLabel detailsLabel = new JLabel("<html>" + data.get(3) + "</html>");
        detailsLabel.setFont(FONT_TEXT);

        details.add(title);
        details.add(Box.createVerticalStrut(10));
        details.add(date);
        details.add(Box.createVerticalStrut(8));
        details.add(loc);
        details.add(Box.createVerticalStrut(8));
        details.add(warning);
        details.add(Box.createVerticalStrut(8));
        details.add(hours);
        details.add(Box.createVerticalStrut(15));
        details.add(detailsLabel);

        rightPanel.add(details, BorderLayout.CENTER);

        // ================= Button Logic =================
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        btnPanel.setOpaque(false);

        // 1. เช็คว่าสมัครงานนี้ไปหรือยัง
        boolean isAlreadyApplied = false;
        try {
            int jId = Integer.parseInt(this.jobid);
            isAlreadyApplied = JobManager.isJobApplied(jId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (isAlreadyApplied) {
            // [กรณีสมัครแล้ว] แสดงปุ่ม Disabled ที่บอกว่าสมัครแล้ว
            JButton appliedBtn = new JButton("Already Applied (รับงานแล้ว)") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // สีเทา
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            appliedBtn.setFont(FONT_BTN);
            appliedBtn.setForeground(Color.DARK_GRAY);
            appliedBtn.setContentAreaFilled(false);
            appliedBtn.setBorderPainted(false);
            appliedBtn.setPreferredSize(new Dimension(250, 45));
            appliedBtn.setEnabled(false); // กดไม่ได้

            btnPanel.add(appliedBtn);

        } else {
            // [กรณีเนังไม่สมัคร] แสดงปุ่ม Accept ปกติ
            JButton acceptBtn = new JButton("Accept Job") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, new Color(255, 160, 160),
                            getWidth(), 0, new Color(255, 200, 150));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                    super.paintComponent(g2);
                    g2.dispose();
                }
            };
            acceptBtn.setFont(FONT_BTN);
            acceptBtn.setForeground(Color.WHITE);
            acceptBtn.setContentAreaFilled(false);
            acceptBtn.setBorderPainted(false);
            acceptBtn.setPreferredSize(new Dimension(200, 45));
            acceptBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            int finalHoursInt = hoursInt; // ต้องเป็น final สำหรับ lambda
            acceptBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "คุณต้องการรับงานนี้ใช่หรือไม่?",
                        "ยืนยันการรับงาน",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int id = Integer.parseInt(this.jobid);
                        boolean success = JobManager.applyJob(id, finalHoursInt); // ใช้ค่าชั่วโมงที่ดึงมา

                        if (success) {
                            JOptionPane.showMessageDialog(this, "รับงานสำเร็จ! (Applied Successfully)");
                            new AvailableJob().setVisible(true);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "ไม่สามารถรับงานได้ (อาจเกิดข้อผิดพลาด)",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Job ID ไม่ถูกต้อง", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            btnPanel.add(acceptBtn);
        }

        rightPanel.add(btnPanel, BorderLayout.SOUTH);
        card.add(rightPanel, BorderLayout.CENTER);
        cardContainer.add(card);

        return cardContainer;
    }

    // ========= HELPERS ==========

    private ImageIcon loadAndResizeImage(String imagePath, int width, int height) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return new ImageIcon(createPlaceholderImage(width, new Color(139, 69, 19)));
        }
        try {
            BufferedImage originalImage = null;
            if (imagePath.startsWith("http")) {
                originalImage = ImageIO.read(new URL(imagePath));
            } else {
                File f = new File(imagePath);
                if (f.exists()) {
                    originalImage = ImageIO.read(f);
                } else {
                    File retry = new File("user_images/" + imagePath);
                    if (retry.exists()) originalImage = ImageIO.read(retry);
                }
            }
            if (originalImage != null) {
                Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {}
        return new ImageIcon(createPlaceholderImage(width, new Color(139, 69, 19)));
    }

    private Image createPlaceholderImage(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillRect(0, 0, size, size);
        g2.dispose();
        return img;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            DetailJob d = new DetailJob("");
            d.setVisible(true);
        });
    }
}