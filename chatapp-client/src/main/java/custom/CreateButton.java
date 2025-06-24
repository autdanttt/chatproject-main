package custom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IconButton extends JButton {

    public IconButton(String iconPath, Runnable onClick) {
        super(new ImageIcon(iconPath));

        setPreferredSize(new Dimension(48, 48));
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setContentAreaFilled(true);
                setBackground(new Color(220, 220, 220)); // hiệu ứng nhấn
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setContentAreaFilled(false);
            }
        });

        addActionListener(e -> onClick.run());
    }
}
