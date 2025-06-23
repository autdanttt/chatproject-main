package custom;

import javax.swing.*;
import java.awt.*;

public class RoundedPasswordField extends JPasswordField {
    private int arc = 16;

    public RoundedPasswordField(int columns) {
        super(columns);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.decode("#E9E9E9"));
        ((Graphics2D) g).drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
    }
}
