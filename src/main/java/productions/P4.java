package productions;

import common.Geom;
import common.Label;
import common.Type;

import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

@Service
public class P4 {

    public Graph prepareTestGraph(BufferedImage img) {
        Graph graph = new SingleGraph("P4Test");

        int width = img.getWidth();
        int height = img.getHeight();
        Geom center = new Geom((width-1)/2, (height-1)/2);

        // TODO: Change name to UUID?
        addNode(graph, "1", Type.HYPEREDGE, Label.I, new Geom(center.getX()-7, center.getY()+7));
        addNode(graph, "2", Type.HYPEREDGE, Label.I, new Geom(center.getX()+7, center.getY()+7));
        addNode(graph, "3", Type.HYPEREDGE, Label.I, new Geom(center.getX()-7, center.getY()-7));
        addNode(graph, "4", Type.HYPEREDGE, Label.I, new Geom(center.getX()+7, center.getY()-7));

        // Geom is just for test, should not change during production
        addNode(graph, "5", Type.VERTEX, Label.T, new Geom(center.getX(), center.getY()+10), new Color(0,0,0));
        addNode(graph, "6", Type.VERTEX, Label.T, new Geom(center.getX()+10, center.getY()), new Color(0,0,0));
        addNode(graph, "7", Type.VERTEX, Label.T, new Geom(center.getX(), center.getY()-10), new Color(0,0,0));
        addNode(graph, "8", Type.VERTEX, Label.T, new Geom(center.getX()-10, center.getY()), new Color(0,0,0));

        addNode(graph, "9", Type.HYPEREDGE, Label.FE, new Geom(center.getX()+7, center.getY()));
        addNode(graph, "10", Type.HYPEREDGE, Label.FW, new Geom(center.getX()-7, center.getY()));

        addNode(graph, "11", Type.HYPEREDGE, Label.FN, center);

        addEdge(graph,"1", "5");
        addEdge(graph,"5", "2");
        addEdge(graph,"2", "6");
        addEdge(graph,"6", "4");
        addEdge(graph,"4", "7");
        addEdge(graph,"7", "3");
        addEdge(graph,"3", "8");
        addEdge(graph,"8", "1");

        addEdge(graph,"6", "9");
        addEdge(graph,"8", "10");

        addEdge(graph,"5", "11");
        addEdge(graph,"7", "11");

        return graph;
    }

    private boolean verifyNodes(Graph graph, Node nodeFN, Node nodeFW, Node nodeFE) {
        if (nodeFN == null || nodeFW == null || nodeFE == null) {
            System.out.println("One ore more of the input nodes are null");
            return false;
        }

        if(nodeFN.getAttribute("label") != Label.FN && nodeFN.getAttribute("label") != Label.FS){
            return false;
        }

        AStar aStar = new AStar(graph);
        aStar.compute(nodeFN.getId(), nodeFW.getId());
        Path NWpath = aStar.getShortestPath();
        if (NWpath.getNodeCount() != 5) {
            return false;
        }
        aStar.compute(nodeFN.getId(), nodeFE.getId());
        Path NEpath = aStar.getShortestPath();
        if (NEpath.getNodeCount() != 5) {
            return false;
        }
        aStar.compute(nodeFW.getId(), nodeFE.getId());
        Path WEpath = aStar.getShortestPath();
        if (WEpath.getNodeCount() != 7) {
            return false;
        }

        Geom nodeFWgeom = nodeFW.getAttribute("geom");
        Geom nodeFEgeom = nodeFE.getAttribute("geom");

        return true;
    }

    private Node getLowerNode(Graph graph, Node node){
        Edge firstEdge = node.getEdge(0);
        Node firstNode = firstEdge.getNode0();
        firstNode = firstNode!=node?firstNode:firstEdge.getNode1();

        Edge secondEdge = node.getEdge(1);
        Node secondNode = secondEdge.getNode0();
        secondNode = secondNode!=node?secondNode:secondEdge.getNode1();

        if(((Geom)secondNode.getAttribute("geom")).getY()<((Geom)firstNode.getAttribute("geom")).getY()){
            return secondNode;
        }
        else{
            return firstNode;
        }
    }

    private Node removeLowerEdgeAndReturnLowerNode(Graph graph, Node node){
        Edge firstEdge = node.getEdge(0);
        Node firstNode = firstEdge.getNode0();
        firstNode = firstNode!=node?firstNode:firstEdge.getNode1();

        Edge secondEdge = node.getEdge(1);
        Node secondNode = secondEdge.getNode0();
        secondNode = secondNode!=node?secondNode:secondEdge.getNode1();

        if(((Geom)secondNode.getAttribute("geom")).getY()<((Geom)firstNode.getAttribute("geom")).getY()){
            graph.removeEdge(secondEdge);
            return secondNode;
        }
        else{
            graph.removeEdge(firstEdge);
            return firstNode;
        }
    }

