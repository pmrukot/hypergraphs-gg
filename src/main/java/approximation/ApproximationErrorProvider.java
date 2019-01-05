package approximation;

import common.Geom;
import common.Type;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApproximationErrorProvider {

    public double calculateErrorForElement(Graph graph, BufferedImage image, Node node) throws Exception {
        if (node.getAttribute("type") != Type.HYPEREDGE) {
            throw new Exception("Not hyperedge");
        }
        List<Node> list = new ArrayList<>();
        node.getNeighborNodeIterator().forEachRemaining(list::add);
        List<Node> nodes = list.stream().filter(n -> n.getAttribute("type") == Type.VERTEX)
                .collect(Collectors.toList());
        return calculateErrorForElement(graph, image, nodes);
    }

    private double calculateErrorForElement(Graph graph, BufferedImage image, List<Node> nodes) {
        assert nodes.size() == 4;

        nodes.sort((n1, n2) -> {
            Geom n1Geom = n1.getAttribute("geom");
            Geom n2Geom = n2.getAttribute("geom");
            int vDelta = n2Geom.getY() - n1Geom.getY();
            if (vDelta == 0) {
                return n1Geom.getX() - n2Geom.getX();
            }
            return vDelta;
        });

        Node node1 = nodes.get(0);
        Geom node1Geom = node1.getAttribute("geom");
        Node node2 = nodes.get(1);
        Geom node2Geom = node2.getAttribute("geom");
        Node node3 = nodes.get(2);
        Geom node3Geom = node3.getAttribute("geom");
        Node node4 = nodes.get(3);
        Geom node4Geom = node4.getAttribute("geom");

        int xLength = node2Geom.getX() - node1Geom.getX();
        int yLength = node1Geom.getY() - node3Geom.getY();

        double error = 0.0;
        for (int x = node1Geom.getX(); x <= node2Geom.getX(); x++) {
            for (int y = node3Geom.getY(); y <= node1Geom.getY(); y++) {
                Color color = getColor(image, x, y);

                // diff array is not needed because everything can be calculated "in place" per pixel
                double diff_r = color.getRed();
                double diff_g = color.getGreen();
                double diff_b = color.getBlue();

                double fx = ((double)x - node1Geom.getX()) / xLength;
                double nfx = 1 - fx;
                double fy = ((double)(y - node3Geom.getY())) / yLength;
                double nfy = 1 - fy;

                diff_r -= getColor(image, node1Geom).getRed() * nfx * fy;
                diff_g -= getColor(image, node1Geom).getGreen() * nfx * fy;
                diff_b -= getColor(image, node1Geom).getBlue() * nfx * fy;

                diff_r -= getColor(image, node2Geom).getRed() * fx * fy;
                diff_g -= getColor(image, node2Geom).getGreen() * fx * fy;
                diff_b -= getColor(image, node2Geom).getBlue() * fx * fy;

                diff_r -= getColor(image, node3Geom).getRed() * nfx * nfy;
                diff_g -= getColor(image, node3Geom).getGreen() * nfx * nfy;
                diff_b -= getColor(image, node3Geom).getBlue() * nfx * nfy;

                diff_r -= getColor(image, node4Geom).getRed() * fx * nfy;
                diff_g -= getColor(image, node4Geom).getGreen() * fx * nfy;
                diff_b -= getColor(image, node4Geom).getBlue() * fx * nfy;

                error += 0.5 * Math.pow(diff_r, 2) +
                        0.3 * Math.pow(diff_g, 2) +
                        0.2 * Math.pow(diff_b, 2);
            }
        }
        return error;
    }

    private Color getColor(BufferedImage img, int x, int y) {
        return new Color(img.getRGB(x, y));
    }

    private Color getColor(BufferedImage img, Geom geom) {
        return new Color(img.getRGB(geom.getX(), geom.getY()));
    }
}
