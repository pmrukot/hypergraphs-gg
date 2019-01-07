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
import java.util.stream.Stream;

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
    public void propagationTest() throws IOException {
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


    }
}
