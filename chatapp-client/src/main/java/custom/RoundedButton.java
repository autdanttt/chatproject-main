package custom;

import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    Dotenv dotenv = Dotenv.load();
    String COLOR_BG = dotenv.get("COLOR_BG");

    private final Color normalTextColor = Color.WHITE;
    private final Color hoverTextColor = Color.decode("#090040");

    private final Color normalBackgroundColor = Color.decode("#090040");
    private final Color hoverBackgroundColor = Color.WHITE;

    private float animationProgress = 0.0f;
    private final Timer animationTimer;
    private boolean hovering = false;

    public RoundedButton(String text) {
        super(text.toUpperCase());
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Montserrat", Font.BOLD, 13));
        setPreferredSize(new Dimension(140, 44));
        setForeground(normalTextColor);

        animationTimer = new Timer(10, e -> {
            if (hovering && animationProgress < 1f) {
                animationProgress += 0.08f;
            } else if (!hovering && animationProgress > 0f) {
                animationProgress -= 0.08f;
            }
            animationProgress = Math.max(0f, Math.min(1f, animationProgress));
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                animationTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                animationTimer.start();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bgColor = blend(normalBackgroundColor, hoverBackgroundColor, animationProgress);
        Color fgColor = blend(normalTextColor, hoverTextColor, animationProgress);
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        setForeground(fgColor); // áp dụng màu chữ mượt

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Không cần vẽ viền
    }

    private Color blend(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(r, g, b);
    }
}
