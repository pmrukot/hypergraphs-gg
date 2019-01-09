package productions;


import common.Geom;
import common.Label;
import common.Type;
import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
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

    private void addEdge(Graph graph, Node sourceName, Node targetName) {
        String name = sourceName.getId() + "-" + targetName.getId();
        graph.addEdge(name, sourceName, targetName);
    }

    private void addBorderEdge(Graph graph, Node sourceName, Node targetName) {
        String name = sourceName.getId() + "-" + targetName.getId();
        Edge edge = graph.addEdge(name, sourceName, targetName);
        edge.addAttribute("label", Label.B);
    }
}
