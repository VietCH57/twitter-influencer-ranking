package TwitRank.visualization.ui.components;

import org.graphstream.graph.Graph;
import org.graphstream.ui.layout.Layout;
import javax.swing.*;
import java.awt.*;

public class LayoutControlPanel extends JPanel {
    private final Graph graph;
    private final Layout layout;
    private final JButton resetBtn;
    private final JSlider forceSlider;

    public LayoutControlPanel(Graph graph, Layout layout) {
        this.graph = graph;
        this.layout = layout;
        this.resetBtn = new JButton("Reset Layout");
        this.forceSlider = new JSlider(0, 100, 50);

        setLayout(new FlowLayout(FlowLayout.LEFT));
        setupComponents();
    }

    private void setupComponents() {
        add(resetBtn);
        add(new JLabel("Force: "));
        add(forceSlider);

        resetBtn.addActionListener(e -> resetLayout());
        forceSlider.addChangeListener(e -> updateForce());
    }

    public void resetLayout() {
        layout.clear();
        layout.compute();
    }

    private void updateForce() {
        double force = forceSlider.getValue() / 100.0;
        layout.setStabilizationLimit(force);
    }
}