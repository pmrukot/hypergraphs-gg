import common.Label;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.swingViewer.Viewer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import productions.P1;
import productions.P4;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;


@ComponentScan({"productions"})
public class Hypergraph {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(Hypergraph.class);
        P1 p1 = context.getBean(P1.class);

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        BufferedImage img = ImageIO.read(Objects.requireNonNull(cl.getResourceAsStream("colors.jpg")));
        Graph graph = p1.run(img);
        graph.addAttribute("ui.stylesheet", "graph { padding: 200px; fill-color: #EEE; }");
        Viewer viewer = graph.display();
        viewer.disableAutoLayout();

//        // Preparing P4 test graph
//        P4 p4 = context.getBean(P4.class);
//        Graph preP4 = p4.prepareTestGraph(img);
//        preP4.display().disableAutoLayout();
//
//        // Extracting input nodes for P4 test
//        Node nodeFN = p4.getNodeByLabel(preP4, Label.FN);
//        Node nodeFW = p4.getNodeByLabel(preP4, Label.FW);
//        Node nodeFE = p4.getNodeByLabel(preP4, Label.FE);
//
//        // Run P4 production
//        Graph postP4 = p4.run(preP4, img, nodeFN, nodeFW, nodeFE);
//        postP4.display().disableAutoLayout();
    }

}
