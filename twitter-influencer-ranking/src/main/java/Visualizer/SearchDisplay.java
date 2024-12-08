package Visualizer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public class SearchDisplay implements ViewerListener {
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_HEIGHT = 30;
    private static final double TARGET_ZOOM = 0.035;

    private final JTextField searchField;
    private final Graph graph;
    private final Camera camera;
    private final JPanel searchPanel;

    public SearchDisplay(Graph graph, Viewer viewer) {
        this.graph = graph;
        this.camera = viewer.getDefaultView().getCamera();

        // Create search panel
        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
        searchPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        // Create and configure search field
        searchField = new JTextField(15);
        searchField.setToolTipText("Enter username to search");

        // Add action listener for search
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAndFocus(searchField.getText().trim());
            }
        });

        // Add components to panel
        searchPanel.add(searchField);
    }

    private void searchAndFocus(String username) {
        if (username.isEmpty()) {
            return;
        }

        // Find node with matching username
        Optional<Node> targetNode = graph.nodes()
                .filter(node -> {
                    Object usernameAttr = node.getAttribute("username");
                    return usernameAttr != null &&
                            username.equalsIgnoreCase(String.valueOf(usernameAttr));
                })
                .findFirst();

        if (targetNode.isPresent()) {
            Node node = targetNode.get();

            // Get node position
            double nodeX = node.getNumber("x");
            double nodeY = node.getNumber("y");

            // Set zoom level to show labels (less than LABEL_VISIBILITY_THRESHOLD)
            double targetZoom = ZoomHandler.LABEL_VISIBILITY_THRESHOLD / 2;

            // Set zoom level and center on node
            camera.setViewPercent(targetZoom);
            camera.setViewCenter(nodeX, nodeY, 0);

            // Update visibility for all nodes at this zoom level
            graph.nodes().forEach(n -> {
                Object usernameAttr = n.getAttribute("username");
                if (usernameAttr != null) {
                    if (targetZoom < ZoomHandler.LABEL_VISIBILITY_THRESHOLD) {
                        n.setAttribute("ui.label", usernameAttr.toString());
                    } else {
                        n.setAttribute("ui.label", "");
                    }
                }
            });

            // Clear search field
            searchField.setText("");

            // Highlight the found node
            highlightNode(node);
        } else {
            JOptionPane.showMessageDialog(
                    searchPanel,
                    "User '" + username + "' not found.",
                    "Search Result",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void highlightNode(Node node) {
        String originalStyle = node.getAttribute("ui.style", String.class);
        if (originalStyle == null) {
            originalStyle = "";
        }

        node.setAttribute("ui.style", "fill-color: rgb(255,255,0); size: 40px;");

        final String finalOriginalStyle = originalStyle;
        new Timer(1500, e -> {
            node.setAttribute("ui.style", finalOriginalStyle);
            ((Timer)e.getSource()).stop();
        }).start();
    }

    public JPanel getSearchPanel() {
        return searchPanel;
    }

    // ViewerListener implementation
    @Override
    public void viewClosed(String id) {}

    @Override
    public void buttonPushed(String id) {}

    @Override
    public void buttonReleased(String id) {}

    @Override
    public void mouseOver(String id) {}

    @Override
    public void mouseLeft(String id) {}
}