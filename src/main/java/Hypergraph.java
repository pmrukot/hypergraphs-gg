import org.graphstream.graph.Graph;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import productions.P1;
import productions.P4;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;


@ComponentScan({"productions"})
public class Hypergraph {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(Hypergraph.class);
        P1 p1 = context.getBean(P1.class);

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        BufferedImage img = ImageIO.read(Objects.requireNonNull(cl.getResourceAsStream("colors.jpg")));
        Graph graph = p1.run(img);
        graph.display();

        //// P4 testing bellow
        // P4 p4 = context.getBean(P4.class);
        // Graph preP4 = p4.prepareTestGraph();
        // preP4.display();
        // Graph postP4 = p4.run(preP4);
        // postP4.display();
    }

}
