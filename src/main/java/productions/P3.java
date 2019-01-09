package productions;


import common.Geom;
import common.Label;
import common.Type;
import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class P3 {
    public Graph run(Graph graph, BufferedImage img, Edge border, Node nodeF) {
        java.util.List<Node> p1;
        java.util.List<Node> p2;
        Node n0;
        Node n1;
        if (border == null || !border.hasAttribute("label") || border.getAttribute("label") != Label.B ||
                nodeF == null || !nodeF.hasAttribute("label") || (nodeF.getAttribute("label") != Label.FN &&
                nodeF.getAttribute("label") != Label.FW && nodeF.getAttribute("label") != Label.FE &&
                nodeF.getAttribute("label") != Label.FS)) {
            return graph;
        }
        AStar aStar = new AStar(graph);
        n0 = border.getNode0();
        n1 = border.getNode1();
        aStar.compute(n0.getId(), nodeF.getId());
        p1 = aStar.getShortestPath().getNodePath();
        aStar.compute(n1.getId(), nodeF.getId());
        p2 = aStar.getShortestPath().getNodePath();
        if (p2.size() != 4 || p1.size() != 4 || p1.get(1).getAttribute("label") != Label.I ||
                p2.get(1).getAttribute("label") != Label.I)
            return graph;

        Geom g1 = n1.getAttribute("geom");
        Geom g0 = n0.getAttribute("geom");
        graph.removeEdge(border);
        int x = (g0.getX() + g1.getX()) / 2;
        int y = (g0.getY() + g1.getY()) / 2;
        Node v = addNode(graph, Integer.toString(graph.getNodeCount() + 1), new Geom(x, y),
                Type.VERTEX, Label.V, getColor(img, x, y));
        addBorderEdge(graph, v, n0);
        addBorderEdge(graph, v, n1);
        addEdge(graph, v, p1.get(1));
        addEdge(graph, v, p2.get(1));
        addEdge(graph, v, nodeF);
        return graph;
    }

    public Graph prepareTestGraph(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        Geom center = new Geom((width - 1) / 2, (height - 1));

        Graph graph = new SingleGraph(getClass().getName());
        addNode(graph, "1", new Geom(0, 0), Type.VERTEX, Label.T, false, getColor(img, new Geom(0, 0)));
        addNode(graph, "2", new Geom(width - 1, 0), Type.VERTEX, Label.T, false, getColor(img, new Geom(width - 1, 0)));
        addNode(graph, "3", center, Type.VERTEX, Label.T, false, getColor(img, center));

        Geom v4geom = new Geom((width - 1) / 4, (height - 1) / 2);
        Geom v5geom = new Geom(((width - 1) / 4) * 3, (height - 1) / 2);
        addNode(graph, "4", Type.HYPEREDGE, Label.I, v4geom);
        addNode(graph, "5", Type.HYPEREDGE, Label.I, v5geom);

        addNode(graph, "f1", Type.HYPEREDGE, Label.FN, new Geom(center.getX(), center.getY() / 2));

        addBorderEdge(graph, "1", "2");
        addEdge(graph, "1", "4");
        addEdge(graph, "4", "3");
        addEdge(graph, "3", "5");
        addEdge(graph, "5", "2");
        addEdge(graph, "f1", "3");
        return graph;
    }


    private Color getColor(BufferedImage img, int x, int y) {
        return new Color(img.getRGB(x, y));
    }

    private Node addNode(Graph graph, String name, Geom geom, Type type, Label label, Color rgb) {
        Node node = graph.addNode(name);
        node.setAttribute("geom", geom);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("rgb", rgb);
        node.setAttribute("xy", geom.getX(), geom.getY());
        return node;
    }

    private void addNode(Graph graph, String name, Geom geom, Type type, Label label, boolean isBreak, Color rgb) {
        Node node = graph.addNode(name);
        node.setAttribute("geom", geom);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("break", isBreak);
        node.setAttribute("rgb", rgb);
    }

    private void addNode(Graph graph, String name, Type type, Label label, Geom geom) {
        Node node = graph.addNode(name);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("geom", geom);
        node.setAttribute("xy", geom.getX(), geom.getY());
    }

    private void addEdge(Graph graph, Node sourceName, Node targetName) {
        String name = sourceName.getId() + "-" + targetName.getId();
        graph.addEdge(name, sourceName, targetName);
    }

    private void addEdge(Graph graph, String sourceName, String targetName) {
        String name = sourceName + "-" + targetName;
        graph.addEdge(name, sourceName, targetName);
    }

    private void addBorderEdge(Graph graph, Node sourceName, Node targetName) {
        String name = sourceName.getId() + "-" + targetName.getId();
        Edge edge = graph.addEdge(name, sourceName, targetName);
        edge.addAttribute("label", Label.B);
    }

    private void addBorderEdge(Graph graph, String sourceName, String targetName) {
        String name = sourceName + "-" + targetName;
        Edge edge = graph.addEdge(name, sourceName, targetName);
        edge.addAttribute("label", Label.B);
    }

    private Color getColor(BufferedImage img, Geom geom) {
        return new Color(img.getRGB(geom.getX(), geom.getY()));
    }

}
