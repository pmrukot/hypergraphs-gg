package approximation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class BitmapAproximator {

    public BufferedImage bitmap;

    public int[][] APPROX_R;
    public int[][] APPROX_G;
    public int[][] APPROX_B;

    public BitmapAproximator() {
        initializeTables(1000, 1000);
    }

    public BitmapAproximator(int maxX, int maxY) {
        initializeTables(maxX, maxY);
    }

    public void run(Color rgb1, Color rgb2, Color rgb3, Color rgb4, int x1, int y1, int x2, int y2) {
        fillTables(rgb1, rgb2, rgb3, rgb4, x1, y1, x2, y2);
        drawImage(x1, y1, x2, y2);
    }

    private void fillTables(Color rgb1, Color rgb2, Color rgb3, Color rgb4, int x1, int y1, int x2, int y2) {
        for (int px = x1; px <= x2; px++) {
            for (int py = y1; py <= y2; py++) {
                APPROX_R[px][py] = rgb1.getRed() * get1stMultiplier(x1, y1, x2, y2, px, py)
                                 + rgb2.getRed() * get2ndMultiplier(x1, y1, x2, y2, px, py)
                                 + rgb3.getRed() * get3rdMultiplier(x1, y1, x2, y2, px, py)
                                 + rgb4.getRed() * get4thMultiplier(x1, y1, x2, y2, px, py);
                APPROX_G[px][py] = rgb1.getGreen() * get1stMultiplier(x1, y1, x2, y2, px, py)
                                 + rgb2.getGreen() * get2ndMultiplier(x1, y1, x2, y2, px, py)
                                 + rgb3.getGreen() * get3rdMultiplier(x1, y1, x2, y2, px, py)
                                 + rgb4.getGreen() * get4thMultiplier(x1, y1, x2, y2, px, py);
                APPROX_B[px][py] = rgb1.getBlue() * get1stMultiplier(x1, y1, x2, y2, px, py)
                                 + rgb2.getBlue() * get2ndMultiplier(x1, y1, x2, y2, px, py)
                                 + rgb3.getBlue() * get3rdMultiplier(x1, y1, x2, y2, px, py)
                                 + rgb4.getBlue() * get4thMultiplier(x1, y1, x2, y2, px, py);
            }
        }
    }

    private void drawImage(int x1, int y1, int x2, int y2) {
        bitmap = new BufferedImage(x2+1, y2+1, BufferedImage.TYPE_INT_RGB);
        for (int px = x1; px <= x2; px++) {
            for (int py = y1; py <= y2; py++) {
                bitmap.setRGB(px, py, getRGBValue(px, py));
            }
        }
        saveFile();
    }

    private void initializeTables(int x2, int y2) {
        APPROX_R = new int[x2+1][y2+1];
        APPROX_G = new int[x2+1][y2+1];
        APPROX_B = new int[x2+1][y2+1];
    }

    private int get1stMultiplier(int x1, int y1, int x2, int y2, int px, int py) {
        return (1 - (px - x1) / (x2 - x1)) * (py - y1) / (y2 - y1);
    }

    private int get2ndMultiplier(int x1, int y1, int x2, int y2, int px, int py) {
        return (px - x1) / (x2 - x1) * (py - y1) / (y2 - y1);
    }

    private int get3rdMultiplier(int x1, int y1, int x2, int y2, int px, int py) {
        return (1 - (px - x1) / (x2 - x1)) * (1 - (py - y1) / (y2 - y1));
    }

    private int get4thMultiplier(int x1, int y1, int x2, int y2, int px, int py) {
        return (px - x1) / (x2 - x1) * (1 - (py - y1) / (y2 - y1));
    }

    private int getRGBValue(int px, int py) {
        return ((APPROX_R[px][py] & 0xFF) << 16) |
                ((APPROX_G[px][py] & 0xFF) << 8)  |
                (APPROX_B[px][py] & 0xFF);
    }

    private void saveFile() {
        try {
            ImageIO.write(bitmap,"PNG", new File("bitmap.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
