package de.acepe.streamdeck.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import static de.acepe.streamdeck.device.StreamDeck.KEY_PIXEL_HEIGHT;
import static de.acepe.streamdeck.device.StreamDeck.KEY_PIXEL_WIDTH;

public final class IconHelper {

    public static BufferedImage imageFromText(String text) {
        BufferedImage img = new BufferedImage(KEY_PIXEL_WIDTH, KEY_PIXEL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 48);
        g2d.setFont(font);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int x = (KEY_PIXEL_WIDTH - fm.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = ((KEY_PIXEL_HEIGHT - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, x, y);
        g2d.dispose();
        return img;
    }

    public static BufferedImage createDefaultImage() {
        BufferedImage img = new BufferedImage(KEY_PIXEL_WIDTH, KEY_PIXEL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, KEY_PIXEL_WIDTH, KEY_PIXEL_HEIGHT);
        g.dispose();
        return img;
    }

    public static byte[] convertImage(Image fxImage) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(fxImage, null);
        return convertImage(bufferedImage);
    }

    /**
     * Converts the given image to the stream deck format.<br>
     * Format is:<br>
     * Color Schema: BGR<br>
     * Image size: 72 x 72 pixel<br>
     * Stored in an array with each byte stored seperatly (Size of each array is
     * 72 x 72 x 3 = 15_552).
     *
     * @param img Image to be converted
     * @return Byte arraythat contains the given image, ready to be sent to the
     * stream deck
     */
    public static byte[] convertImage(BufferedImage img) {
        img = IconHelper.fillBackground(img, Color.BLACK);
//        if (img.getHeight() > KEY_PIXEL_HEIGHT || img.getWidth() > KEY_PIXEL_WIDTH) {
        img = IconHelper.createResizedCopy(img);
//        }

        byte[] result = new byte[KEY_PIXEL_HEIGHT * KEY_PIXEL_WIDTH * 3];

        for (int row = 0; row < KEY_PIXEL_HEIGHT; row++) {
            for (int col = 0; col < KEY_PIXEL_WIDTH; col++) {
                Color color = new Color(img.getRGB(col, row));

                int i = ((row + 1) * KEY_PIXEL_HEIGHT * 3) - ((col) * 3) - 3;

                result[i] = (byte) color.getBlue();
                result[i + 1] = (byte) color.getGreen();
                result[i + 2] = (byte) color.getRed();
            }
        }
        return result;
    }

    public static BufferedImage createResizedCopy(BufferedImage src) {
        BufferedImage dest = new BufferedImage(KEY_PIXEL_WIDTH, KEY_PIXEL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(
                (double) KEY_PIXEL_WIDTH / src.getWidth(),
                (double) KEY_PIXEL_HEIGHT / src.getHeight());
        g.drawRenderedImage(src, at);
        return dest;
    }

    public static BufferedImage fillBackground(BufferedImage img, Color color) {
        BufferedImage nImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics2D g = nImg.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, nImg.getWidth(), nImg.getHeight());
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return nImg;
    }

    public static BufferedImage rotate180(BufferedImage inputImage) {
        // Flip the image vertically and horizontally; equivalent to rotating the image 180 degrees
        AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
        tx.translate(-inputImage.getWidth(null), -inputImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(inputImage, null);
    }

    /**
     * Creates a bitmap image of a solid colour. Takes an int - can be specified as 0xRRGGBB
     *
     * @param colour The colour passed as an int
     */
    public static byte[] createColourBitmap(int colour) {
        byte b = (byte) (colour & 0xFF);
        byte g = (byte) ((colour >> 8) & 0xFF);
        byte r = (byte) ((colour >> 16) & 0xFF);
        return createColourBitmap(r, g, b);
    }

    /**
     * Creates a bitmap image of a solid colour. Takes three bytes as the colour.
     *
     * @param r Red
     * @param g Green
     * @param b Blue
     */
    public static byte[] createColourBitmap(byte r, byte g, byte b) {
        byte[] rgb = {b, g, r};
        byte[] img = new byte[15552];
        for (int i = 0; i < img.length; i++)
            img[i] = rgb[i % 3];

        return img;
    }

}
