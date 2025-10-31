package ui;

import javax.swing.*;
import java.awt.*;
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
}
