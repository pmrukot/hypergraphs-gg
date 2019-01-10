package parser;

import common.Geom;
import common.Label;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import productions.P1;
import productions.P2;
import productions.P3;
import productions.P4;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ComponentScan({"productions"})
public class Parser {
    int[] parseNumbersInLine(String line){
        String delimiterRegex = "[ ]*,[ ]*";
        return Arrays.stream(line.split(delimiterRegex)).mapToInt(Integer::parseInt).toArray();
    }

    private Stream<Node> neighourStream(Node node) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        node.getNeighborNodeIterator(),
                        Spliterator.ORDERED
                ),
                false
        );
    }

    Graph runProduction(int productionNumber, int[] coordinates, int[] rgb, Graph graph) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(Parser.class);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        BufferedImage img = ImageIO.read(Objects.requireNonNull(cl.getResourceAsStream("colors.jpg")));

        switch(productionNumber){
            case 1:
                P1 p1 = context.getBean(P1.class);
                return p1.run(coordinates, rgb);
            case 2:
                P2 p2 = context.getBean(P2.class);
                int x1 = coordinates[0];
                int y1 = coordinates[1];
                int x2 = coordinates[2];
                int y2 = coordinates[3];

                Node nodeI = graph.getNodeSet().stream()
                        .filter(node -> node.hasAttribute("label") &&
                                        node.<Label>getAttribute("label").equals(Label.I) &&
                                        node.<Geom>getAttribute("geom").getX() == (x1 + x2) / 2 &&
                                        node.<Geom>getAttribute("geom").getY() == (y1 + y2) / 2
                        ).findFirst().get();
                return p2.run(graph, img, nodeI);
            case 3:
                P3 p3 = context.getBean(P3.class);
                Graph testGraph = p3.prepareTestGraph(img);
                Edge edge = testGraph.getEdge("1-2");
                Node f1 = testGraph.getNode("f1");
                Graph g3 = p3.run(testGraph, img, edge, f1);
                g3.display().disableAutoLayout();
                break;
            case 4:
                P4 p4 = context.getBean(P4.class);

                x1 = coordinates[0];
                y1 = coordinates[1];
                x2 = coordinates[2];
                y2 = coordinates[3];

                // Extracting input nodes for P4 test
//                Node nodeFN = p4.getNodeByLabel(graph, Label.FN);
                Node nodeFN = graph.getNodeSet().stream()
                        .filter(node -> node.<Label>getAttribute("label").equals(Label.FN) &&
                                        node.<Geom>getAttribute("geom").getX() == (x1 + x2) / 2 &&
                                        node.<Geom>getAttribute("geom").getY() == (y1 + y2) / 2)
                        .findFirst().get();

                Node nodeFW = graph.getNodeSet().stream()
                        .filter(node ->
                                node.<Geom>getAttribute("geom").getX() == x1 &&
                                node.<Geom>getAttribute("geom").getY() == (y1 + y2) / 2
                        ).flatMap(this::neighourStream)
                        .filter(node -> node.<Label>getAttribute("label").equals(Label.FW))
                        .findFirst().get();

                Node nodeFE = graph.getNodeSet().stream()
                        .filter(node ->
                                node.<Geom>getAttribute("geom").getX() == x2 &&
                                        node.<Geom>getAttribute("geom").getY() == (y1 + y2) / 2
                        ).flatMap(this::neighourStream)
                        .filter(node -> node.<Label>getAttribute("label").equals(Label.FE))
                        .findFirst().get();

                // Run P4 production

                return p4.run(graph, img, nodeFN, nodeFW, nodeFE);
            default:
                System.out.println("Production number is wrong");
        }
        return null;
    }

    public void parseFileAndRunProduction(String pathname) {
        Scanner scanner;
        ClassLoader classLoader = getClass().getClassLoader();

        File f = new File(Objects.requireNonNull(classLoader.getResource(pathname)).getFile());
        try {
            scanner = new Scanner(f);
            Graph graph = null;

            while (scanner.hasNext()) {
                int productionNumber = Integer.parseInt(scanner.nextLine());
                int[] coordinates = parseNumbersInLine(scanner.nextLine());
                int[] rgb;
                if (scanner.hasNext())
                    rgb = parseNumbersInLine(scanner.nextLine());
                else
                    rgb = null;
                graph = runProduction(productionNumber, coordinates, rgb, graph);
            }
            graph.display();

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        parser.parseFileAndRunProduction("production.txt");
    }
}