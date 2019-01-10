package productions;

import common.Geom;
import common.Label;
import common.Type;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.OptionalInt;

import static common.Label.V;

@Service
public class P2 {

    @Autowired
    private P1 p1;

    public Graph prepareTestGraph(BufferedImage img) {
        Graph graph = p1.run(img);
        Node nodeI = graph.getNodeSet().stream().filter(node -> node.hasAttribute("label") && node.getAttribute("label").toString().equals(Label.I.toString())).findFirst().get();
        nodeI.setAttribute("break", true);
        return graph;
    }

    private boolean verifyNodes(Graph graph, Node nodeI) {
        Boolean breakAttribute = nodeI.getAttribute("break");

        if (nodeI == null || !breakAttribute) {
            return false;
        }
        Iterator<Node> iterator = nodeI.getNeighborNodeIterator();
        ArrayList<Node> neighbours = new ArrayList<>();

        while (iterator.hasNext()) {
            neighbours.add(iterator.next());
        }

        if (neighbours.size() != 4) {
            return false;
        }
        return true;
    }

    public Graph run(Graph graph, BufferedImage img, Node nodeI) {

        if (verifyNodes(graph, nodeI)) {

            Node v = replaceI(graph, nodeI);
            Node fn = createHyperEdge(graph, Label.FN);
            Node fe = createHyperEdge(graph, Label.FE);
            Node fw = createHyperEdge(graph, Label.FW);
            Node fs = createHyperEdge(graph, Label.FS);
            addEdge(graph, v, fn);
            addEdge(graph, v, fe);
            addEdge(graph, v, fw);
            addEdge(graph, v, fs);
            graph.removeNode(nodeI);

        }
        return graph;
    }

    private Node replaceI(Graph graph, Node nodeI) {
        int x = 0;
        int y = 0;
        for (Iterator<Node> it = nodeI.getNeighborNodeIterator(); it.hasNext(); ) {
            Node neighbour = it.next();
            x += ((Geom) neighbour.getAttribute("geom")).getX();
            y += ((Geom) neighbour.getAttribute("geom")).getY();

        }
        Node v = createVertexToReplaceI(graph, nodeI, x / 4, y / 4);
        for (Iterator<Node> it = nodeI.getNeighborNodeIterator(); it.hasNext(); ) {
            Node neighbour = it.next();
            // graph.removeEdge(nodeI, neighbour); not needed, as we remove the I node later
            Node newI = createHyperEdge(graph, Label.I);
            addEdge(graph, v, newI);
            addEdge(graph, newI, neighbour);
        }
        return v;
    }

    private Node createVertexToReplaceI(Graph graph, Node nodeI, int x, int y) {
        return addNode(graph, Integer.toString(getNewMaxNodeId(graph)), new Geom(x, y),
                Type.VERTEX, V, false, nodeI.getAttribute("rgb"), x, y);
    }

    private Node createHyperEdge(Graph graph, Label label) {
        return addNode(graph, Integer.toString(getNewMaxNodeId(graph)),
                Type.HYPEREDGE, label, false);
    }


    private Color getColor(BufferedImage img, Geom geom) {
        return new Color(img.getRGB(geom.getX(), geom.getY()));
    }

    private Node addNode(Graph graph, String name, Type type, Label label, boolean isBreak) {
        Node node = graph.addNode(name);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("break", isBreak);
        return node;
    }


    private Node addNode(Graph graph, String name, Geom geom, Type type, Label label, boolean isBreak, Color rgb, int x, int y) {
        Node node = graph.addNode(name);
        node.setAttribute("geom", geom);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("break", isBreak);
        node.setAttribute("rgb", rgb);
        node.setAttribute("x", geom.getX());
        node.setAttribute("y", geom.getY());
        return node;
    }

    private void addEdge(Graph graph, Node source, Node target) {
        String name = source.getId() + "-" + target.getId();
        Edge edge = graph.addEdge(name, source, target);
    }

    private int getNewMaxNodeId(Graph graph) {
        OptionalInt result = graph.getNodeSet().stream().mapToInt(n -> Integer.parseInt(n.getId())).max();
        if (result.isPresent()) {
            return result.getAsInt() + 1;
        } else {
            return 0;
        }
    }


}
