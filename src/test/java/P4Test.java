import common.Geom;
import common.Label;
import common.Type;
import org.graphstream.graph.Graph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import productions.P4;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class P4Test {

    @InjectMocks
    private P4 p4;

    @Test
    public void testP4() throws IOException {
        Graph graph = p4.prepareTestGraph();

        assertEquals(11, graph.getNodeCount());
        assertEquals(12, graph.getEdgeCount());
        assertEquals(4, graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.I)).count());
        assertEquals(4, graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.V)).count());
        assertEquals(1, graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.FE)).count());
        assertEquals(1, graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.FW)).count());
        assertEquals(1, graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.FN)).count());
        assertEquals(7, graph.getNodeSet().stream().filter(n -> n.getAttribute("type").equals(Type.HYPEREDGE)).count());
        assertEquals(4, graph.getNodeSet().stream().filter(n -> n.getAttribute("type").equals(Type.VERTEX)).count());

        Graph graphP4 = p4.run(graph);

        assertEquals(13, graphP4.getNodeCount());
        assertEquals(21, graphP4.getEdgeCount());
        assertEquals(4, graphP4.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.I)).count());
        assertEquals(5, graphP4.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.V)).count());
        assertEquals(1, graphP4.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.FE)).count());
        assertEquals(1, graphP4.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.FW)).count());
        assertEquals(1, graphP4.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.FN)).count());
        assertEquals(1, graphP4.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.FS)).count());
        assertEquals(9, graphP4.getNodeSet().stream().filter(n -> n.getAttribute("type").equals(Type.HYPEREDGE)).count());
        assertEquals(4, graphP4.getNodeSet().stream().filter(n -> n.getAttribute("type").equals(Type.VERTEX)).count());

        Geom north = graphP4.getNode("5").getAttribute("geom");
        Geom west = graphP4.getNode("6").getAttribute("geom");
        Geom south = graphP4.getNode("7").getAttribute("geom");
        Geom east = graphP4.getNode("8").getAttribute("geom");
        Geom v = graphP4.getNode("v").getAttribute("geom");

        assertEquals(north.getX(), v.getX());
        assertEquals(south.getX(), v.getX());
        assertEquals(west.getY(), v.getY());
        assertEquals(east.getY(), v.getY());
    }
}
