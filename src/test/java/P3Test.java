import common.Label;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import productions.P3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class P3Test {

    private Graph graph;
    private BufferedImage img;
    private Edge borderEdge;
    private Node f1;
    private P3 p3;

    @Before
    public void setUp() throws Exception {
        p3 = new P3();
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource("colors.jpg")).getFile());
        img = ImageIO.read(f);
        graph = p3.prepareTestGraph(img);
        borderEdge = graph.getEdge("1-2");
        f1 = graph.getNode("f1");
    }

    @Test
    public void testP3() {
        Graph result = p3.run(graph, img, borderEdge, f1);
        result.display();

        Collection<Node> nodeSet = result.getNodeSet();
        Collection<Edge> edgeSet = result.getEdgeSet();
        List<Object> labels = edgeSet.stream().map(Element::getId).collect(Collectors.toList());
        assertEquals(7, nodeSet.size());
        assertEquals(10, labels.size());
        String[] nodes = new String[]{"f1-3", "1-4", "4-3", "3-5", "5-2", "7-1",
        "7-2", "7-4", "7-5", "7-f1"};
        for (String node : nodes) {
            assertTrue(labels.contains(node));
        }

        Optional<Node> vNode = nodeSet.stream().filter(n -> n.getAttribute("label") == Label.V).findFirst();
        assertTrue(vNode.isPresent());
        assertEquals(new Color(img.getRGB((img.getWidth()-1)/2, 0)), vNode.get().getAttribute("rgb"));
    }

}
