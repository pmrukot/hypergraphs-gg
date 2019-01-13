package productions;

import common.Geom;
import common.Label;
import common.Type;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.*;

@Service
public class P6 {

    private final P5 p5;

    @Autowired
    public P6(P5 p5) {
        this.p5 = p5;
    }


    public Graph run(Graph graph, BufferedImage img, Node node) {
        if (!verify(graph, img, node)) return graph;

        ArrayList<Node> neighbourList = getNeighbours(node);
        if (neighbourList.size() != 2 && neighbourList.size() != 3) {
            return graph;
        }
        int[] corners = getCorners(neighbourList);
        double sourceDiagonal = calculateDiagonal(corners);
        HashMap<String, Node> cornersMap = assignNodesToCorners(neighbourList, corners);

        cornersMap.forEach((corner, sourceNeighbour) -> getINodes(sourceNeighbour).forEach(iNode -> {
            ArrayList<Node> iNodeNeighbourList = getNeighbours(iNode);
            if (checkIfTileIsDiagonal(corner, sourceNeighbour, iNodeNeighbourList)) return;
            if (calculateDiagonal(getCorners(iNodeNeighbourList)) > sourceDiagonal) {
                p5.run(graph, img, iNode);
            }
        }));

        return graph;
    }

    private boolean checkIfTileIsDiagonal(String corner, Node sourceNode, ArrayList<Node> iNodeNeighbourList) {
        Geom sourceGeom = mapNodeToGeom(sourceNode);
        switch (corner) {
            case "LD":
                return iNodeNeighbourList.stream().map(this::mapNodeToGeom).anyMatch(x -> x.getX() < sourceGeom.getX() && x.getY() < sourceGeom.getY());
            case "LU":
                return iNodeNeighbourList.stream().map(this::mapNodeToGeom).anyMatch(x -> x.getX() < sourceGeom.getX() && x.getY() > sourceGeom.getY());
            case "RD":
                return iNodeNeighbourList.stream().map(this::mapNodeToGeom).anyMatch(x -> x.getX() > sourceGeom.getX() && x.getY() < sourceGeom.getY());
            case "RU":
                return iNodeNeighbourList.stream().map(this::mapNodeToGeom).anyMatch(x -> x.getX() > sourceGeom.getX() && x.getY() > sourceGeom.getY());
            default:
                return false;
        }
    }

    private Geom mapNodeToGeom(Node node) {
        return (Geom) node.getAttribute("geom");
    }

    private HashMap<String, Node> assignNodesToCorners(ArrayList<Node> neighbourList, int[] corners) {
        HashMap<String, Node> map = new HashMap<>();
        neighbourList.forEach(x ->
                map.put((((Geom) x.getAttribute("geom")).getX() == corners[0] ? "L" : "R") +
                                (((Geom) x.getAttribute("geom")).getY() == corners[1] ? "D" : "U")
                        , x)
        );
        return map;
    }

    private double calculateDiagonal(int[] corners) {
        return (corners[0] - corners[2]) * (corners[0] - corners[2]) + (corners[1] - corners[3]) * (corners[1] - corners[3]);
    }

    private int[] getCorners(List<Node> nodeList) {
        //low x low y high x high y
        int[] corners = {Integer.MAX_VALUE, Integer.MAX_VALUE, -1, -1};
        nodeList.stream().map(x -> (Geom) x.getAttribute("geom")).forEach(x -> {
            if (x.getX() < corners[0]) corners[0] = x.getX();
            if (x.getY() < corners[1]) corners[1] = x.getY();
            if (x.getX() > corners[2]) corners[2] = x.getX();
            if (x.getY() > corners[3]) corners[3] = x.getY();
        });
        return corners;
    }

    private ArrayList<Node> getINodes(Node node) {
        Iterator<Node> nodeIterator = node.getNeighborNodeIterator();
        ArrayList<Node> iNodesList = new ArrayList<>();
        while (nodeIterator.hasNext()) {
            Node neighbour = nodeIterator.next();
            if (neighbour.getAttribute("label").equals(Label.I)) {
                iNodesList.add(neighbour);
            }
        }
        return iNodesList;
    }

    private ArrayList<Node> getNeighbours(Node sourceNode) {
        ArrayList<Node> neighboursList = new ArrayList<>();
        sourceNode.getNeighborNodeIterator().forEachRemaining(
                neighboursList::add
        );
        return neighboursList;
    }

    private boolean verify(Graph graph, BufferedImage img, Node node) {
        return (node.getAttribute("type") == Type.HYPEREDGE) && (boolean) node.getAttribute("break");
    }
}
