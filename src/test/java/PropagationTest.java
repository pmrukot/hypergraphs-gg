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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    @InjectMocks
    private P6 p6;

    Graph graph;

    @Test
    public void propagationTest() throws IOException, InterruptedException {
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource("colors.jpg")).getFile());
        BufferedImage img = ImageIO.read(f);

        graph = p1.run(img);

        Node nodeI = graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.I)).findFirst().get();

        graph = p5.run(graph, img, nodeI);
        graph = p2.run(graph, img, nodeI);

        Node nodeBN = graph.getNode("B1-2");
        Node nodeBE = graph.getNode("B2-4");
        Node nodeBS = graph.getNode("B4-3");
        Node nodeBW = graph.getNode("B3-1");
        Node nodeFN = p4.getNodeByLabel(graph, Label.FN);
        Node nodeFW = p4.getNodeByLabel(graph, Label.FW);
        Node nodeFE = p4.getNodeByLabel(graph, Label.FE);
        Node nodeFS = p4.getNodeByLabel(graph, Label.FS);
//        graph = p3.run(graph, img, nodeBN, nodeFN);
//        graph = p3.run(graph, img, nodeBE, nodeFE);
//        graph = p3.run(graph, img, nodeBS, nodeFS);
//        graph = p3.run(graph, img, nodeBW, nodeFW);

        List<Node> nodesLabeledI = graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.I)).collect(Collectors.toList());
        Node nodeForBreakCheck = nodesLabeledI.get(3);

        graph = p5.run(graph, img, nodesLabeledI.get(0));
        graph = p5.run(graph, img, nodesLabeledI.get(1));
        graph = p5.run(graph, img, nodesLabeledI.get(2));
        graph = p2.run(graph, img, nodesLabeledI.get(0));
        graph = p2.run(graph, img, nodesLabeledI.get(1));
        graph = p2.run(graph, img, nodesLabeledI.get(2));
        // graph = p3.run(graph, img, connotfindB, secondLowestFN);
        // graph = p3.run(graph, img, connotfindB, findNode(Label.FN, 1);
        // graph = p3.run(graph, img, connotfindB, secondLowestFE);
        // graph = p3.run(graph, img, connotfindB, findNode(Label.FE, 1);
        // graph = p3.run(graph, img, connotfindB, secondHighestFW);
        // graph = p3.run(graph, img, connotfindB, findNode(Label.FW, -2);
        // graph = p3.run(graph, img, connotfindB, secondHighestFS);
        // graph = p3.run(graph, img, connotfindB, findNode(Label.FS, -2);
        // graph = p3.run(graph, img, connotfindB, highestFS);
        // graph = p3.run(graph, img, connotfindB, findNode(Label.FS, -1);
        // graph = p3.run(graph, img, connotfindB, highestFE);
        // graph = p3.run(graph, img, connotfindB, findNode(Label.FE, -1);

        // graph = p4.run(graph, img, lowestFE, highestFN, secondLowestFS);
        //graph = p4.run(graph, img, findNode(Label.FE, 0), highestFN, secondLowestFS);
        // graph = p4.run(graph, img, lowestFS, secondHighestFE, highestFW);

        nodesLabeledI = graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.I)).collect(Collectors.toList());
        nodeI = nodesLabeledI.get(nodesLabeledI.size() - 4);
        graph = p5.run(graph, img, nodeI);
        graph = p2.run(graph, img, nodeI);
        //nodeI = lowestI;
        graph = p5.run(graph, img, nodeI);
        Assert.assertTrue(nodeI.getAttribute("break"));

        graph.display();
        Thread.sleep(10000);
    }

    public Node findNode(Label label, Integer offset){
        List<Node> nodes = graph.getNodeSet().stream()
                .filter(n -> n.getAttribute("label").equals(label))
                .collect(Collectors.toList());
        return nodes.get(nodes.size() + offset);
    }
}
