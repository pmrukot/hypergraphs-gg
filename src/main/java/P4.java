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

        setNodeTypeAndLabel(graph.addNode("1"), NodeType.NODE, NodeLabel.I);
        setNodeTypeAndLabel(graph.addNode("2"), NodeType.NODE, NodeLabel.I);
        setNodeTypeAndLabel(graph.addNode("3"), NodeType.NODE, NodeLabel.I);
        setNodeTypeAndLabel(graph.addNode("4"), NodeType.NODE, NodeLabel.I);

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


        graph.display();

        return graph;
    }
}
