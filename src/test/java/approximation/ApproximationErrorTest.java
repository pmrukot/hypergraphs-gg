package approximation;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.graphstream.graph.Graph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import productions.P1;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ApproximationErrorTest {

    @InjectMocks
    private P1 p1;

    @Test
    public void testApproximationErrorCalculation() throws ApproximationErrorComputationException {
        int width = 100;
        int height = 50;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, Color.RED.getRGB());
            }
        }

        Graph graph = p1.run(image);
        ApproximationError approximationError = new ApproximationError(image, graph);
        Double error = approximationError.compute(graph.getNode("5"));

        assertEquals(0., error, 1e-15);
    }

    @Test
    public void testApproximationErrorCalculationIncorrect() throws ApproximationErrorComputationException {
        int width = 3;
        int height = 3;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, Color.BLUE.getRGB());
            }
        }
        image.setRGB(0, 0, Color.RED.getRGB());
        image.setRGB(0, height - 1, Color.RED.getRGB());
        image.setRGB(width - 1, 0, Color.RED.getRGB());
        image.setRGB(width - 1, height - 1, Color.RED.getRGB());

        Graph graph = p1.run(image);
        ApproximationError approximationError = new ApproximationError(image, graph);
        Double error = approximationError.compute(graph.getNode("5"));

        assertEquals(Double.valueOf(227587.5), error);
    }
}
