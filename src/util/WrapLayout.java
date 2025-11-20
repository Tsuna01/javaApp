package util;

import java.awt.*;
import javax.swing.JScrollPane;

/**
 * WrapLayout — FlowLayout ที่ขึ้นบรรทัดใหม่อัตโนมัติ
 */
public class WrapLayout extends FlowLayout {

    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {

            int maxWidth = target.getParent() instanceof JScrollPane
                    ? ((JScrollPane) target.getParent()).getViewport().getWidth()
                    : target.getWidth();

            if (maxWidth <= 0)
                maxWidth = Integer.MAX_VALUE;

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int x = 0, y = insets.top;
            int rowHeight = 0;
            Dimension dim = new Dimension(0, 0);

            for (Component comp : target.getComponents()) {
                if (!comp.isVisible()) continue;

                Dimension d = preferred ? comp.getPreferredSize() : comp.getMinimumSize();
                if (x + d.width > maxWidth) {
                    x = 0;
                    y += rowHeight + vgap;
                    rowHeight = 0;
                }

                if (x > 0) x += hgap;
                x += d.width;
                rowHeight = Math.max(rowHeight, d.height);
            }

            y += rowHeight + insets.bottom;
            return new Dimension(maxWidth, y);
        }
    }
}
