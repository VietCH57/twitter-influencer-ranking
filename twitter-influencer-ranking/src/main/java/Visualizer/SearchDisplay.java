package Visualizer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;

public class SearchDisplay implements ViewerListener {
    public static final int PANEL_WIDTH = 300;
    public static final int PANEL_HEIGHT = 40;
    private static final int SEARCH_FIELD_HEIGHT = 30;
    private static final int CORNER_RADIUS = 15;
    private static final String SEARCH_PLACEHOLDER = "Search by username...";

    private final JTextField searchField;
    private final Graph graph;
    private final Camera camera;
    private final JPanel searchPanel;
    private boolean isClickActivated = false;

    public SearchDisplay(Graph graph, Viewer viewer) {
        this.graph = graph;
        this.camera = viewer.getDefaultView().getCamera();

        // Create search panel with absolute positioning
        searchPanel = new JPanel(null);
        searchPanel.setOpaque(false);
        searchPanel.setBorder(null);

        // Create custom search field with rounded corners
        searchField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);

                // Draw rounded border
                g2.setColor(new Color(180, 180, 180));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void setBorder(javax.swing.border.Border border) {
                super.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            }

            @Override
            protected void processKeyEvent(KeyEvent e) {
                if (isClickActivated) {
                    super.processKeyEvent(e);
                }
            }

            @Override
            public boolean isFocusable() {
                return isClickActivated;
            }
        };

        // Make the text field transparent
        searchField.setOpaque(false);
        searchField.setBackground(new Color(255, 255, 255, 240));
        searchField.setFocusable(false); // Initially not focusable

        // Style and position the search field
        searchField.setPreferredSize(new Dimension(PANEL_WIDTH - 20, SEARCH_FIELD_HEIGHT));
        searchField.setBounds(10, 5, PANEL_WIDTH - 20, SEARCH_FIELD_HEIGHT);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(Color.GRAY);
        searchField.setText(SEARCH_PLACEHOLDER);
        searchField.setToolTipText("Click to search");

        // Add mouse listener for click behavior
        searchField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isClickActivated) {
                    isClickActivated = true;
                    searchField.setFocusable(true);
                    searchField.requestFocus();
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
        });

        // Add focus listener
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    deactivateSearch();
                }
            }
        });

        // Add action listener for Enter key
        searchField.addActionListener(e -> {
            if (isClickActivated) {
                String text = searchField.getText().trim();
                if (!text.isEmpty()) {
                    searchAndFocus(text);
                }
            }
        });

        // Add key listener for Escape key
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && isClickActivated) {
                    deactivateSearch();
                }
            }
        });

        // Add search field to panel
        searchPanel.add(searchField);
    }

    private void deactivateSearch() {
        isClickActivated = false;
        searchField.setFocusable(false);
        searchField.setForeground(Color.GRAY);
        searchField.setText(SEARCH_PLACEHOLDER);
    }



    private void searchAndFocus(String username) {
        if (username.isEmpty() || username.equals(SEARCH_PLACEHOLDER)) {
            return;
        }

        // Find node with matching username (case-insensitive)
        Optional<Node> targetNode = graph.nodes()
                .filter(node -> {
                    Object usernameAttr = node.getAttribute("username");
                    return usernameAttr != null &&
                            username.equalsIgnoreCase(String.valueOf(usernameAttr));
                })
                .findFirst();

        if (targetNode.isPresent()) {
            Node node = targetNode.get();
            focusOnNode(node);
            highlightNode(node);
            resetSearchField();
        } else {
            showNotFoundMessage(username);
        }
    }

    private void focusOnNode(Node node) {
        // Get node position
        double nodeX = node.getNumber("x");
        double nodeY = node.getNumber("y");

        // Set zoom level to show labels
        double targetZoom = ZoomHandler.LABEL_VISIBILITY_THRESHOLD / 2;

        // Animate to the node's position
        camera.setViewPercent(targetZoom);
        camera.setViewCenter(nodeX, nodeY, 0);

        // Update node labels visibility
        updateNodeLabels(targetZoom);
    }

    private void updateNodeLabels(double zoom) {
        graph.nodes().forEach(n -> {
            Object usernameAttr = n.getAttribute("username");
            if (usernameAttr != null) {
                n.setAttribute("ui.label",
                        zoom < ZoomHandler.LABEL_VISIBILITY_THRESHOLD ?
                                usernameAttr.toString() : "");
            }
        });
    }

    private void resetSearchField() {
        isClickActivated = false;
        searchField.setText(SEARCH_PLACEHOLDER);
        searchField.setForeground(Color.GRAY);
        searchField.transferFocus();
    }

    private void showNotFoundMessage(String username) {
        JOptionPane.showMessageDialog(
                searchPanel,
                "User '" + username + "' not found.",
                "Search Result",
                JOptionPane.INFORMATION_MESSAGE
        );
        resetSearchField();
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
    @Override public void viewClosed(String id) {}
    @Override public void buttonPushed(String id) {}
    @Override public void buttonReleased(String id) {}
    @Override public void mouseOver(String id) {}
    @Override public void mouseLeft(String id) {}
}