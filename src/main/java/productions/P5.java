package productions;

import common.Type;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class P5 {

    private final P6 p6;

    @Autowired
    public P5(@Lazy P6 p6) {
        this.p6 = p6;
    }

    public Graph run(Graph graph, BufferedImage img, Node node) {
        if (verify(graph, img, node)) return graph;
        node.setAttribute("break", true);
        return p6.run(graph, img, node);
    }

    private boolean verify(Graph graph, BufferedImage img, Node node) {
        return node.getAttribute("type") == Type.HYPEREDGE;
    }
}
