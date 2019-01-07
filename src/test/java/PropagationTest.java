import common.Label;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
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
    private P4 p4;
    @InjectMocks
    private P5 p5;
    @InjectMocks
    private P6 p6;

    @Test
    public void propagationTest() throws IOException, InterruptedException {
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File(Objects.requireNonNull(classLoader.getResource("colors.jpg")).getFile());
        BufferedImage img = ImageIO.read(f);

        Graph graph = p1.run(img);

        Optional<Node> nodeI = graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.I)).findFirst();

        graph = p5.run(graph, img, nodeI.get());
        graph = p2.run(graph, img, nodeI.get());

        // run p3
        // run p3
        // run p3
        // run p3

        List<Node> nodes = graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.I)).collect(Collectors.toList());
        List<Node> nodesV = graph.getNodeSet().stream().filter(n -> n.getAttribute("label").equals(Label.V)).collect(Collectors.toList());


        graph = p5.run(graph, img, nodes.get(1));
        graph = p5.run(graph, img, nodes.get(2));
        graph = p5.run(graph, img, nodes.get(3));
        graph = p2.run(graph, img, nodes.get(1));
        graph = p2.run(graph, img, nodes.get(2));
        graph = p2.run(graph, img, nodes.get(3));
        // run p3
        // run p3
        // run p3
        // run p3
        // run p3
        // run p3

        Node nodeFN = p4.getNodeByLabel(graph, Label.FN);
        Node nodeFW = p4.getNodeByLabel(graph, Label.FW);
        Node nodeFE = p4.getNodeByLabel(graph, Label.FE);
        graph = p4.run(graph, img, nodeFN, nodeFW, nodeFE);

        nodeFN = p4.getNodeByLabel(graph, Label.FN);
        nodeFW = p4.getNodeByLabel(graph, Label.FW);
        nodeFE = p4.getNodeByLabel(graph, Label.FE);
        graph = p4.run(graph, img, nodeFN, nodeFW, nodeFE);


        graph.display();
        Thread.sleep(10000);

    }
}
