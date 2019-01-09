package parser;

import common.Label;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import productions.P1;
import productions.P2;
import productions.P4;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

@ComponentScan({"productions"})
public class Parser {
    int[] parseNumbersInLine(String line){
        String delimiterRegex = "[ ]*,[ ]*";
        return Arrays.stream(line.split(delimiterRegex)).mapToInt(Integer::parseInt).toArray();
    }

    void runProduction(int productionNumber, int[] coordinates, int[] rgb) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(Parser.class);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        BufferedImage img = ImageIO.read(Objects.requireNonNull(cl.getResourceAsStream("colors.jpg")));

        switch(productionNumber){
            case 1:
                P1 p1 = context.getBean(P1.class);
                Graph graph = p1.run(img);
                graph.display();
                break;
            case 2:
                P2 p2 = context.getBean(P2.class);

                Graph graphP2 = p2.prepareTestGraph(img);
                Node nodeI =  graphP2.getNodeSet().stream().filter(node -> node.hasAttribute("label") && node.getAttribute("label").toString().equals(Label.I.toString())).findFirst().get();
                Graph g2 = p2.run(graphP2, img, nodeI);
                g2.display();
                break;
            case 3:
                break;
            case 4:
                P4 p4 = context.getBean(P4.class);

                // Preparing P4 test graph
                Graph preP4 = p4.prepareTestGraph(img);
                preP4.display().disableAutoLayout();

                // Extracting input nodes for P4 test
                Node nodeFN = p4.getNodeByLabel(preP4, Label.FN);
                Node nodeFW = p4.getNodeByLabel(preP4, Label.FW);
                Node nodeFE = p4.getNodeByLabel(preP4, Label.FE);

                // Run P4 production
                Graph postP4 = p4.run(preP4, img, nodeFN, nodeFW, nodeFE);
                postP4.display().disableAutoLayout();
                break;
            default:
                System.out.println("Production number is wrong");

        }
    }

    public void parseFileAndRunProduction(String pathname) {
        Scanner scanner;
        ClassLoader classLoader = getClass().getClassLoader();

        File f = new File(Objects.requireNonNull(classLoader.getResource(pathname)).getFile());
        try {
            scanner = new Scanner(f);
            int productionNumber = Integer.parseInt(scanner.nextLine());
            int[] coordinates = parseNumbersInLine(scanner.nextLine());
            int[] rgb = parseNumbersInLine(scanner.nextLine());

            runProduction(productionNumber, coordinates, rgb);

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