    public Graph run(Graph graph, BufferedImage img, Node nodeFN) {
        Node nodeFE = null;
        Node nodeFW = null;
        Node lowerNode;
        try {
            lowerNode = getLowerNode(graph, nodeFN);
        } catch (IndexOutOfBoundsException e) {
            return graph;
        }

        if(lowerNode == null){
            return graph;
        }

        Iterator<Edge> edgeIterator = nodeFN.getEachEdge().iterator();
        Node top = edgeIterator.next().getOpposite(nodeFN);
        if(top == lowerNode){
            top = edgeIterator.next().getOpposite(nodeFN);
        }
        if(top == null) {
            return graph;
        }

        graph.removeEdge(nodeFN, lowerNode);

        AStar aStar = new AStar(graph);
        aStar.compute(top.getId(), lowerNode.getId());
        if(aStar.noPathFound()){
            addEdge(graph, nodeFN.getId(), lowerNode.getId());
            return graph;
        }

        Path path = aStar.getShortestPath();
        List<Node> nodesOnPath = path.getNodePath();
        if(nodesOnPath.size() != 5){
            addEdge(graph, nodeFN.getId(), lowerNode.getId());
            return graph;
        }

        addEdge(graph, nodeFN.getId(), lowerNode.getId());

        Node nodeNextToFEorFW1 = nodesOnPath.get(2);

        Iterator<Node> nodeIterator = nodeNextToFEorFW1.getNeighborNodeIterator();
        while(nodeIterator.hasNext()){
            Node node = nodeIterator.next();
            if(!nodesOnPath.contains(node) && ((HasSmallerXThan(node, nodeNextToFEorFW1) && HasSmallerXThan(nodeFN, node)) || (HasSmallerXThan(nodeNextToFEorFW1, node) && HasSmallerXThan(node, nodeFN)))){
                if(HasSmallerXThan(node, nodeFN)){
                    nodeFW = node;
                }
                else {
                    nodeFE = node;
                }
                break;
            }
        }
        if(nodeFE == null && nodeFW == null){
            return graph;
        }


        Node temp = nodesOnPath.get(1);

        graph.removeEdge(top.getId(), temp.getId());

        graph.removeEdge(nodeFN, lowerNode);
        aStar = new AStar(graph);
        aStar.compute(top.getId(), lowerNode.getId());
        if(aStar.noPathFound()){
            addEdge(graph, top.getId(), temp.getId());
            addEdge(graph, nodeFN.getId(), lowerNode.getId());
            return graph;
        }

        path = aStar.getShortestPath();
        nodesOnPath = path.getNodePath();
        if(nodesOnPath.size() != 5){
            addEdge(graph, top.getId(), temp.getId());
            addEdge(graph, nodeFN.getId(), lowerNode.getId());
            return graph;
        }




        Node nodeNextToFEorFW2 = nodesOnPath.get(2);
        nodeIterator = nodeNextToFEorFW2.getNeighborNodeIterator();
        while(nodeIterator.hasNext()){
            Node node = nodeIterator.next();
            if(!nodesOnPath.contains(node) && ((HasSmallerXThan(node, nodeNextToFEorFW2) && HasSmallerXThan(nodeFN, node)) || (HasSmallerXThan(nodeNextToFEorFW2, node) && HasSmallerXThan(node, nodeFN)))){
                if(HasSmallerXThan(node, nodeFN)){
                    nodeFW = node;
                }
                else {
                    nodeFE = node;
                }
                break;
            }
        }
        addEdge(graph, top.getId(), temp.getId());
        if(nodeFE == null || nodeFW == null){
            addEdge(graph, nodeFN.getId(), lowerNode.getId());
            return graph;
        }
        addEdge(graph, nodeFN.getId(), lowerNode.getId());
        if(verifyNodes(graph, nodeFN, nodeFW, nodeFE)) {

            Node upperNode = top;


            Geom oldGeom = nodeFN.getAttribute("geom");

            Geom nodeUnderFnGeom = lowerNode.getAttribute("geom");
            Geom nodeAboveFnGeom = upperNode.getAttribute("geom");

            Geom fnNewGeom;

            Iterator<Node> iterator = nodeFN.getNeighborNodeIterator();
            List<Node> oldNeighbors = new ArrayList<>();
            while(iterator.hasNext()){
                oldNeighbors.add(iterator.next());
            }

            graph.removeNode(nodeFN);

            String oldId = String.valueOf(getNewMaxNodeId(graph));
            if(nodeFN.getAttribute("label") == Label.FN){
                addNode(graph, oldId, Type.HYPEREDGE, Label.FN, new Geom(oldGeom.getX(), nodeAboveFnGeom.getY()- (nodeAboveFnGeom.getY()-nodeUnderFnGeom.getY())/8));
            }
            else{
                addNode(graph, oldId, Type.HYPEREDGE, Label.FS, new Geom(oldGeom.getX(), nodeUnderFnGeom.getY() + (nodeAboveFnGeom.getY()-nodeUnderFnGeom.getY())/8));
            }

            oldNeighbors.forEach(node -> addEdge(graph, node.getId(), oldId));

            String newId = String.valueOf(getNewMaxNodeId(graph));
            if(nodeFN.getAttribute("label") == Label.FN){
                addNode(graph, newId, Type.HYPEREDGE, Label.FS, new Geom(oldGeom.getX(), nodeUnderFnGeom.getY() + (nodeAboveFnGeom.getY()-nodeUnderFnGeom.getY())/8));
                addEdge(graph, newId, lowerNode.getId());
            }
            else{
                addNode(graph, newId, Type.HYPEREDGE, Label.FN, new Geom(oldGeom.getX(), nodeAboveFnGeom.getY()- (nodeAboveFnGeom.getY()-nodeUnderFnGeom.getY())/8));
                addEdge(graph, newId, upperNode.getId());
            }



            Geom middle = new Geom(nodeAboveFnGeom.getX(), nodeUnderFnGeom.getY()+(nodeAboveFnGeom.getY()-nodeUnderFnGeom.getY())/2);
            Color color = getColor(img, middle);
            Node vNode = addNode(graph, String.valueOf(getNewMaxNodeId(graph)), Type.HYPEREDGE, Label.V, middle, color);

            vNode.addAttribute("ui.label", "V: " + color.getRed() + " " + color.getGreen() + " " + color.getBlue());


            aStar = new AStar(graph);
            aStar.compute(nodeNextToFEorFW1.getId(), lowerNode.getId());
            path = aStar.getShortestPath();
            nodesOnPath = path.getNodePath();
            addEdge(graph, vNode.getId(), nodesOnPath.get(1).getId());

            aStar.compute(nodeNextToFEorFW1.getId(), upperNode.getId());
            path = aStar.getShortestPath();
            nodesOnPath = path.getNodePath();
            addEdge(graph, vNode.getId(), nodesOnPath.get(1).getId());

            aStar.compute(nodeNextToFEorFW2.getId(), lowerNode.getId());
            path = aStar.getShortestPath();
            nodesOnPath = path.getNodePath();
            addEdge(graph, vNode.getId(), nodesOnPath.get(1).getId());

            aStar.compute(nodeNextToFEorFW2.getId(), upperNode.getId());
            path = aStar.getShortestPath();
            nodesOnPath = path.getNodePath();
            addEdge(graph, vNode.getId(), nodesOnPath.get(1).getId());


            addEdge(graph, vNode.getId(), nodeFW.getId());
            addEdge(graph, vNode.getId(), nodeFE.getId());
        }
        return graph;
    }

