import common.Geom;
import common.Label;
import common.Type;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import productions.P1;
import productions.P2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class P2Test {

    @InjectMocks
    private P2 p2;
    @InjectMocks
    private P1 p1;


    @Test
    public void testP2() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource("colors.jpg")).getFile());
        BufferedImage img = ImageIO.read(f);

        //todo - write a separate method to generate graph which returns a graph instead of using implementation of p1
        Graph initialGraph= p1.run(img);
        Node nodeI =  initialGraph.getNodeSet().stream().filter(node -> node.hasAttribute("label") && node.getAttribute("label").toString().equals(Label.I.toString())).findFirst().get();
        nodeI.setAttribute("break", true);

        Graph resultGraph = p2.run(initialGraph, img, nodeI);

        resultGraph.display();

        //todo - assertions
        assertEquals(13 /* not sure if it should be 9 or 13 */, resultGraph.getNodeCount());
    }

    private Color getColor(BufferedImage img, int x, int y) {
        return new Color(img.getRGB(x, y));
    }



}
