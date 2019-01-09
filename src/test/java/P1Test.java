import common.Geom;
import common.Label;
import common.Type;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.swingViewer.Viewer;
import org.junit.Test;
import org.graphstream.graph.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import productions.P1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class P1Test {

    @InjectMocks
    private P1 p1;

    @Test
    public void testP1() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource("colors.jpg")).getFile());
        BufferedImage img = ImageIO.read(f);

        Graph graph = p1.run(img);
        graph.addAttribute("ui.stylesheet", "graph { padding: 200px; fill-color: #EEE; }");


        //not sure if unit test is the best place for this...
        Viewer viewer = graph.display();
        viewer.disableAutoLayout();


        //todo: replace with something less ugly...
        //(how to check if window is still open?)

        try {
            sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //structure
        assertEquals(9, graph.getNodeCount());
        assertEquals(12, graph.getEdgeCount());
        assertEquals(1, graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.I)).count());
        assertEquals(4, graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.B)).count());
        assertEquals(4, graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.V)).count());
        assertEquals(12, graph.getEdgeSet().stream().filter(n -> n.getAttributeCount() == 0).count());
        assertEquals(5, graph.getNodeSet().stream().filter(n -> n.getAttribute("type").equals(Type.HYPEREDGE)).count());
        assertEquals(4, graph.getNodeSet().stream().filter(n -> n.getAttribute("type").equals(Type.VERTEX)).count());
        assertFalse(graph.getNode("5").getAttribute("break"));

        //check connections
        assertTrue(graph.getNode("1").hasEdgeToward("B1-2"));
        assertTrue(graph.getNode("2").hasEdgeToward("B2-4"));
        assertTrue(graph.getNode("4").hasEdgeToward("B4-3"));
        assertTrue(graph.getNode("3").hasEdgeToward("B3-1"));
        assertTrue(graph.getNode("1").hasEdgeToward("5"));
        assertTrue(graph.getNode("2").hasEdgeToward("5"));
        assertTrue(graph.getNode("4").hasEdgeToward("5"));
        assertTrue(graph.getNode("3").hasEdgeToward("5"));

        assertFalse(graph.getNode("1").hasEdgeToward("4"));
        assertFalse(graph.getNode("2").hasEdgeToward("3"));
        assertFalse(graph.getNode("3").hasEdgeToward("2"));
        assertFalse(graph.getNode("4").hasEdgeToward("1"));

        //RGB
        int width  = img.getWidth();
        int height = img.getHeight();

        assertEquals(getColor(img, 0, height - 1), graph.getNode("1").getAttribute("rgb"));
        assertEquals(getColor(img, width -1 , height - 1), graph.getNode("2").getAttribute("rgb"));
        assertEquals(getColor(img, 0, 0), graph.getNode("3").getAttribute("rgb"));
        assertEquals(getColor(img, width - 1, 0), graph.getNode("4").getAttribute("rgb"));


        //geom
        Geom topLeft = graph.getNode("1").getAttribute("geom");
        assertEquals(0,  topLeft.getX());
        assertEquals(height - 1, topLeft.getY());

        Geom topRight = graph.getNode("2").getAttribute("geom");
        assertEquals(width - 1,  topRight.getX());
        assertEquals(height -1 , topRight.getY());

        Geom bottomLeft = graph.getNode("3").getAttribute("geom");
        assertEquals(0,  bottomLeft.getX());
        assertEquals(0, bottomLeft.getY());

        Geom bottomRight = graph.getNode("4").getAttribute("geom");
        assertEquals(width - 1,  bottomRight.getX());
        assertEquals(0, bottomRight.getY());
    }

    private Color getColor(BufferedImage img, int x, int y) {
        return new Color(img.getRGB(x, y));
    }



}
