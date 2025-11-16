package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URL;

//---------- ตัวช่วยโหลดรูป (กัน null) ----------
class ImageUtils {
    static ImageIcon loadScaled(String path, int w, int h) {
        try {
            ImageIcon raw = new ImageIcon(path);
            if (raw.getIconWidth() <= 0 || raw.getIconHeight() <= 0) {
                return placeholder(w, h);
            }
            Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return placeholder(w, h);
        }
    }
    private static ImageIcon placeholder(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(new Color(241, 245, 249));
        g2.fillRect(0, 0, w, h);
        g2.setColor(new Color(148, 163, 184));
        g2.drawRect(0, 0, w - 1, h - 1);
        g2.drawString("hero.png", 10, h / 2);
        g2.dispose();
        return new ImageIcon(img);
    }
    
    public static ImageIcon makeRoundedIcon(ImageIcon imageIcon, int diameter) {
        if (imageIcon == null || imageIcon.getImage() == null) {
            System.err.println("⚠️ imageIcon is null!");
            return null;
        }

        int w = imageIcon.getIconWidth();
        int h = imageIcon.getIconHeight();

        // ทำให้เป็นสี่เหลี่ยมจัตุรัสจากตรงกลาง
        int size = Math.min(w, h);
        int x = (w - size) / 2;
        int y = (h - size) / 2;

        BufferedImage square = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g1 = square.createGraphics();
        g1.drawImage(imageIcon.getImage(), -x, -y, null);
        g1.dispose();

        // Resize เป็นขนาดที่ต้องการ
        Image scaled = square.getScaledInstance(diameter, diameter, Image.SCALE_SMOOTH);

        // ตัดให้เป็นวงกลม
        BufferedImage rounded = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = rounded.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, diameter, diameter);

        g2.setComposite(AlphaComposite.Src);
        Shape circle = new Ellipse2D.Float(0, 0, diameter, diameter);
        g2.setClip(circle);

        g2.drawImage(scaled, 0, 0, null);
        g2.dispose();

        return new ImageIcon(rounded);
    }

}
