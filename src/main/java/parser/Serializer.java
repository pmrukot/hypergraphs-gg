package parser;

import common.Geom;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.springframework.stereotype.Service;
import productions.P1;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Service
public class Serializer {

    public void serializeP1(BufferedWriter out, BufferedImage image) throws IOException {
        int x1 = 0;
        int x2 = image.getWidth() - 1;
        int y1 = 0;
        int y2 = image.getHeight() - 1;
        Color c1 = getColor(image, new Geom(x1, y1));
        Color c2 = getColor(image, new Geom(x2, y1));
        Color c3 = getColor(image, new Geom(x1, y2));
        Color c4 = getColor(image, new Geom(x2, y2));

        out.write("1");
        out.newLine();
        out.write(String.format("%d,%d,%d,%d", x1, y1, x2, y2));
        out.newLine();
        out.write(String.format("%s,%s,%s,%s", colorToString(c1), colorToString(c2), colorToString(c3), colorToString(c4)));
        out.newLine();
    }

    public void serializeP2(BufferedWriter out, Node nodeI) throws IOException {

        Node[] sortedNodes = StreamSupport.<Node>stream(
                Spliterators.spliteratorUnknownSize(
                        nodeI.getNeighborNodeIterator(),
                        Spliterator.ORDERED
                ),
                false
        ).sorted((l, r) -> {
            Geom left = l.getAttribute("geom");
            Geom right = r.getAttribute("geom");
            return left.getY() - right.getY();
        }).sorted((l, r) -> {
            Geom left = l.getAttribute("geom");
            Geom right = r.getAttribute("geom");
            return left.getX() - right.getX();
        }).toArray(Node[]::new);

        int x1 = xOf(sortedNodes[0]);
        int y1 = yOf(sortedNodes[0]);
        int x2 = xOf(sortedNodes[3]);
        int y2 = yOf(sortedNodes[3]);
        Color c = nodeI.getAttribute("rgb");

        out.write("2");
        printSimple(out, x1, x2, y1, y2, c);
    }

    public void serializeP4(BufferedWriter out, BufferedImage img, Node nodeFN, Node nodeFW, Node nodeFE) throws IOException {
        Node[] fnNeighbours =
                StreamSupport.stream(
                        Spliterators.<Node>spliteratorUnknownSize(nodeFN.getNeighborNodeIterator(), Spliterator.ORDERED),
                        false
                ).sorted(Comparator.comparing(n -> n.<Geom>getAttribute("geom").getY())).toArray(Node[]::new);

        Node upperNode = fnNeighbours[0];
        Node lowerNode = fnNeighbours[1];
        Node eastNode = nodeFE.getNeighborNodeIterator().next();
        Node westNode = nodeFW.getNeighborNodeIterator().next();

        int x1 = xOf(westNode);
        int x2 = xOf(eastNode);

        int y1 = yOf(upperNode);
        int y2 = yOf(lowerNode);

        Geom fnGeom = nodeFN.getAttribute("geom");
        Color color = getColor(img, fnGeom);

        out.write("4");
        printSimple(out, x1, x2, y1, y2, color);
    }

    private void printSimple(BufferedWriter out, int x1, int x2, int y1, int y2, Color color) throws IOException {
        out.newLine();
        out.write(String.format("%d,%d,%d,%d", x1, y1, x2, y2));
        out.newLine();
        out.write(colorToString(color));
        out.newLine();
    }

    private int xOf(Node node) {
        return node.<Geom>getAttribute("geom").getX();
    }

    private int yOf(Node node) {
        return node.<Geom>getAttribute("geom").getY();
    }

    private Color getColor(BufferedImage img, Geom geom) {
        return new Color(img.getRGB(geom.getX(),geom.getY()));
    }

    private static String colorToString(Color color) {
        return String.format("%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue());
    }


}
