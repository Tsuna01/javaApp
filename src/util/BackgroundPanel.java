package util;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {

    private final Image image;

    public BackgroundPanel(String imagePath) {
        image = new ImageIcon(imagePath).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // วาดภาพเต็มพื้นที่ panel
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
}
