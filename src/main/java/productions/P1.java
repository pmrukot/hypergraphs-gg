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
        Geom center = new Geom(width/2, height/2);

        Graph graph = new SingleGraph("graph");
        addNode(graph, "1", topLeft, Type.VERTEX, Label.V, false, getColor(img, topLeft));
        addNode(graph, "2", topRight, Type.VERTEX, Label.V, false, getColor(img, topRight));
        addNode(graph, "3", bottomLeft, Type.VERTEX, Label.V, false, getColor(img, bottomLeft));
        addNode(graph, "4", bottomRight, Type.VERTEX, Label.V, false,getColor(img, bottomRight));
        addNode(graph, "5", center, Type.HYPEREDGE, Label.I, false, null);

        addBorderEdge(graph, "1", "2", width/2, height-1);
        addBorderEdge(graph, "2", "4", width-1, height/2);
        addBorderEdge(graph, "4", "3", width/2, 0);
        addBorderEdge(graph, "3", "1", 0, height/2);
        addEdge(graph, "1", "5");
        addEdge(graph, "2", "5");
        addEdge(graph, "4", "5");
        addEdge(graph, "3", "5");

        return graph;
    }

    private Color getColor(BufferedImage img, Geom geom) {
        return new Color(img.getRGB(geom.getX(),geom.getY()));
    }


    private void addNode(Graph graph, String name, Geom geom, Type type, Label label, boolean isBreak, Color rgb) {
        Node node = graph.addNode(name);
        node.setAttribute("geom", geom);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("break", isBreak);
        node.setAttribute("x", geom.getX());
        node.setAttribute("y", geom.getY());

        if (rgb != null) {
            node.setAttribute("rgb", rgb);
        }

        String uiName = "geom = (" + geom.getX() + "," + geom.getY() + ")" + printColor(rgb) + " : " + name;
        node.addAttribute("ui.label", uiName);
    }

    private String printColor(Color rgb) {
        return rgb != null ? " : rgb=(" + rgb.getRed() + "," + rgb.getGreen() + "," + rgb.getBlue() + ")" : "";
    }

    private void addEdge(Graph graph, String sourceName, String targetName) {
        String name = sourceName + "-" + targetName;
        graph.addEdge(name, sourceName, targetName);
    }
    private void addBorderEdge(Graph graph, String sourceName, String targetName, int x, int y) {
        String nodeName = "B" +  sourceName + "-" + targetName;
        Node node = graph.addNode(nodeName);
        node.addAttribute("label", Label.B);
        node.setAttribute("type", Type.HYPEREDGE);
        node.setAttribute("x", x);
        node.setAttribute("y", y);

        graph.addEdge(sourceName + "-" + nodeName, sourceName, nodeName);
        graph.addEdge(nodeName + "-" + targetName, nodeName, targetName);
    }
}
