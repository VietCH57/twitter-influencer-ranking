package TwitRank.visualization.ui.components;

import org.graphstream.graph.Graph;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StatisticsPanel extends JPanel {
    private final Graph graph;
    private final JButton statsButton;
    private final JLabel statsLabel;

    public StatisticsPanel(Graph graph) {
        this.graph = graph;
        this.statsButton = new JButton("Show Statistics");
        this.statsLabel = new JLabel("Ready");

        setLayout(new FlowLayout(FlowLayout.LEFT));
        setupComponents();
    }

    private void setupComponents() {
        add(statsButton);
        add(statsLabel);

        statsButton.addActionListener(e -> showStatistics());
    }

    public void updateStatistics() {
        int nodeCount = graph.getNodeCount();
        int edgeCount = graph.getEdgeCount();
        statsLabel.setText(String.format("Nodes: %d, Edges: %d", nodeCount, edgeCount));
    }

    private void showStatistics() {
        Map<String, Integer> edgeTypes = new HashMap<>();
        graph.edges().forEach(e -> {
            String type = e.getAttribute("ui.class", String.class);
            edgeTypes.merge(type, 1, Integer::sum);
        });

        StringBuilder stats = new StringBuilder("Graph Statistics:\n\n");
        stats.append(String.format("Nodes: %d\n", graph.getNodeCount()));
        stats.append(String.format("Edges: %d\n\n", graph.getEdgeCount()));
        stats.append("Edge Types:\n");
        edgeTypes.forEach((type, count) ->
                stats.append(String.format("%s: %d\n", type, count)));

        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(stats.toString())),
                "Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
}