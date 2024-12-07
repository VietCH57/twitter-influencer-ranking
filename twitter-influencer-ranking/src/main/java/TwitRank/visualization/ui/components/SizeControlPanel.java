package TwitRank.visualization.ui.components;

import org.graphstream.graph.Graph;
import javax.swing.*;
import java.awt.*;

public class SizeControlPanel extends JPanel {
    private final Graph graph;
    private final JButton followerSizeBtn;
    private final JButton followingSizeBtn;
    private final JButton resetSizeBtn;

    public SizeControlPanel(Graph graph) {
        this.graph = graph;
        this.followerSizeBtn = new JButton("By Followers");
        this.followingSizeBtn = new JButton("By Following");
        this.resetSizeBtn = new JButton("Reset");

        setLayout(new FlowLayout(FlowLayout.LEFT));
        setupComponents();
    }

    private void setupComponents() {
        add(followerSizeBtn);
        add(followingSizeBtn);
        add(resetSizeBtn);

        followerSizeBtn.addActionListener(e -> setSizesByMetric("followers"));
        followingSizeBtn.addActionListener(e -> setSizesByMetric("following"));
        resetSizeBtn.addActionListener(e -> resetSizes());
    }

    private void setSizesByMetric(String metric) {
        graph.nodes().forEach(n -> {
            double value = (double) n.getAttribute(metric);
            double size = Math.max(5, Math.min(30, Math.log(value + 1) * 3));
            n.setAttribute("ui.size", size);
        });
    }

    public void resetSizes() {
        graph.nodes().forEach(n -> n.setAttribute("ui.size", 10));
    }
}