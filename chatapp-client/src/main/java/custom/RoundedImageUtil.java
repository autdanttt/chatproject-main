package custom;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class RoundedImageUtil {

    /**
     * Multi-step high-quality downscaling để tránh nhòe khi scale ảnh lớn xuống nhỏ.
     */
    private static BufferedImage getHighQualityScaledInstance(BufferedImage img, int targetWidth, int targetHeight) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        int w = img.getWidth();
        int h = img.getHeight();

        do {
            if (w > targetWidth) {
                w /= 2;
                if (w < targetWidth) w = targetWidth;
            }
            if (h > targetHeight) {
                h /= 2;
                if (h < targetHeight) h = targetHeight;
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }

    public static Image getRoundedImage(Image image, int diameter) {
        // Chuyển Image sang BufferedImage
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(image, 0, 0, null);
        bGr.dispose();

        // Scale chất lượng cao
        BufferedImage scaled = getHighQualityScaledInstance(bufferedImage, diameter, diameter);

        // Tạo ảnh bo tròn
        BufferedImage output = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Clear background trong suốt
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, diameter, diameter);
        g2.setComposite(AlphaComposite.SrcOver);

        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, diameter, diameter);
        g2.setClip(circle);

        g2.drawImage(scaled, 0, 0, null);
        g2.dispose();

        return output;
    }

    public static ImageIcon loadRoundedAvatarFromURL(String urlStr, int diameter) {
        try {
            URL url = new URL(urlStr);
            BufferedImage image = ImageIO.read(url);
            if (image == null) return null;
            Image roundedImage = getRoundedImage(image, diameter);
            return new ImageIcon(roundedImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ImageIcon loadRoundedAvatarFromFile(String filePath, int diameter) {
        try {
            BufferedImage image = ImageIO.read(new File(filePath));
            if (image == null) return null;
            Image roundedImage = getRoundedImage(image, diameter);
            return new ImageIcon(roundedImage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
