package productions;

import common.*;
import common.Label;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class P1 {

    public Graph run(int[] coordinates, int[] rgb) {
        int x1 = coordinates[0];
        int y1 = coordinates[1];
        int x2 = coordinates[2];
        int y2 = coordinates[3];
        Geom topLeft = new Geom(x1,y2);
        Geom bottomLeft = new Geom(x1, y1);
        Geom topRight = new Geom(x2, y2);
        Geom bottomRight = new Geom(x2, y1);

        Graph graph = new SingleGraph("graph");

        addNode(graph, "1", topLeft, Type.VERTEX, Label.V, false, new Color(rgb[0], rgb[1], rgb[2]));
        addNode(graph, "2", topRight, Type.VERTEX, Label.V, false, new Color(rgb[3], rgb[4], rgb[5]));
        addNode(graph, "3", bottomLeft, Type.VERTEX, Label.V, false, new Color(rgb[6], rgb[7], rgb[8]));
        addNode(graph, "4", bottomRight, Type.VERTEX, Label.V, false, new Color(rgb[9], rgb[10], rgb[11]));
        addNode(graph, "5", Type.HYPEREDGE, Label.I, false);

        addBorderEdge(graph, "1", "2");
        addBorderEdge(graph, "2", "4");
        addBorderEdge(graph, "4", "3");
        addBorderEdge(graph, "3", "1");
        addEdge(graph, "1", "5");
        addEdge(graph, "2", "5");
        addEdge(graph, "4", "5");
        addEdge(graph, "3", "5");

        return graph;
    }

    public Graph run(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        Geom topLeft = new Geom(0,height-1);
        Geom bottomLeft = new Geom(0,0);
        Geom topRight = new Geom(width - 1,height -1);
        Geom bottomRight = new Geom(width -1,0);

        Graph graph = new SingleGraph("graph");
        addNode(graph, "1", topLeft, Type.VERTEX, Label.V, false, getColor(img, topLeft));
        addNode(graph, "2", topRight, Type.VERTEX, Label.V, false, getColor(img, topRight));
        addNode(graph, "3", bottomLeft, Type.VERTEX, Label.V, false, getColor(img, bottomLeft));
        addNode(graph, "4", bottomRight, Type.VERTEX, Label.V, false,getColor(img, bottomRight));
        addNode(graph, "5", Type.HYPEREDGE, Label.I, false);

        addBorderEdge(graph, "1", "2");
        addBorderEdge(graph, "2", "4");
        addBorderEdge(graph, "4", "3");
        addBorderEdge(graph, "3", "1");
        addEdge(graph, "1", "5");
        addEdge(graph, "2", "5");
        addEdge(graph, "4", "5");
        addEdge(graph, "3", "5");

        return graph;
    }

    private Color getColor(BufferedImage img, Geom geom) {
        return new Color(img.getRGB(geom.getX(),geom.getY()));
    }

    private void addNode(Graph graph, String name, Type type, Label label, boolean isBreak) {
        Node node = graph.addNode(name);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("break", isBreak);
    }


    private void addNode(Graph graph, String name, Geom geom, Type type, Label label, boolean isBreak, Color rgb) {
        Node node = graph.addNode(name);
        node.setAttribute("geom", geom);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("break", isBreak);
        node.setAttribute("rgb", rgb);
    }

    private void addEdge(Graph graph, String sourceName, String targetName) {
        String name = sourceName + "-" + targetName;
        Edge edge = graph.addEdge(name, sourceName, targetName);
    }
    private void addBorderEdge(Graph graph, String sourceName, String targetName) {
        String name = sourceName + "-" + targetName;
        Edge edge = graph.addEdge(name, sourceName, targetName);
        edge.addAttribute("label", Label.B);
    }
}
