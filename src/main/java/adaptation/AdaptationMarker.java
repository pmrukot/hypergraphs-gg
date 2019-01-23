package adaptation;

import approximation.ApproximationError;
import approximation.ApproximationErrorComputationException;
import common.Label;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import productions.P2;
import productions.P3;
import productions.P4;
import productions.P5;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

public class AdaptationMarker {

    private P2 p2;
    private P3 p3;
    private P4 p4;
    private P5 p5;
    boolean wasErrorGreaterThanEpsilon = false;
    private Graph graph;
    private BufferedImage img;
    boolean continueAdaptation;

    public AdaptationMarker(){
        p2 = new P2();
        p3 = new P3();
        p4 = new P4();
        p5 = new P5();
    }

    public Graph run(Graph graph, BufferedImage img, double epsilon, int maxStep) {
        this.graph = graph;
        this.img = img;
        ApproximationError errorChecker = new ApproximationError(img, graph);
        for (int i = 0; i < maxStep; i++) {
            List<Node> nodeList = graph.getNodeSet().stream()
                    .filter(n -> checkLabel(n, Label.I))
                    .collect(Collectors.toList());
            nodeList.forEach(node -> checkApproximationError(node, errorChecker, epsilon));
            if (wasErrorGreaterThanEpsilon) {
                do {
                    continueAdaptation = false;
                    nodeList = graph.getNodeSet().stream()
                            .filter(n -> checkLabel(n, Label.I))
                            .collect(Collectors.toList());
                    nodeList.forEach(node -> runP2(node));
                    checkForChanges(nodeList, Label.I);

                    nodeList = graph.getNodeSet().stream()
                            .filter(n -> checkLabel(n, Label.B))
                            .collect(Collectors.toList());
                    nodeList.forEach(node -> runP3(node));
                    checkForChanges(nodeList, Label.B);

                    nodeList = graph.getNodeSet().stream()
                            .filter(n -> checkLabel(n, Label.FN) || checkLabel(n, Label.FW) || checkLabel(n, Label.FS) || checkLabel(n, Label.FE))
                            .collect(Collectors.toList());
                    nodeList.forEach(node -> runP4(node));
                    checkForChanges(nodeList, Label.FN);
                } while (continueAdaptation);
            }
        }
        return graph;
    }

    private void checkForChanges(List<Node> nodeList, Label label) {
        List<Node> nodeListAfterProductions;
        if (label != Label.FN) {
            nodeListAfterProductions = graph.getNodeSet().stream()
                    .filter(n -> checkLabel(n, label))
                    .collect(Collectors.toList());
        } else {
            nodeListAfterProductions = graph.getNodeSet().stream()
                    .filter(n -> checkLabel(n, Label.FN) || checkLabel(n, Label.FW) || checkLabel(n, Label.FS) || checkLabel(n, Label.FE))
                    .collect(Collectors.toList());
        }
        if (nodeList.size() != nodeListAfterProductions.size()){
            continueAdaptation = true;
        }
    }

    private boolean checkLabel(Node n, Label label) {
        return n.getAttribute("label").equals(label);
    }

    private void checkApproximationError(Node node, ApproximationError errorChecker, double epsilon) {
        try {
            double error = errorChecker.compute(node);
            if (error > epsilon) {
                graph = p5.run(graph, img, node);
                wasErrorGreaterThanEpsilon = true;
            }
        } catch (ApproximationErrorComputationException e) {
            e.printStackTrace();
        }
    }

    private void runP2(Node node) {
        graph = p2.run(graph, img, node);
    }

    private void runP3(Node node) {
        graph = p3.run(graph, img, node);
    }

    private void runP4(Node node) {
        graph = p4.run(graph, img, node);
    }
}
