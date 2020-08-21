package gui.graph;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CellSize {
    private static final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    private static final Graphics2D g2d = img.createGraphics();
    private static final Font font = new Font("Arial", Font.PLAIN, 12);
    private static final FontMetrics metrics = g2d.getFontMetrics(font);
    private static final int padding = 15;


    public static int getX(String text) {
        return metrics.stringWidth(text) + padding;
    }

    public static int getY(String text) {
        int lineBreaks = text.split("\n").length;
        return metrics.getHeight() * lineBreaks + padding;
    }
}
