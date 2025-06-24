package custom;

import javax.swing.*;
import java.awt.*;

// Custom JButton with rounded corners
public class RoundedButton extends JButton {
    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBackground(Color.BLACK);
        setFont(new Font("Montserrat", Font.BOLD, 13));
        setPreferredSize(new Dimension(140, 44));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose(); // clean

        // ✅ Bây giờ mới vẽ text, border, focus...
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Không vẽ viền nếu không muốn
    }
}
