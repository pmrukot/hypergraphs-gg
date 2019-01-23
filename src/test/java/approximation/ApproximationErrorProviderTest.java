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
public class ApproximationErrorProviderTest {
    @InjectMocks
    private P1 p1;

    @Test
    public void testApproximationErrorProviderCalculate() throws Exception {
        int width = 100;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, Color.BLACK.getRGB());
            }
        }

        Graph graph = p1.run(image);
        ApproximationErrorProvider approximationError = new ApproximationErrorProvider();
        double error = approximationError.calculateErrorForElement(graph, image, graph.getNode("5"));

        assertEquals(0., error, 1e-15);
    }

    @Test
    public void testApproximationErrorProviderCalculateBigError() throws Exception{
        int width = 100;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, Color.GREEN.getRGB());
            }
        }
        image.setRGB(0, 0, Color.RED.getRGB());
        image.setRGB(0, height - 1, Color.RED.getRGB());
        image.setRGB(width - 1, 0, Color.RED.getRGB());
        image.setRGB(width - 1, height - 1, Color.RED.getRGB());

        Graph graph = p1.run(image);
        ApproximationErrorProvider approximationError = new ApproximationErrorProvider();
        double error = approximationError.calculateErrorForElement(graph, image, graph.getNode("5"));


        assertEquals(5.199E8, error, 1E5);
    }

    @Test
    public void testApproximationErrorProviderCalculateSmallError() throws Exception{
        int width = 100;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, Color.RED.getRGB());
            }
        }
        image.setRGB(width - 1, height - 1, Color.PINK.getRGB());

        Graph graph = p1.run(image);
        ApproximationErrorProvider approximationError = new ApproximationErrorProvider();
        double error = approximationError.calculateErrorForElement(graph, image, graph.getNode("5"));


        assertEquals(1.717E7, error, 1E3);
    }
    @Test
    public void testApproximationErrorProviderCalculate3Nodes() throws Exception{
        int width = 100;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, Color.RED.getRGB());
            }
        }

        Graph graph = p1.run(image);
        ApproximationErrorProvider approximationError = new ApproximationErrorProvider();
        graph.removeNode("3");
        double error = approximationError.calculateErrorForElement(graph, image, graph.getNode("5"));


        assertEquals(0, error, 1e-15);
    }
}