    public boolean HasSmallerXThan(Node a, Node b){
        return ((Geom)a.getAttribute("geom")).getX() < ((Geom)b.getAttribute("geom")).getX();
    }

    public Node getNodeByLabel(Graph graph, Label label){
        for (Node node : graph) {
            Label currentLabel = node.getAttribute("label");

            if (currentLabel == label)
                return node;
        }
        return null;
    }

    private void addNode(Graph graph, String name, Type type, Label label) {
        Node node = graph.addNode(name);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
    }

    private Color getColor(BufferedImage img, Geom geom) {
        return new Color(img.getRGB(geom.getX(),geom.getY()));
    }

    private Node addNode(Graph graph, String name, Type type, Label label, Geom geom) {
        Node node = graph.addNode(name);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("geom", geom);
        node.setAttribute("xy", geom.getX(), geom.getY());
        return node;
    }

    private Node addNode(Graph graph, String name, Type type, Label label, Geom geom, Color rgb) {
        Node node = addNode(graph, name, type, label, geom);
        node.setAttribute("rgb", rgb);
        return node;
    }

    private void addEdge(Graph graph, String sourceName, String targetName) {
        String name = sourceName + "-" + targetName;
        graph.addEdge(name, sourceName, targetName);
    }

    private void removeEdge(Graph graph, String sourceName, String targetName) {
        graph.removeEdge(sourceName, targetName);
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
}
