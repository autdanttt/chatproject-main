package utility;

import dev.onvoid.webrtc.media.MediaDevices;
import dev.onvoid.webrtc.media.audio.AudioDevice;
import dev.onvoid.webrtc.media.video.VideoDevice;

import javax.swing.*;
import java.awt.*;

public class HardwareSelectionDialog extends JDialog {
    private JComboBox<VideoDevice> cameraComboBox;
    private JComboBox<AudioDevice> micComboBox;
    private VideoDevice selectedCamera;
    private AudioDevice selectedMic;
    private boolean confirmed;



    public HardwareSelectionDialog(JFrame parent) {
        super(parent, "Select Hardware", true);
        setSize(400, 150);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(3, 2, 10, 10));

        // Camera selection
        JLabel cameraLabel = new JLabel("Camera:");
        cameraComboBox = new JComboBox<>();
        for (VideoDevice webcam : MediaDevices.getVideoCaptureDevices()) {
            cameraComboBox.addItem(webcam);
        }
        if (cameraComboBox.getItemCount() > 0) {
            cameraComboBox.setSelectedIndex(0);
        }

        // Microphone selection
        JLabel micLabel = new JLabel("Microphone:");
        micComboBox = new JComboBox<>();
        for (AudioDevice mic : MediaDevices.getAudioCaptureDevices()) {
            micComboBox.addItem(mic);
        }
        if (micComboBox.getItemCount() > 0) {
            micComboBox.setSelectedIndex(0);
        }

        // Buttons
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            selectedCamera = (VideoDevice) cameraComboBox.getSelectedItem();
            selectedMic = (AudioDevice) micComboBox.getSelectedItem();
            confirmed = true;
            setVisible(false);
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        // Add components
        add(cameraLabel);
        add(cameraComboBox);
        add(micLabel);
        add(micComboBox);
        add(okButton);
        add(cancelButton);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public VideoDevice getSelectedCamera() {
        return selectedCamera;
    }

    public AudioDevice getSelectedMic() {
        return selectedMic;
    }
}
