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

        graph.display().disableAutoLayout();

        /***** Second Step *****/

        List<Node> nodesLabeledI = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.I) && !isSpecificNeighbor(n, "1"))
                .collect(Collectors.toList());

        for (Node node : nodesLabeledI) {
            graph = p5.run(graph, img, node);
        }

        for (Node node : nodesLabeledI) {
            graph = p2.run(graph, img, node);
        }

        nodesLabeledB = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.B) && !isSpecificNeighbor(n, "1"))
                .collect(Collectors.toList());

        for (Node node : nodesLabeledB) {
            graph = p3.run(graph, img, node);
        }

        List<Node> nodesLabeledF = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.FE) || checkLabel(n, Label.FS) || checkLabel(n, Label.FS) || checkLabel(n, Label.FS))
                .collect(Collectors.toList());

        for (Node node : nodesLabeledF) {
            graph = p4.run(graph, img, node);
        }

//        nodeI = graph.getNodeSet().stream()
//                .filter(n -> checkLabel(n, Label.I) && isSpecificNeighbor(n, "4"))
//                .findFirst()
//                .get();

//        graph = p5.run(graph, img, nodeI);
//        graph = p2.run(graph, img, nodeI);

        graph.display().disableAutoLayout();
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /***** Third Step *****/

        nodeI = getFinalPropagationI(4);

        graph = p5.run(graph, img, nodeI);
        graph = p2.run(graph, img, nodeI);

        nodeI = getFinalPropagationI(2);

        graph = p5.run(graph, img, nodeI);

        nodeI = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.I) && isSpecificNeighbor(n, "1"))
                .findFirst()
                .get();

        Assert.assertTrue(nodeI.getAttribute("break"));

        graph.display().disableAutoLayout();
    }

    private boolean checkLabel(Node n, Label label) {
        return n.getAttribute("label").equals(label);
    }

    private boolean isSpecificNeighbor(Node node, String nodeId) {
        Iterator<Node> neighborIterator = node.getNeighborNodeIterator();
        while(neighborIterator.hasNext()) {
            Node neighbor = neighborIterator.next();
            if(neighbor.getId().equals(nodeId) && neighbor.getAttribute("label").equals(Label.V)) {
                return true;
            }
        }
        return false;
    }

    public Node getFinalPropagationI(int neededNeighbourCount) {
        // warunek : ma 4 sasiadow + pierwszy i jest oddalony o 2 wezly

        Node firstI = graph.getNodeSet().stream()
                .filter(n -> checkLabel(n, Label.I) && isSpecificNeighbor(n, "1"))
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

            if (neighboursCount == neededNeighbourCount) {
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
