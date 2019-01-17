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

    int distanceFromVToF1;

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


            ArrayList<Node> nodes = new ArrayList<Node>();
            for (Iterator<Node> it = v.getNeighborNodeIterator(); it.hasNext(); ) {
                nodes.add(it.next());
            }
//            this.distanceFromVToF1 = calculateDistanceFromVToF1(nodes);
            int x = ((Geom) v.getAttribute("geom")).getX();
            int y = ((Geom) v.getAttribute("geom")).getY();

            Node fn = createHyperEdge(graph, Label.FN, x, y +distanceFromVToF1);
            Node fe = createHyperEdge(graph, Label.FE, x + distanceFromVToF1, y);
            Node fw = createHyperEdge(graph, Label.FW, x - distanceFromVToF1, y);
            Node fs = createHyperEdge(graph, Label.FS, x, y - distanceFromVToF1);
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
        int vx = x / 4;
        int vy = y / 4;
        Node v = createVertexToReplaceI(graph, nodeI, vx, vy);

        ArrayList<Node> nodes = new ArrayList<Node>();
        for (Iterator<Node> it = nodeI.getNeighborNodeIterator(); it.hasNext(); ) {
            nodes.add(it.next());
        }

        this.distanceFromVToF1 = calculateDistanceFromVToF1(nodes);



        Node newI1 = createHyperEdge(graph, Label.I, vx - distanceFromVToF1, vy+distanceFromVToF1);
        addEdge(graph, v, newI1);
        addEdge(graph, newI1, getTopLeftNode(nodes));

        Node newI2 = createHyperEdge(graph, Label.I, vx + distanceFromVToF1, vy+distanceFromVToF1);
        addEdge(graph, v, newI2);
        addEdge(graph, newI2, getTopRightNode(nodes));

        Node newI3 = createHyperEdge(graph, Label.I, vx - distanceFromVToF1, vy-distanceFromVToF1);
        addEdge(graph, v, newI3);
        addEdge(graph, newI3, getBottomLeftNode(nodes));

        Node newI4 = createHyperEdge(graph, Label.I, vx + distanceFromVToF1, vy-distanceFromVToF1);
        addEdge(graph, v, newI4);
        addEdge(graph, newI4, getBottomRightNode(nodes));

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

    private Node createHyperEdge(Graph graph, Label label, int x, int y) {
        return addNode(graph, Integer.toString(getNewMaxNodeId(graph)), new Geom(x, y),
                Type.HYPEREDGE, label, false, null, x, y);
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
        OptionalInt result = graph.getNodeSet().stream().map(n -> n.getId()).filter(id -> tryParseInt(id)).mapToInt(id -> Integer.valueOf(id)).max();
        if (result.isPresent()) {
            return result.getAsInt() + 1;
        } else {
            return 0;
        }
    }

    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    Positions getPositions(ArrayList<Node> nodes) {
        Positions pos = new Positions();
        pos.x1 = nodes.stream().mapToInt(n -> ((Geom) n.getAttribute("geom")).getX()).min().getAsInt();
        pos.x2 = nodes.stream().mapToInt(n -> ((Geom) n.getAttribute("geom")).getX()).max().getAsInt();
        pos.y1 = nodes.stream().mapToInt(n -> ((Geom) n.getAttribute("geom")).getY()).min().getAsInt();
        pos.y2 = nodes.stream().mapToInt(n -> ((Geom) n.getAttribute("geom")).getY()).max().getAsInt();

        return pos;
    }

    int calculateDistanceFromVToF1(ArrayList<Node> nodes) {
        Positions positions = getPositions(nodes);

        return ((positions.x2 - positions.x1) + (positions.y2 - positions.y1)) / 8;

    }

    Node getTopLeftNode(ArrayList<Node> nodes) {
        Positions positions = getPositions(nodes);
        return nodes.stream().filter(n -> ((Geom) n.getAttribute("geom")).getX() == positions.x1 && ((Geom) n.getAttribute("geom")).getY() == positions.y2).findFirst().get();
    }

    Node getTopRightNode(ArrayList<Node> nodes) {
        Positions positions = getPositions(nodes);
        return nodes.stream().filter(n -> ((Geom) n.getAttribute("geom")).getX() == positions.x2 && ((Geom) n.getAttribute("geom")).getY() == positions.y2).findFirst().get();
    }
    Node getBottomLeftNode(ArrayList<Node> nodes) {
        Positions positions = getPositions(nodes);
        return nodes.stream().filter(n -> ((Geom) n.getAttribute("geom")).getX() == positions.x1 && ((Geom) n.getAttribute("geom")).getY() == positions.y1).findFirst().get();
    }
    Node getBottomRightNode(ArrayList<Node> nodes) {
        Positions positions = getPositions(nodes);
        return nodes.stream().filter(n -> ((Geom) n.getAttribute("geom")).getX() == positions.x2 && ((Geom) n.getAttribute("geom")).getY() == positions.y1).findFirst().get();
    }


    class Positions {
        public int x1;
        public int x2;
        public int y1;
        public int y2;
    }


}
