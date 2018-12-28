import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

class P4 {
    private static void setNodeTypeAndLabel(Node node, NodeType type, NodeLabel label) {
        node.setAttribute("type", type);
        node.setAttribute("label", label);
    }

    static Graph prepareTestGraph() {
        Graph graph = new SingleGraph("P4Test");

        setNodeTypeAndLabel(graph.addNode("1"), NodeType.HYPERREDGE, NodeLabel.I);
        setNodeTypeAndLabel(graph.addNode("2"), NodeType.HYPERREDGE, NodeLabel.I);
        setNodeTypeAndLabel(graph.addNode("3"), NodeType.HYPERREDGE, NodeLabel.I);
        setNodeTypeAndLabel(graph.addNode("4"), NodeType.HYPERREDGE, NodeLabel.I);

        setNodeTypeAndLabel(graph.addNode("5"), NodeType.NODE, null);
        setNodeTypeAndLabel(graph.addNode("6"), NodeType.NODE, null);
        setNodeTypeAndLabel(graph.addNode("7"), NodeType.NODE, null);
        setNodeTypeAndLabel(graph.addNode("8"), NodeType.NODE, null);

        setNodeTypeAndLabel(graph.addNode("9"), NodeType.HYPERREDGE, NodeLabel.F2);
        setNodeTypeAndLabel(graph.addNode("10"), NodeType.HYPERREDGE, NodeLabel.F2);

        setNodeTypeAndLabel(graph.addNode("11"), NodeType.HYPERREDGE, NodeLabel.F1);

        graph.addEdge("1-5", "1", "5");
        graph.addEdge("5-2", "5", "2");
        graph.addEdge("2-6", "2", "6");
        graph.addEdge("6-4", "6", "4");
        graph.addEdge("4-7", "4", "7");
        graph.addEdge("7-3", "7", "3");
        graph.addEdge("3-8", "3", "8");
        graph.addEdge("8-1", "8", "1");

        graph.addEdge("8-9", "8", "9");
        graph.addEdge("6-10", "6", "10");

        graph.addEdge("5-11", "5", "11");
        graph.addEdge("7-11", "7", "11");

        // graph.display();

        return graph;
    }

    static Graph executeProduction(Graph graph) {
        for (Node node : graph) {
            NodeLabel label = node.getAttribute("label");

            if (label == NodeLabel.F1 && node.getAttribute("P4") == null) {
                for (Edge edge : node.getEachEdge()) {
                    if (edge != null) {
                        graph.removeEdge(edge);
                    }
                }
                Node newF1 = graph.addNode("12");
                setNodeTypeAndLabel(newF1, NodeType.HYPERREDGE, NodeLabel.F1);

                // Adding another F1 results will include it to iteration, there is a need to distinguish them...
                newF1.setAttribute("P4", true);

                setNodeTypeAndLabel(graph.addNode("v"), NodeType.NODE, null);

                graph.addEdge("5-11", "5", "11");
                graph.addEdge("11-v", "11", "v");
                graph.addEdge("v-12", "v", "12");
                graph.addEdge("12-7", "12", "7");

                graph.addEdge("v-9", "v", "9");
                graph.addEdge("v-10", "v", "10");

                graph.addEdge("v-1", "v", "1");
                graph.addEdge("v-2", "v", "2");
                graph.addEdge("v-3", "v", "3");
                graph.addEdge("v-4", "v", "4");
            }
        }
        return graph;
    }
}
