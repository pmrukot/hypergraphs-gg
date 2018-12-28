import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class Runner {
    public static void main(String[] args) {
        System.out.println("Hello World");
        Graph graph = new SingleGraph("Tutorial 1");
        graph.addNode("A" );
        graph.addNode("B" );
        graph.addNode("C" );
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");
    }
}
