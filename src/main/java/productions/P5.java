package productions;

import common.Type;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class P5 {

    private final P6 p6;

    public P5() {
        this.p6 = new P6(this);
    }

    public Graph run(Graph graph, BufferedImage img, Node node) {
        if (!verify(graph, img, node)) return graph;
        node.setAttribute("break", true);
        return p6.run(graph, img, node);
    }

    private boolean verify(Graph graph, BufferedImage img, Node node) {
        return node.getAttribute("type") == Type.HYPEREDGE;
    }
}
