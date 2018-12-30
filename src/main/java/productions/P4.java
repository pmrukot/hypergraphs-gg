package productions;

import common.Geom;
import common.Label;
import common.Type;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.springframework.stereotype.Service;

@Service
public class P4 {

    public Graph prepareTestGraph() {
        Graph graph = new SingleGraph("P4Test");

        // TODO: Change name to UUID?
        addNode(graph, "1", Type.HYPEREDGE, Label.I);
        addNode(graph, "2", Type.HYPEREDGE, Label.I);
        addNode(graph, "3", Type.HYPEREDGE, Label.I);
        addNode(graph, "4", Type.HYPEREDGE, Label.I);

        // Geom is just for test, should not change during production
        addNode(graph, "5", Type.VERTEX, Label.V, new Geom(1, 2));
        addNode(graph, "6", Type.VERTEX, Label.V, new Geom(3, 3));
        addNode(graph, "7", Type.VERTEX, Label.V, new Geom(1, 1));
        addNode(graph, "8", Type.VERTEX, Label.V, new Geom(2, 3));

        addNode(graph, "9", Type.HYPEREDGE, Label.FE);
        addNode(graph, "10", Type.HYPEREDGE, Label.FW);

        addNode(graph, "11", Type.HYPEREDGE, Label.FN);

        addEdge(graph,"1", "5");
        addEdge(graph,"5", "2");
        addEdge(graph,"2", "6");
        addEdge(graph,"6", "4");
        addEdge(graph,"4", "7");
        addEdge(graph,"7", "3");
        addEdge(graph,"3", "8");
        addEdge(graph,"8", "1");

        addEdge(graph,"8", "9");
        addEdge(graph,"6", "10");

        addEdge(graph,"5", "11");
        addEdge(graph,"7", "11");

        return graph;
    }

    public Graph run(Graph graph) {
        for (Node node : graph) {
            Label label = node.getAttribute("label");

            if (label == Label.FN) {
                for (Edge edge : node.getEachEdge()) {
                    if (edge != null) {
                        graph.removeEdge(edge);
                    }
                }
                addNode(graph, "12", Type.HYPEREDGE, Label.FS);

                Geom northGeom = graph.getNode("5").getAttribute("geom");
                Geom westGeom = graph.getNode("6").getAttribute("geom");

                int vX = northGeom.getX();
                int vY = westGeom.getY();

                addNode(graph, "v", Type.HYPEREDGE, Label.V, new Geom(vX, vY));

                addEdge(graph,"5","11");
                addEdge(graph,"11","v");
                addEdge(graph,"v","12");
                addEdge(graph,"12","7");

                addEdge(graph,"v", "9");
                addEdge(graph,"v", "10");

                addEdge(graph,"v", "1");
                addEdge(graph,"v", "2");
                addEdge(graph,"v", "3");
                addEdge(graph,"v", "4");
            }
        }

        return graph;
    }

    private void addNode(Graph graph, String name, Type type, Label label) {
        Node node = graph.addNode(name);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
    }

    private void addNode(Graph graph, String name, Type type, Label label, Geom geom) {
        Node node = graph.addNode(name);
        node.setAttribute("type", type);
        node.setAttribute("label", label);
        node.setAttribute("geom", geom);
    }

    private void addEdge(Graph graph, String sourceName, String targetName) {
        String name = sourceName + "-" + targetName;
        graph.addEdge(name, sourceName, targetName);
    }
}
