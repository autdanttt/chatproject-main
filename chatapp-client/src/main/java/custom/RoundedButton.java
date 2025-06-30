package custom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private Color textColor = Color.WHITE;
    private Color normalTextColor = Color.WHITE;
    private Color hoverTextColor = Color.decode("#090040");

    private Color backgroundColor = Color.decode("#090040");
    private Color hoverBackgroundColor = Color.WHITE;

    private float animationProgress = 0.0f;
    private Timer animationTimer;
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
        setForeground(textColor);

        // Animation timer: chạy mỗi 10ms để cập nhật hiệu ứng
        animationTimer = new Timer(10, e -> {
            if (hovering && animationProgress < 1f) {
                animationProgress += 0.08f;
                if (animationProgress > 1f) animationProgress = 1f;
                repaint();
            } else if (!hovering && animationProgress > 0f) {
                animationProgress -= 0.08f;
                if (animationProgress < 0f) animationProgress = 0f;
                repaint();
            }
        });

        // Mouse events
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

        // Nền đen trước
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // Nền trắng chạy từ trái sang phải dựa trên animationProgress
        int fillWidth = (int) (getWidth() * animationProgress);
        g2.setColor(hoverBackgroundColor);
        g2.fillRoundRect(0, 0, fillWidth, getHeight(), 10, 10);

        // Vẽ text với màu chuyển dần
        Color textCol = blend(normalTextColor, hoverTextColor, animationProgress);
        setForeground(textCol);

        // Gọi vẽ text gốc
        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Không vẽ viền
    }

    // Hàm blend màu giữa hai Color
    private Color blend(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(r, g, b);
    }
}
