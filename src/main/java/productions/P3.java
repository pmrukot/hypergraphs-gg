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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

@Service
public class P3 {
    public Graph run(Graph graph, BufferedImage img, Node border) {
        java.util.List<Node> path;
        Node n0;
        Node n1;
        Label testLabel;
        if (border == null || !border.hasAttribute("label") || border.getAttribute("label") != Label.B) {
            return graph;
        }
        int borderGeomX = border.getAttribute("x");
        int borderGeomY = border.getAttribute("y");
        AStar aStar = new AStar(graph);
        Iterator<Node> neighborNodeIterator = border.getNeighborNodeIterator();

        n0 = neighborNodeIterator.next();
        n1 = neighborNodeIterator.next();
        graph.removeNode(border);
        aStar.compute(n0.getId(), n1.getId());
        path = aStar.getShortestPath().getNodePath();
        if (path.size() != 5) {
            repair(graph, n0, n1);
            return graph;
        }
        Node n3 = path.get(2);
        int width = img.getWidth() - 1;
        int height = img.getHeight() - 1;
        if (borderGeomX == width)
            testLabel = Label.FE;
        else if (borderGeomX == 0 ) {
            testLabel = Label.FW;
        } else if (borderGeomY == 0) {
            testLabel = Label.FS;
        } else if (borderGeomY == height) {
            testLabel = Label.FN;
        } else {
            repair(graph, n0, n1);
            return graph;
        }
        ArrayList<Node> nodes = new ArrayList<>();
        n3.getNeighborNodeIterator().forEachRemaining(nodes::add);
        Label finalTestLabel = testLabel;
        Optional<Node> nodeFOpt = nodes.stream().filter(n -> n.getAttribute("label") == finalTestLabel).findFirst();
        if (!nodeFOpt.isPresent()) {
            repair(graph, n0, n1);
            return graph;
        }
        Node nodeF = nodeFOpt.get();
        Geom g1 = n1.getAttribute("geom");
        Geom g0 = n0.getAttribute("geom");
        int x = (g0.getX() + g1.getX()) / 2;
        int y = (g0.getY() + g1.getY()) / 2;
        Node v = addNode(graph, Integer.toString(graph.getNodeCount() + 1), new Geom(x, y),
                Type.VERTEX, Label.V, getColor(img, x, y));
        addBorderEdge(graph, v.getId(), n0.getId(), (g0.getX() + x) / 2, (g0.getY() + y) / 2);
        addBorderEdge(graph, v.getId(), n1.getId(), (g1.getX() + x) / 2, (g1.getY() + y) / 2);

        addEdge(graph, v, path.get(1));
        addEdge(graph, v, path.get(3));
        addEdge(graph, v, nodeF);
        return graph;
    }

    private void repair(Graph graph, Node n0, Node n1) {
        Geom n0geom = n0.getAttribute("geom");
        Geom n1geom = n1.getAttribute("geom");
        addBorderEdge(graph, n0.getId(), n1.getId(), (n0geom.getX() + n1geom.getX()) / 2, (n0geom.getY() + n1geom.getY()) / 2);
    }

    public Graph prepareTestGraph(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        Geom center = new Geom((width - 1) / 2, 0);

        Graph graph = new SingleGraph(getClass().getName());
        addNode(graph, "1", new Geom(0, height - 1), Type.VERTEX, Label.T, false, getColor(img, new Geom(0, 0)));
        addNode(graph, "2", new Geom(width - 1, height - 1), Type.VERTEX, Label.T, false, getColor(img, new Geom(width - 1, 0)));
        addNode(graph, "3", center, Type.VERTEX, Label.T, false, getColor(img, center));

        Geom v4geom = new Geom((width - 1) / 4, (height - 1) / 2);
        Geom v5geom = new Geom(((width - 1) / 4) * 3, (height - 1) / 2);
        addNode(graph, "4", Type.HYPEREDGE, Label.I, v4geom);
        addNode(graph, "5", Type.HYPEREDGE, Label.I, v5geom);

        addNode(graph, "f1", Type.HYPEREDGE, Label.FN, new Geom(center.getX(), (height - 1) / 2));

        addBorderEdge(graph, "1", "2", (width - 1) / 2, (height - 1));
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
        node.setAttribute("xy", geom.getX(), geom.getY());
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

    private void addBorderEdge(Graph graph, String sourceName, String targetName, int x, int y) {
        String nodeName = "B" + sourceName + "-" + targetName;
        Node node = graph.addNode(nodeName);
        node.addAttribute("label", Label.B);
        node.setAttribute("type", Type.HYPEREDGE);
        node.setAttribute("x", x);
        node.setAttribute("y", y);
        node.setAttribute("xy", x, y);

        graph.addEdge(sourceName + "-" + nodeName, sourceName, nodeName);
        graph.addEdge(nodeName + "-" + targetName, nodeName, targetName);
    }

    private Color getColor(BufferedImage img, Geom geom) {
        return new Color(img.getRGB(geom.getX(), geom.getY()));
    }

}
