package productions;

import com.sun.istack.internal.NotNull;
import common.Geom;
import common.Type;
import org.springframework.stereotype.Service;
import org.graphstream.graph.*;

import java.awt.*;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

@Service
public class Alg18 {
    // petla po wierzcholkach w grafie
    // dla kazdego hyperedge
    // znajdz jego sasiadow
    // znajdz max_x, max_y, min_x, min_y
    // wywolaj funkcje
    // (x1,y1),(x2,y2) r1,r2,r3,r4, g1,g2,g3,g4, b1,b2,b3,b4
    // a po naszemu

    public Graph run(Graph graph) {

        Geom geom;

        for (Node node : graph) {
            if(node.getAttribute("type") == Type.HYPEREDGE) {
                Set<Integer> x = new TreeSet<>();
                Set<Integer> y = new TreeSet<>();
                for(Edge edge : node.getEachEdge()) {
                    geom = edge.getOpposite(node).getAttribute("geom");
                    x.add(geom.getX());
                    y.add(geom.getY());
                }

                int minX = Collections.min(x);
                int maxX = Collections.max(x);
                int minY = Collections.min(y);
                int maxY = Collections.max(y);

                int r1 = 0, r2 = 0, r3 = 0, r4 = 0;
                int b1 = 0, b2 = 0, b3 = 0, b4 = 0;
                int g1 = 0, g2 = 0, g3 = 0, g4 = 0;

                Geom tmpGeom1 = new Geom(maxX, maxY);
                Geom tmpGeom2 = new Geom(maxX, minY);
                Geom tmpGeom3 = new Geom(minX, maxY);
                Geom tmpGeom4 = new Geom(minX, minY);

                if(graph.getNodeSet().stream().filter(n -> n.getAttribute("geom").equals(tmpGeom1)).count() == 1) {
                    Color rgb = graph.getNodeSet().stream().filter(n -> n.getAttribute("geom").equals(tmpGeom1)).findAny().get().getAttribute("rgb");
                    r1 = rgb.getRed();
                    b1 = rgb.getBlue();
                    g1 = rgb.getGreen();
                }

                if(graph.getNodeSet().stream().filter(n -> n.getAttribute("geom").equals(tmpGeom2)).count() == 1) {
                    Color rgb = graph.getNodeSet().stream().filter(n -> n.getAttribute("geom").equals(tmpGeom2)).findAny().get().getAttribute("rgb");
                    r2 = rgb.getRed();
                    b2 = rgb.getBlue();
                    g2 = rgb.getGreen();
                }

                if(graph.getNodeSet().stream().filter(n -> n.getAttribute("geom").equals(tmpGeom3)).count() == 1) {
                    Color rgb = graph.getNodeSet().stream().filter(n -> n.getAttribute("geom").equals(tmpGeom3)).findAny().get().getAttribute("rgb");
                    r3 = rgb.getRed();
                    b3 = rgb.getBlue();
                    g3 = rgb.getGreen();
                }

                if(graph.getNodeSet().stream().filter(n -> n.getAttribute("geom").equals(tmpGeom4)).count() == 1) {
                    Color rgb = graph.getNodeSet().stream().filter(n -> n.getAttribute("geom").equals(tmpGeom4)).findAny().get().getAttribute("rgb");
                    r4 = rgb.getRed();
                    b4 = rgb.getBlue();
                    g4 = rgb.getGreen();
                }

                //todo: alg12(minX, minY, maxX, maxY, r1, r2, r3, r4,  b1, b2, b3, b4, g1, g2, g3, g4);
            }
        }

        return graph;
    }
}
