import common.Label;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import productions.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class PropagationTest {

    @InjectMocks
    private P1 p1;
    @InjectMocks
    private P2 p2;
    @InjectMocks
    private P3 p3;
    @InjectMocks
    private P4 p4;
    @InjectMocks
    private P5 p5;

    Graph graph;

    @Test
    public void propagationTest() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource("colors.jpg")).getFile());
        BufferedImage img = ImageIO.read(f);

        /***** First Step *****/

        graph = p1.run(img);

        Node nodeI = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.I))
                .findFirst()
                .get();

        graph = p5.run(graph, img, nodeI);
        graph = p2.run(graph, img, nodeI);

        List<Node> nodesLabeledB = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.B))
                .collect(Collectors.toList());

        for (Node node : nodesLabeledB) {
            graph = p3.run(graph, img, node);
        }

        graph.display();

        /***** Second Step *****/

        List<Node> nodesLabeledI = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.I) && !isFirstVertexNeighbor(n))
                .collect(Collectors.toList());

        for (Node node : nodesLabeledI) {
            graph = p5.run(graph, img, node);
        }

        for (Node node : nodesLabeledI) {
            graph = p2.run(graph, img, node);
        }

        nodesLabeledB = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.B) && !isFirstVertexNeighbor(n))
                .collect(Collectors.toList());

        for (Node node : nodesLabeledB) {
            graph = p3.run(graph, img, node);
        }

        List<Node> nodesLabeledF = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.FN) || checkLabel(n, Label.FW) || checkLabel(n, Label.FS) || checkLabel(n, Label.FE))
                .collect(Collectors.toList());

        for (Node node : nodesLabeledF) {
            //graph = p4.run(graph, img, node);
        }
        // graph = p4.run(graph, img, lowestFE, highestFN, secondLowestFS);
        // graph = p4.run(graph, img, findNode(Label.FE, 0), findNode(Label.FN, -1), findNode(Label.FS, 1));
        // graph = p4.run(graph, img, lowestFS, secondHighestFE, highestFW);
        // graph = p4.run(graph, img, findNode(Label.FS, 0), findNode(Label.FE, -2), findNode(Label.FW, -1));

        graph.display();

        /***** Third Step *****/

        nodeI = getFinalPropagationI();

        graph = p5.run(graph, img, nodeI);
        graph = p2.run(graph, img, nodeI);

        nodeI = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.I) && isFirstVertexNeighbor(n))
                .findFirst()
                .get();

        graph = p5.run(graph, img, nodeI);

        Assert.assertTrue(nodeI.getAttribute("break"));

        graph.display();
    }

    private boolean checkLabel(Node n, Label label) {
        return n.getAttribute("label").equals(label);
    }

    private boolean isFirstVertexNeighbor(Node node) {
        Iterator<Node> neighborIterator = node.getNeighborNodeIterator();
        while(neighborIterator.hasNext()) {
            Node neighbor = neighborIterator.next();
            if(neighbor.getId().equals("1") && neighbor.getAttribute("label").equals(Label.V)) {
                return true;
            }
        }
        return false;
    }

    public Node getFinalPropagationI() {
        // warunek : ma 4 sasiadow + pierwszy i jest oddalony o 2 wezly

        Node firstI = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.I) && isFirstVertexNeighbor(n))
                .findFirst()
                .get();

        List<Node> iNodes = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.I))
                .collect(Collectors.toList());

        for (Node i : iNodes) {
            Iterator<Node> iNeighbours = i.getNeighborNodeIterator();

            int neighboursCount = 0;
            while(iNeighbours.hasNext()) {
                neighboursCount++;
                iNeighbours.next();
            }

            if (neighboursCount == 4) {
                iNeighbours = i.getNeighborNodeIterator();
                while (iNeighbours.hasNext()){
                    Node neighbour = iNeighbours.next();
                    Iterator<Node> neighboursOfNeighbours = neighbour.getNeighborNodeIterator();
                    while (neighboursOfNeighbours.hasNext()) {
                        if ((neighboursOfNeighbours.next().getId().equals(firstI.getId())) && (!i.getId().equals(firstI.getId()))) {
                            return i;
                        }
                    }
                }
            }
        }
        return null;
    }
}
