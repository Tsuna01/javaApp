package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import service.API;
import ui.component.Navbar;

public class DetailJob extends JFrame {

    private static final Color BG_COLOR = new Color(240, 240, 240);
    private String jobid;

    // Fonts
    private static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 20);
    private static final Font FONT_TEXT = new Font("Tahoma", Font.PLAIN, 14);
    private static final Font FONT_HASHTAG = new Font("Tahoma", Font.PLAIN, 12);
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

        // Left: Image
        JLabel image = new JLabel(new ImageIcon(createPlaceholderImage(200, new Color(139, 69, 19))));
        image.setPreferredSize(new Dimension(200, 280));
        image.setVerticalAlignment(SwingConstants.TOP);

        card.add(image, BorderLayout.WEST);

        // Right: Details + Button
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        // Details
        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);
        String workingHours= "";
        String vacancies = "";
        for(API i: job){
            data.add(i.title);
            data.add(i.dateTime);
            data.add(i.location);
            workingHours = String.valueOf(i.workingHours);
            data.add(i.details);
            vacancies = String.valueOf(i.vacancies);
            data.add(i.jobType);

        }


        String dateD = data.get(1);
        String[] dayD = dateD.split(" ");
        String MM;

        String[] dateParts = dayD[0].split("-");

        String[] timeParts = dayD[1].split(":");

        System.out.println("ปี: " + dateParts[0]);
        System.out.println("เดือน: " + dateParts[1]);
        System.out.println("วันที่: " + dateParts[2]);

        System.out.println("ชั่วโมง: " + timeParts[0]);
        System.out.println("นาที: " + timeParts[1]);

        switch (dateParts[1]) {
            case "1":
            case "01":
                MM = "มกราคม";
                break;
            case "2":
            case "02":
                MM = "กุมภาพันธ์";
                break;
            case "3":
            case "03":
                MM = "มีนาคม";
                break;
            case "4":
            case "04":
                MM = "เมษายน";
                break;
            case "5":
            case "05":
                MM = "พฤษภาคม";
                break;
            case "6":
            case "06":
                MM = "มิถุนายน";
                break;
            case "7":
            case "07":
                MM = "กรกฎาคม";
                break;
            case "8":
            case "08":
                MM = "สิงหาคม";
                break;
            case "9":
            case "09":
                MM = "กันยายน";
                break;
            case "10":
                MM = "ตุลาคม";
                break;
            case "11":
                MM = "พฤศจิกายน";
                break;
            case "12":
                MM = "ธันวาคม";
                break;
            default:
                MM = "ไม่ทราบเดือน";
        }

        String type = data.get(4);

        if (type != null && type.equalsIgnoreCase("paid")) {
            data.set(4, "งานมีค่าตอบแทน");
        } else if (type != null && type.equalsIgnoreCase("volunteer")) {
            data.set(4, "งานจิตอาสา");
        } else {
            data.set(4, "");
        }


        JLabel title = new JLabel(" " + data.get(0));
        title.setFont(FONT_TITLE);

        JLabel date = new JLabel(
                "<html><font color='#FFD700'>☀</font> วันที่: "
                        + dateParts[2] + " " + MM + " " + dateParts[0]
                        + "<br>&nbsp;&nbsp;&nbsp;เวลา "
                        + timeParts[0] + ":" + timeParts[1] + " น.</html>"
        );
        date.setFont(FONT_TEXT);

        JLabel loc = new JLabel("<html><font color='red'>📍</font> " + data.get(2) + "</html>");
        loc.setFont(FONT_TEXT);

        JLabel warning = new JLabel("<html><font color='red'>🚨 รับ: " + vacancies + " อัตรา</font></html>");
        warning.setFont(new Font("Tahoma", Font.BOLD, 14));

        JLabel hours = new JLabel("<html>🔘 ประเภท: " + data.get(4) + " (" + workingHours + " ชม.)</html>");
        hours.setFont(FONT_TEXT);

        JLabel detailsLabel = new JLabel("<html>" + data.get(3) + "</html>");
        detailsLabel.setFont(FONT_TEXT);

// ===== add ลง panel =====
        details.add(title);
        details.add(Box.createVerticalStrut(10));
        details.add(date);
        details.add(Box.createVerticalStrut(8));
        details.add(loc);
        details.add(Box.createVerticalStrut(8));
        details.add(warning);
        details.add(Box.createVerticalStrut(8));
        details.add(hours);
        details.add(Box.createVerticalStrut(8));
        details.add(detailsLabel);


        rightPanel.add(details, BorderLayout.CENTER);

        // Accept Button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        btnPanel.setOpaque(false);

        JButton acceptBtn = new JButton("Accept Job") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background (pink to orange)
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

        btnPanel.add(acceptBtn);

        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        card.add(rightPanel, BorderLayout.CENTER);

        cardContainer.add(card);
        return cardContainer;
    }

    // ========= HELPERS ==========
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
