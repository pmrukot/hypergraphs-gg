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
import java.util.Iterator;
import java.util.List;

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
        if (nodeFN.getAttribute("label") != Label.FN || nodeFW.getAttribute("label") != Label.FW || nodeFE.getAttribute("label") != Label.FE) {
            System.out.println("One or more of the labels of input nodes are incorrect");
            return false;
        }

        Iterator<Node> nodeFNiterator = nodeFN.getNeighborNodeIterator();
        while (nodeFNiterator.hasNext()) {
            Node currentNode = nodeFNiterator.next();
            if (currentNode.getAttribute("label") != Label.T) {
                return false;
            }
        }

        Iterator<Node> nodeFWiterator = nodeFW.getNeighborNodeIterator();
        while (nodeFWiterator.hasNext()) {
            Node currentNode = nodeFWiterator.next();
            if (currentNode.getAttribute("label") != Label.T) {
                return false;
            }
        }

        Iterator<Node> nodeFEiterator = nodeFE.getNeighborNodeIterator();
        while (nodeFEiterator.hasNext()) {
            Node currentNode = nodeFEiterator.next();
            if (currentNode.getAttribute("label") != Label.T) {
                return false;
            }
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

        Geom nodeFNgeom = nodeFN.getAttribute("geom");
        Geom nodeFWgeom = nodeFW.getAttribute("geom");
        Geom nodeFEgeom = nodeFE.getAttribute("geom");

        return nodeFNgeom.getY() == nodeFWgeom.getY() && nodeFWgeom.getY() == nodeFEgeom.getY();
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
        Iterator<Edge> edgeIterator = nodeFN.getEachEdge().iterator();
        Node lowerNode;
        try {
            lowerNode = removeLowerEdgeAndReturnLowerNode(graph, nodeFN);
        } catch (IndexOutOfBoundsException e) {
            return graph;
        }
        Node top = edgeIterator.next().getOpposite(nodeFN);
        AStar aStar = new AStar(graph);
        aStar.compute(top.getId(), lowerNode.getId());
        if(aStar.noPathFound())
            return graph;
        Path path = aStar.getShortestPath();
        List<Node> nodesOnPath = path.getNodePath();
        if(nodesOnPath.size() != 5)
            return graph;

        Node nodeNextToFEorFW = nodesOnPath.get(2);
        Iterator<Node> nodeIterator = nodeNextToFEorFW.getNeighborNodeIterator();
        while(nodeIterator.hasNext()){
            Node node = nodeIterator.next();
            if(!nodesOnPath.contains(node) && ((HasSmallerXThan(node, nodeNextToFEorFW) && HasSmallerXThan(nodeFN, node)) || (HasSmallerXThan(nodeNextToFEorFW, node) && HasSmallerXThan(node, nodeFN)))){
                if(HasSmallerXThan(node, nodeFN)){
                    nodeFW = node;
                }
                else {
                    nodeFE = node;
                }
                break;
            }
        }
        if(nodeFE == null && nodeFW == null)
            return graph;

        Node temp = nodesOnPath.get(1);
        graph.removeEdge(top, temp);

        aStar = new AStar(graph);
        aStar.compute(top.getId(), lowerNode.getId());
        if(aStar.noPathFound())
            return graph;
        path = aStar.getShortestPath();
        nodesOnPath = path.getNodePath();
        if(nodesOnPath.size() != 5)
            return graph;

        nodeNextToFEorFW = nodesOnPath.get(2);
        nodeIterator = nodeNextToFEorFW.getNeighborNodeIterator();
        while(nodeIterator.hasNext()){
            Node node = nodeIterator.next();
            if(!nodesOnPath.contains(node) && ((HasSmallerXThan(node, nodeNextToFEorFW) && HasSmallerXThan(nodeFN, node)) || (HasSmallerXThan(nodeNextToFEorFW, node) && HasSmallerXThan(node, nodeFN)))){
                if(HasSmallerXThan(node, nodeFN)){
                    nodeFW = node;
                }
                else {
                    nodeFE = node;
                }
                break;
            }
        }
        if(nodeFE == null || nodeFW == null)
            return graph;

        addEdge(graph, top.getId(), temp.getId());

        if(verifyNodes(graph, nodeFN, nodeFW, nodeFE)) {

            Node upperNode = nodeFN.getNeighborNodeIterator().next();

            String fsId = Integer.toString(Integer.parseInt(nodeFN.getId()) + 1);
            Geom fnGeom = nodeFN.getAttribute("geom");
            Geom nodeUnderFnGeom = lowerNode.getAttribute("geom");
            Geom nodeAboveFnGeom = upperNode.getAttribute("geom");
            Geom fnNewGeom = new Geom((fnGeom.getX() + nodeAboveFnGeom.getX()) / 2, (fnGeom.getY() + nodeAboveFnGeom.getY()) / 2);
            nodeFN.setAttribute("geom", fnNewGeom);
            nodeFN.setAttribute("xy", fnNewGeom.getX(), fnNewGeom.getY());

            addNode(graph, fsId, Type.HYPEREDGE, Label.FS, new Geom((fnGeom.getX() + nodeUnderFnGeom.getX()) / 2, (fnGeom.getY() + nodeUnderFnGeom.getY()) / 2));
            addEdge(graph, fsId, lowerNode.getId());
            Color color = getColor(img, fnGeom);
            Node vNode = addNode(graph, "V", Type.HYPEREDGE, Label.V, fnGeom, color);

            vNode.addAttribute("ui.label", "V: " + color.getRed() + " " + color.getGreen() + " " + color.getBlue());

            lowerNode.getNeighborNodeIterator().forEachRemaining(node -> {
                addEdge(graph, vNode.getId(), node.getId());
            });

            upperNode.getNeighborNodeIterator().forEachRemaining(node -> {
                addEdge(graph, vNode.getId(), node.getId());
            });

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
}
