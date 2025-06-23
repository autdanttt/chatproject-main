package custom;
import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private final int cornerRadius;
    private final Color backgroundColor;
    private final Color borderColor;
    private final int borderThickness;

    public RoundedPanel(int radius) {
        this(radius, Color.WHITE, Color.BLACK, 1);
    }

    public RoundedPanel(int radius, Color bgColor, Color borderColor, int borderThickness) {
        super();
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        this.borderThickness = borderThickness;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Tô nền
        g2.setColor(backgroundColor);
        g2.fillRoundRect(
                borderThickness / 2,
                borderThickness / 2,
                getWidth() - borderThickness,
                getHeight() - borderThickness,
                cornerRadius,
                cornerRadius
        );

        // Vẽ viền bo góc
        if (borderThickness > 0) {
            g2.setStroke(new BasicStroke(borderThickness));
            g2.setColor(borderColor);
            g2.drawRoundRect(
                    borderThickness / 2,
                    borderThickness / 2,
                    getWidth() - borderThickness,
                    getHeight() - borderThickness,
                    cornerRadius,
                    cornerRadius
            );
        }

        g2.dispose();
        super.paintComponent(g); // Đặt sau để đảm bảo child components vẫn hiển thị
    }
}

