package TwitRank.visualization.ui.components;

import org.graphstream.graph.Graph;
import javax.swing.*;
import java.awt.*;

public class EdgeFilterPanel extends JPanel {
    private final Graph graph;
    private final JButton showAllBtn;
    private final JButton showFollowBtn;
    private final JButton showRetweetBtn;
    private final JButton showReplyBtn;

    public EdgeFilterPanel(Graph graph) {
        this.graph = graph;
        this.showAllBtn = new JButton("All");
        this.showFollowBtn = new JButton("Follow");
        this.showRetweetBtn = new JButton("Retweet");
        this.showReplyBtn = new JButton("Reply");

        setLayout(new FlowLayout(FlowLayout.LEFT));
        setupComponents();
    }

    private void setupComponents() {
        add(showAllBtn);
        add(showFollowBtn);
        add(showRetweetBtn);
        add(showReplyBtn);

        showAllBtn.addActionListener(e -> showAllEdges());
        showFollowBtn.addActionListener(e -> filterEdges("follow"));
        showRetweetBtn.addActionListener(e -> filterEdges("retweet"));
        showReplyBtn.addActionListener(e -> filterEdges("reply"));
    }

    public void showAllEdges() {
        graph.edges().forEach(e -> e.removeAttribute("ui.hide"));
    }

    private void filterEdges(String type) {
        graph.edges().forEach(e -> {
            String edgeClass = e.getAttribute("ui.class", String.class);
            e.setAttribute("ui.hide", !type.equals(edgeClass));
        });
    }
}