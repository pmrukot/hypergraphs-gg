import common.Geom;
import common.Label;
import common.Type;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import productions.P1;
import productions.P2;
import productions.P3;

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
    private Graph initialGraph;

    @Before
    public void setUp() throws Exception {
        p2 = new P2();
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource("colors.jpg")).getFile());
        BufferedImage img = ImageIO.read(f);
        this.initialGraph = createP2TestGraph(img); //p1.run(img);
    }

    @Test
    public void testP2() throws IOException, InterruptedException {
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource("colors.jpg")).getFile());
        BufferedImage img = ImageIO.read(f);

//        initialGraph.display().disableAutoLayout();
        //Thread.sleep(10000);

        Node nodeI = initialGraph.getNodeSet().stream().filter(node -> node.hasAttribute("label") && node.getAttribute("label").toString().equals(Label.I.toString())).findFirst().get();

        Graph resultGraph = p2.run(initialGraph, img, nodeI);

//        resultGraph.display().disableAutoLayout();

        //Thread.sleep(10000);

        assertEquals(13, resultGraph.getNodeCount());
        assertEquals(12, resultGraph.getEdgeCount());

        String[] expectedEdges = {"6-14",
"6-11",
"6-12",
"6-13",
"6-7",
"7-1",
"6-8",
"8-2",
"6-9",
"9-3",
"6-10",
"10-4"};

        List<String> actualEdges = new ArrayList<>();
        for (Edge edge : resultGraph.getEdgeSet()) {
            String id = edge.getId();
            actualEdges.add(id);
        }

        Assert.assertArrayEquals(expectedEdges, actualEdges.toArray());

    }

    public Graph createP2TestGraph(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int shorterSide = width > height ? width : height;
        Geom topLeft = new Geom( 1,shorterSide - 1);
        Geom bottomLeft = new Geom(1,1);
        Geom topRight = new Geom(shorterSide - 1,shorterSide - 1);
        Geom bottomRight = new Geom(shorterSide- 1,1);
        Geom center = new Geom(shorterSide/2, shorterSide/2);

        Graph graph = new SingleGraph("graph");
        addNode(graph, "1", topLeft, Type.VERTEX, Label.V, false);
        addNode(graph, "2", topRight, Type.VERTEX, Label.V, false);
        addNode(graph, "3", bottomLeft, Type.VERTEX, Label.V, false);
        addNode(graph, "4", bottomRight, Type.VERTEX, Label.V, false);
        addNode(graph, "5", center, Type.HYPEREDGE, Label.I, false).setAttribute("break", true);

        addEdge(graph, "1", "5");
        addEdge(graph, "2", "5");
        addEdge(graph, "4", "5");
        addEdge(graph, "3", "5");

        return graph;
    }


    private Node addNode(Graph graph, String name, Geom geom, Type type, Label label, boolean isBreak) {
        Node node = graph.addNode(name);
        node.setAttribute("geom", geom);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("break", isBreak);
        node.setAttribute("x", geom.getX());
        node.setAttribute("y", geom.getY());

        return node;
    }

    private void addEdge(Graph graph, String sourceName, String targetName) {
        String name = sourceName + "-" + targetName;
        graph.addEdge(name, sourceName, targetName);
    }

}
