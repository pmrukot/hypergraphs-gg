package productions;

import org.graphstream.graph.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class P2 {

    @Autowired
    private P1 p1;

    public Graph prepareTestGraph(BufferedImage img) {
        return p1.run(img);
    }
}
