package approximation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BitmapApproximatorTest {

    @InjectMocks
    private BitmapAproximator approximator;

    private int x1 = 10;
    private int x2 = 20;
    private int y1 = 41;
    private int y2 = 179;

    private BufferedImage loadImage() throws IOException {
        File file = new File("bitmap.png");
        return ImageIO.read(file);
    }

    @Test
    public void testRedBitmap() throws IOException {
        Optional<Color> color = Optional.of(Color.RED);

        approximator.run(color, color, color, color, x1, y1, x2, y2);

        BufferedImage image = loadImage();
        for (int px = x1; px <= x2; px++) {
            for (int py = y1; py <= y2; py++) {
                assertEquals(color.get().getRGB(), image.getRGB(px, py));
            }
        }
    }

    @Test
    public void testGreenBitmap() throws IOException {
        Optional<Color> color = Optional.of(Color.GREEN);

        approximator.run(color, color, color, color, x1, y1, x2, y2);

        BufferedImage image = loadImage();
        for (int px = x1; px <= x2; px++) {
            for (int py = y1; py <= y2; py++) {
                assertEquals(color.get().getRGB(), image.getRGB(px, py));
            }
        }
    }

    @Test
    public void testBlueBitmap() throws IOException {
        Optional<Color> color = Optional.of(Color.BLUE);

        approximator.run(color, color, color, color, x1, y1, x2, y2);

        BufferedImage image = loadImage();
        for (int px = x1; px <= x2; px++) {
            for (int py = y1; py <= y2; py++) {
                assertEquals(color.get().getRGB(), image.getRGB(px, py));
            }
        }
    }

    @Test
    public void testWhiteBitmap() throws IOException {
        Optional<Color> color = Optional.of(Color.WHITE);

        approximator.run(color, color, color, color, x1, y1, x2, y2);

        BufferedImage image = loadImage();
        for (int px = x1; px <= x2; px++) {
            for (int py = y1; py <= y2; py++) {
                assertEquals(color.get().getRGB(), image.getRGB(px, py));
            }
        }
    }

    @Test
    public void testBlackBitmap() throws IOException {
        Optional<Color> color = Optional.of(Color.BLACK);

        approximator.run(color, color, color, color, x1, y1, x2, y2);

        BufferedImage image = loadImage();
        for (int px = x1; px <= x2; px++) {
            for (int py = y1; py <= y2; py++) {
                assertEquals(color.get().getRGB(), image.getRGB(px, py));
            }
        }
    }
}
