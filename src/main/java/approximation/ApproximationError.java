package approximation;

import common.Geom;
import common.Type;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class ApproximationError {

    private BufferedImage bitmap;
    private Graph graph;

    public ApproximationError(BufferedImage bitmap, Graph graph) {
        this.bitmap = bitmap;
        this.graph = graph;
    }

    public double compute(Node node) throws ApproximationErrorComputationException {
        if (!isHyperEdge(node)) {
            throw new ApproximationErrorComputationException("Node is not a hyperedge");
        }
        List<Node> neighbourVertices = getNeighbourVertices(node);
        sortNeighbourVertices(neighbourVertices);
        Node topLeftVertex = neighbourVertices.get(0);
        Node topRightVertex = neighbourVertices.get(1);
        Node bottomLeftVertex = neighbourVertices.get(2);
        Node bottomRightVertex = neighbourVertices.get(3);
        return compute(topLeftVertex, topRightVertex, bottomLeftVertex, bottomRightVertex);
    }

    private List<Node> getNeighbourVertices(Node hyperEdge) throws ApproximationErrorComputationException {
        Iterator<Node> iterator = hyperEdge.getNeighborNodeIterator();
        List<Node> neighbours = new ArrayList<>();
        while (iterator.hasNext()) {
            Node neighbour = iterator.next();
            if (isVertex(neighbour)) {
                neighbours.add(neighbour);
            }
        }
        if (neighbours.size() < 3 || neighbours.size() > 4) {
            String message = "Wrong number of neighbour vertices in hyper-edge:" + neighbours.size();
            throw new ApproximationErrorComputationException(message);
        }
        if (neighbours.size() == 3) {
            addMissingVertex(neighbours);
        }
        return neighbours;
    }

    private void addMissingVertex(List<Node> neighbours) throws ApproximationErrorComputationException {
        Map<Integer, Integer> xCounter = new HashMap<>();
        Map<Integer, Integer> yCounter = new HashMap<>();
        for (Node node : neighbours) {
            Geom geom = getVertexGeom(node);
            xCounter.put(geom.getX(), 1 + xCounter.getOrDefault(geom.getX(), 0));
            yCounter.put(geom.getY(), 1 + yCounter.getOrDefault(geom.getY(), 0));
        }

        int x = findCoordinateWith1Count(xCounter);
        int y = findCoordinateWith1Count(yCounter);

        if (x == -1 || y == -1) {
            throw new ApproximationErrorComputationException("couldn't find coordinate with count=1");
        }

        Node node = graph.addNode("missing vertex");
        Geom geom = new Geom(x, y);
        node.setAttribute("geom", geom);
        node.setAttribute("xy", x, y);
        node.setAttribute("rgb", getColor(x, y));
        neighbours.add(node);
    }

    private int findCoordinateWith1Count(Map<Integer, Integer> counts) {
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private void sortNeighbourVertices(List<Node> neighbourVertices) {
        neighbourVertices.sort((vertex1, vertex2) -> {
            int yDiff = getVertexGeom(vertex2).getY() - getVertexGeom(vertex1).getY();
            if (yDiff == 0) {
                return getVertexGeom(vertex1).getX() - getVertexGeom(vertex2).getX();
            }
            return yDiff;
        });
    }

    private double compute(Node topLeftVertex, Node topRightVertex, Node bottomLeftVertex, Node bottomRightVertex) {
        Color topLeftColor = getVertexColor(topLeftVertex);
        Color topRightColor = getVertexColor(topRightVertex);
        Color bottomRightColor = getVertexColor(bottomRightVertex);
        Color bottomLeftColor = getVertexColor(bottomLeftVertex);
        Geom topLeftGeom = getVertexGeom(topLeftVertex);
        Geom topRightGeom = getVertexGeom(topRightVertex);
        Geom bottomLeftGeom = getVertexGeom(bottomLeftVertex);
        int x1 = topLeftGeom.getX();
        int x2 = topRightGeom.getX();
        int y1 = bottomLeftGeom.getY();
        int y2 = topLeftGeom.getY();
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;

        double error = 0;
        for (int px = x1; px <= x2; px++) {
            for (int py = y1; py <= y2; py++) {

                Color color = getColor(px, py);
                double diffRed = color.getRed();
                double diffGreen = color.getGreen();
                double diffBlue = color.getBlue();

                double xRatio = (px - x1) / xDiff;
                double yRatio = (py - y1) / yDiff;

                diffRed -= topLeftColor.getRed() * (1 - xRatio) * yRatio;
                diffGreen -= topLeftColor.getGreen() * (1 - xRatio) * yRatio;
                diffBlue -= topLeftColor.getBlue() * (1 - xRatio) * yRatio;

                diffRed -= topRightColor.getRed() * xRatio * yRatio;
                diffGreen -= topRightColor.getGreen() * xRatio * yRatio;
                diffBlue -= topRightColor.getBlue() * xRatio * yRatio;

                diffRed -= bottomLeftColor.getRed() * (1 - xRatio) * (1 - yRatio);
                diffGreen -= bottomLeftColor.getGreen() * (1 - xRatio) * (1 - yRatio);
                diffBlue -= bottomLeftColor.getBlue() * (1 - xRatio) * (1 - yRatio);

                diffRed -= bottomRightColor.getRed() * xRatio * (1 - yRatio);
                diffGreen -= bottomRightColor.getGreen() * xRatio * (1 - yRatio);
                diffBlue -= bottomRightColor.getBlue() * xRatio * (1 - yRatio);

                error += 0.5 * square(diffRed) + 0.3 * square(diffGreen) + 0.2 * square(diffBlue);
            }
        }

        return error;
    }

    private boolean isHyperEdge(Node node) {
        return node.getAttribute("type") == Type.HYPEREDGE;
    }

    private boolean isVertex(Node node) {
        return node.getAttribute("type") == Type.VERTEX;
    }

    private Geom getVertexGeom(Node vertex) {
        return vertex.getAttribute("geom");
    }

    private Color getVertexColor(Node vertex) {
        return vertex.getAttribute("rgb");
    }

    private Color getColor(int x, int y) {
        return new Color(bitmap.getRGB(x, y));
    }

    private double square(double number) {
        return Math.pow(number, 2);
    }

}
