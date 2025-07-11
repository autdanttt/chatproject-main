package custom;

import javax.swing.*;
import java.awt.*;

public class DefaultPanel extends JPanel {
    private final String[] slides = {
            "Chọn một cuộc trò chuyện để bắt đầu!",
            "Gửi tin nhắn, hình ảnh, file dễ dàng!",
            "Tạo nhóm và trò chuyện cùng bạn bè!",
            "Bảo mật tin nhắn với mã hóa end-to-end!"
    };
    private int currentSlide = 0;
    private float alpha = 1.0f;
    private Timer fadeOutTimer;
    private Timer fadeInTimer;
    private final JLabel label;

    public DefaultPanel() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JPanel p1 = new RoundedPanel(20, Color.WHITE, Color.decode("#E9E9E9"), 2) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        p1.setLayout(new BorderLayout());

        label = new JLabel(slides[0]);
        label.setFont(new Font("Montserrat", Font.BOLD, 20));
        label.setForeground(Color.GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        p1.add(label, BorderLayout.CENTER);

        add(p1, BorderLayout.CENTER);

        Timer slideTimer = new Timer(2000, e -> startFadeOut());
        slideTimer.start();
    }

    private void startFadeOut() {
        if (fadeInTimer != null && fadeInTimer.isRunning()) fadeInTimer.stop();
        fadeOutTimer = new Timer(50, null);
        fadeOutTimer.addActionListener(e -> {
            alpha -= 0.05f;
            if (alpha <= 0.0f) {
                alpha = 0.0f;
                fadeOutTimer.stop();
                changeSlide();
                startFadeIn();
            }
            repaint();
        });
        fadeOutTimer.start();
    }

    private void startFadeIn() {
        fadeInTimer = new Timer(50, null);
        fadeInTimer.addActionListener(e -> {
            alpha += 0.05f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                fadeInTimer.stop();
            }
            repaint();
        });
        fadeInTimer.start();
    }

    private void changeSlide() {
        currentSlide = (currentSlide + 1) % slides.length;
        label.setText(slides[currentSlide]);
    }
}

