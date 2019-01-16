package parser;

import common.Geom;
import common.Label;
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
import java.awt.*;
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

    void setColor(BufferedImage img, Geom geom, Color rgb) {
        img.setRGB(geom.getX(), geom.getY(), rgb.getRGB());
    }

    private Stream<Node> neighbourStream(Node node) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        node.getNeighborNodeIterator(),
                        Spliterator.ORDERED
                ),
                false
        );
    }

    private Node getFNeighbour(Graph graph, int x, int y, Label label) {
        return graph.getNodeSet().stream()
                .filter(node ->
                        node.hasAttribute("geom") &&
                                node.<Geom>getAttribute("geom").getX() == x &&
                                node.<Geom>getAttribute("geom").getY() == y
                ).flatMap(this::neighbourStream)
                .filter(node -> node.<Label>getAttribute("label").equals(label))
                .findFirst().get();
    }

    Graph runProduction(int productionNumber, int[] coordinates, int[] rgb, Graph graph, BufferedImage img) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(Parser.class);

        int x1;
        int y1;
        int x2;
        int y2;

        Color pointRGB;
        Geom pointGeom;

        switch(productionNumber){
            case 1:
                P1 p1 = context.getBean(P1.class);
                int width = img.getWidth();
                int height = img.getHeight();

                Geom topLeft = new Geom(0,height-1);
                Geom topRight = new Geom(width - 1,height -1);
                Geom bottomLeft = new Geom(0,0);
                Geom bottomRight = new Geom(width -1,0);

                Color topLeftRGB = new Color(rgb[0], rgb[1], rgb[2]);
                Color topRightRGB = new Color(rgb[3], rgb[4], rgb[5]);
                Color bottomLeftRGB = new Color(rgb[6], rgb[7], rgb[8]);
                Color bottomRightRGB = new Color(rgb[9], rgb[10], rgb[11]);

                setColor(img, topLeft, topLeftRGB);
                setColor(img, topRight, topRightRGB);
                setColor(img, bottomLeft, bottomLeftRGB);
                setColor(img, bottomRight, bottomRightRGB);

                return p1.run(img);
            case 2:
                P2 p2 = context.getBean(P2.class);
                x1 = coordinates[0];
                y1 = coordinates[1];
                x2 = coordinates[2];
                y2 = coordinates[3];

                pointRGB = new Color(rgb[0], rgb[1], rgb[2]);
                pointGeom = new Geom((x1+x2)/2, (y1+y2)/2);
                setColor(img, pointGeom, pointRGB);

                Node nodeI = graph.getNodeSet().stream()
                        .filter(node -> node.hasAttribute("label") &&
                                        node.<Label>getAttribute("label").equals(Label.I) &&
                                        node.hasAttribute("geom") &&
                                        node.<Geom>getAttribute("geom").getX() == (x1 + x2) / 2 &&
                                        node.<Geom>getAttribute("geom").getY() == (y1 + y2) / 2
                        ).findFirst().get();
                return p2.run(graph, img, nodeI);
            case 3:
                x1 = coordinates[0];
                x2 = coordinates[1];
                y1 = coordinates[2];

                pointRGB = new Color(rgb[0], rgb[1], rgb[2]);
                pointGeom = new Geom((x1+x2)/2, y1);
                setColor(img, pointGeom, pointRGB);

                P3 p3 = context.getBean(P3.class);
                Graph testGraph = p3.prepareTestGraph(img);
                Node border = graph.getNodeSet().stream()
                        .filter(node -> node.hasAttribute("label") &&
                                        node.<Label>getAttribute("label").equals(Label.B) &&
                                        node.hasAttribute("geom") &&
                                        node.<Geom>getAttribute("geom").getX() == (x1 + x2) / 2 &&
                                        node.<Geom>getAttribute("geom").getY() == y1)
                        .findFirst().get();

                return p3.run(testGraph, img, border);
            case 4:
                P4 p4 = context.getBean(P4.class);

                x1 = coordinates[0];
                y1 = coordinates[1];
                x2 = coordinates[2];
                y2 = coordinates[3];

                pointRGB = new Color(rgb[0], rgb[1], rgb[2]);
                pointGeom = new Geom((x1+x2)/2, y1);
                setColor(img, pointGeom, pointRGB);

                Node nodeFN = graph.getNodeSet().stream()
                        .filter(node -> node.<Label>getAttribute("label").equals(Label.FN) &&
                                        node.hasAttribute("geom") &&
                                        node.<Geom>getAttribute("geom").getX() == (x1 + x2) / 2 &&
                                        node.<Geom>getAttribute("geom").getY() == (y1 + y2) / 2)
                        .findFirst().get();

                Node nodeFW = getFNeighbour(graph, x1, (y1 + y2) / 2, Label.FW);

                Node nodeFE = getFNeighbour(graph, x2, (y1 + y2) / 2, Label.FE);

                return p4.run(graph, img, nodeFN, nodeFW, nodeFE);
            default:
                System.out.println("Production number is wrong");
        }
        return null;
    }

    public void parseFileAndRunProduction(String pathname) throws IOException{
        Scanner scanner;
        ClassLoader classLoader = getClass().getClassLoader();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        BufferedImage img = ImageIO.read(Objects.requireNonNull(cl.getResourceAsStream("colors.jpg")));

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
                graph = runProduction(productionNumber, coordinates, rgb, graph, img);
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
