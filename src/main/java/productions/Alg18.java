package productions;

import approximation.BitmapAproximator;
import common.Geom;
import common.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.graphstream.graph.*;

import java.awt.*;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Service
public class Alg18 {

    private BitmapAproximator ba;

    @Autowired
    public Alg18(BitmapAproximator ba) {
        this.ba = ba;
    }

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

                Geom tmpGeom1 = new Geom(maxX, maxY);
                Geom tmpGeom2 = new Geom(maxX, minY);
                Geom tmpGeom3 = new Geom(minX, maxY);
                Geom tmpGeom4 = new Geom(minX, minY);

                Optional<Color> col1 = getColorFromGeom(graph, tmpGeom1);
                Optional<Color> col2 = getColorFromGeom(graph, tmpGeom2);
                Optional<Color> col3 = getColorFromGeom(graph, tmpGeom3);
                Optional<Color> col4 = getColorFromGeom(graph, tmpGeom4);

                ba.run(col1, col2, col3, col4, minX, minY, maxX, maxY);
            }
        }

        return graph;
    }

    private Optional<Color> getColorFromGeom(Graph graph, Geom geom) {
        Color color = new Color(0,0,0);
        if(graph.getNodeSet().stream().filter(n -> n.getAttribute("geom").equals(geom)).count() == 1) {
            return Optional.of(graph.getNodeSet().stream().filter(n -> n.getAttribute("geom").equals(geom)).findAny().get().getAttribute("rgb"));
        } else {
            return Optional.empty();
        }
    }
}
