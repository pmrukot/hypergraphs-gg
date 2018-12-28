import org.graphstream.graph.Graph;

public class Runner {
    public static void main(String[] args) {
        System.out.println("Running");
        Graph testGraph = P4.prepareTestGraph();
        Graph updatedGraph = P4.executeProduction(testGraph);
        updatedGraph.display();
    }
}
