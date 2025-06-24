package custom;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ModernScrollBarUI extends BasicScrollBarUI {

    private final int barWidth = 8;

    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(100, 100, 100);
        trackColor = new Color(240, 240, 240); // Nền scroll
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(thumbColor);
        g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 8, 8);

        g2.dispose();
    }

    @Override
    protected Dimension getMaximumThumbSize() {
        return new Dimension(barWidth, Integer.MAX_VALUE);
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(barWidth, 30);
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
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        return button;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        // Đặt chiều rộng thanh scroll vertical hoặc horizontal
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            return new Dimension(barWidth, super.getPreferredSize(c).height);
        } else {
            return new Dimension(super.getPreferredSize(c).width, barWidth);
        }
    }
}
