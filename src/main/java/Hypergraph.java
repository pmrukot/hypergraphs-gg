import com.sun.org.apache.xpath.internal.NodeSet;
import common.Label;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import productions.P1;
import productions.P2;
import productions.P3;
import productions.P4;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;


@ComponentScan({"productions"})
public class Hypergraph {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(Hypergraph.class);
        P1 p1 = context.getBean(P1.class);

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        BufferedImage img = ImageIO.read(Objects.requireNonNull(cl.getResourceAsStream("colors.jpg")));
        Graph graph = p1.run(img);
        graph.display();

        //P2
//        P2 p2 = context.getBean(P2.class);
//        Graph graphP2 = p2.prepareTestGraph(img);
//        Node nodeI =  graphP2.getNodeSet().stream().filter(node -> node.hasAttribute("label") && node.getAttribute("label").toString().equals(Label.I.toString())).findFirst().get();
//        Graph g2 = p2.run(graphP2, img, nodeI);
//        g2.display();

//        P3 p3 = context.getBean(P3.class);
//        Graph testGraph = p3.prepareTestGraph(img);
//        Edge edge = testGraph.getEdge("1-2");
//        Node f1 = testGraph.getNode("f1");
//        Graph g3 = p3.run(testGraph, img, edge, f1);
//        g3.display().disableAutoLayout();


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
