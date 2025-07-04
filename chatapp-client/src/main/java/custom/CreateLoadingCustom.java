package custom;

import javax.swing.*;
import java.awt.*;

public class CreateLoadingCustom {

    public static JDialog create(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Đang xử lý...", true);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Vui lòng đợi...");
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        return dialog;
    }
}
