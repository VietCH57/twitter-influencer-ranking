package TwitRank.visualization.ui.components;

import org.graphstream.graph.Graph;
import TwitRank.elements.User;
import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {
    private final Graph graph;
    private final JTextField searchField;
    private final JButton searchButton;

    public SearchPanel(Graph graph) {
        this.graph = graph;
        this.searchField = new JTextField(15);
        this.searchButton = new JButton("Search");

        setLayout(new FlowLayout(FlowLayout.LEFT));
        setupComponents();
    }

    private void setupComponents() {
        add(searchField);
        add(searchButton);

        searchButton.addActionListener(e -> performSearch());
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        clearHighlights();

        if (!query.isEmpty()) {
            highlightMatchingNodes(query);
        }
    }

    private void clearHighlights() {
        graph.nodes().forEach(n -> n.setAttribute("ui.class", ""));
    }

    private void highlightMatchingNodes(String query) {
        graph.nodes().forEach(n -> {
            User user = (User) n.getAttribute("user");
            if (user != null && user.getUsername().toLowerCase().contains(query)) {
                n.setAttribute("ui.class", "highlighted");
            }
        });
    }

    public void clearSearch() {
        searchField.setText("");
        clearHighlights();
    }
}