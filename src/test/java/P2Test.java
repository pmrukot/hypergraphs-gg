import common.Geom;
import common.Label;
import common.Type;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.Assert;
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
import java.util.ArrayList;
import java.util.List;
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

        Graph initialGraph= p1.run(img);
        Node nodeI =  initialGraph.getNodeSet().stream().filter(node -> node.hasAttribute("label") && node.getAttribute("label").toString().equals(Label.I.toString())).findFirst().get();
        nodeI.setAttribute("break", true);

        Graph resultGraph = p2.run(initialGraph, img, nodeI);

        resultGraph.display();

        assertEquals(13, resultGraph.getNodeCount());
        assertEquals(16, resultGraph.getEdgeCount());

        String[] expectedEdges = {"1-2", "2-4", "4-3", "3-1", "6-14", "6-11", "6-12", "6-13",
                "6-7", "7-4", "6-8", "8-3", "6-9", "9-1", "6-10", "10-2"};

        List<String> actualEdges = new ArrayList<>();
        for (Edge edge : resultGraph.getEdgeSet()) {
            String id = edge.getId();
            actualEdges.add(id);
        }
        Assert.assertArrayEquals(expectedEdges, actualEdges.toArray());

    }

    private Color getColor(BufferedImage img, int x, int y) {
        return new Color(img.getRGB(x, y));
    }



}
