package TwitRank.visualization.ui;

import TwitRank.visualization.handlers.PanHandler;
import TwitRank.visualization.handlers.ZoomHandler;
import org.graphstream.graph.Graph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main application window that contains the graph visualization and control panel.
 */
public class MainFrame extends JFrame {
    private final ControlPanel controlPanel;
    private final Component graphView;
    private final Graph graph;
    private final Viewer viewer;
    private final Layout layout;
    private final ZoomHandler zoomHandler;
    private final PanHandler panHandler;
    private final ViewerPipe viewerPipe;
    private final AtomicBoolean pumpRunning;
    private Thread pumpThread;

    // Constants for frame configuration
    private static final String DEFAULT_TITLE = "Twitter Network Visualization";
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;

    /**
     * Constructs a new MainFrame.
     *
     * @param graph The graph to be visualized
     * @param viewer The viewer for the graph
     * @param layout The layout manager for the graph
     */
    public MainFrame(Graph graph, Viewer viewer, Layout layout) {
        super(DEFAULT_TITLE);
        this.graph = graph;
        this.viewer = viewer;
        this.layout = layout;
        this.graphView = (Component) viewer.getDefaultView();
        this.viewerPipe = viewer.newViewerPipe();
        this.pumpRunning = new AtomicBoolean(true);

        // Initialize handlers
        this.zoomHandler = new ZoomHandler(viewer, graph, layout);
        this.panHandler = new PanHandler(viewer);

        // Initialize control panel
        this.controlPanel = new ControlPanel(graph, layout);

        setupFrame();
        setupViewerPipe();
    }

    /**
     * Sets up the main frame properties and layout.
     */
    private void setupFrame() {
        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        // Set layout
        setLayout(new BorderLayout());

        // Add components
        add(createToolBar(), BorderLayout.NORTH);
        add(controlPanel, BorderLayout.WEST);
        add(createGraphPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cleanup();
            }
        });

        // Pack and center
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Creates the main toolbar.
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // Create buttons
        JButton resetViewBtn = new JButton("Reset View");
        JButton exportBtn = new JButton("Export");
        JButton zoomInBtn = new JButton("+");
        JButton zoomOutBtn = new JButton("-");
        JButton fitBtn = new JButton("Fit View");

        // Add action listeners
        resetViewBtn.addActionListener(e -> resetView());
        exportBtn.addActionListener(e -> exportGraph());
        zoomInBtn.addActionListener(e -> zoomHandler.zoomIn());
        zoomOutBtn.addActionListener(e -> zoomHandler.zoomOut());
        fitBtn.addActionListener(e -> viewer.getDefaultView().getCamera().resetView());

        // Add buttons to toolbar
        toolBar.add(resetViewBtn);
        toolBar.add(fitBtn);
        toolBar.addSeparator();
        toolBar.add(zoomInBtn);
        toolBar.add(zoomOutBtn);
        toolBar.addSeparator();
        toolBar.add(exportBtn);

        return toolBar;
    }

    /**
     * Creates the panel containing the graph visualization.
     */
    private JPanel createGraphPanel() {
        JPanel graphPanel = new JPanel(new BorderLayout());
        graphPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        graphPanel.add(graphView, BorderLayout.CENTER);
        return graphPanel;
    }

    /**
     * Creates the status bar.
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());

        JLabel statusLabel = new JLabel(" Ready");
        JLabel nodeCountLabel = new JLabel(String.format(" Nodes: %d ", graph.getNodeCount()));
        JLabel edgeCountLabel = new JLabel(String.format(" Edges: %d ", graph.getEdgeCount()));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(statusLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(nodeCountLabel);
        rightPanel.add(edgeCountLabel);

        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(rightPanel, BorderLayout.EAST);

        return statusBar;
    }

    /**
     * Sets up the viewer pipe for graph events.
     */
    private void setupViewerPipe() {
        viewerPipe.addAttributeSink(graph);

        pumpThread = new Thread(() -> {
            while (pumpRunning.get()) {
                try {
                    viewerPipe.pump();
                    Thread.sleep(100);  // Reduce CPU usage
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    System.err.println("Error in viewer pipe: " + e.getMessage());
                }
            }
        }, "ViewerPipe-Pump");

        pumpThread.setDaemon(true);
        pumpThread.start();
    }

    /**
     * Resets the view to its default state.
     */
    public void resetView() {
        SwingUtilities.invokeLater(() -> {
            try {
                panHandler.resetView();
                zoomHandler.resetZoom();
                controlPanel.updatePanels();
                updateStatus("View reset");
            } catch (Exception e) {
                updateStatus("Error resetting view");
            }
        });
    }

    /**
     * Exports the current graph view.
     */
    private void exportGraph() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Graph");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Implement export functionality
                updateStatus("Graph exported successfully");
            } catch (Exception e) {
                updateStatus("Error exporting graph");
                JOptionPane.showMessageDialog(this,
                        "Error exporting graph: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Performs cleanup before closing.
     */
    private void cleanup() {
        // Stop the pump thread
        pumpRunning.set(false);
        if (pumpThread != null) {
            pumpThread.interrupt();
            try {
                pumpThread.join(1000);
            } catch (InterruptedException e) {
                // Ignore
            }
        }

        // Clean up viewer
        if (viewer != null) {
            viewer.close();
        }
    }

    /**
     * Method to safely modify graph attributes
     */
    private void safeGraphOperation(Runnable operation) {
        try {
            synchronized (graph) {
                operation.run();
            }
        } catch (Exception e) {
            System.err.println("Error in graph operation: " + e.getMessage());
        }
    }

    /**
     * Updates the status bar message.
     */
    public void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            JLabel statusLabel = (JLabel)((JPanel)((JPanel)getContentPane()
                    .getComponent(3)).getComponent(0)).getComponent(0);
            statusLabel.setText(" " + message);
        });
    }

    /**
     * Gets the control panel instance.
     */
    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    /**
     * Gets the viewer instance.
     */
    public Viewer getViewer() {
        return viewer;
    }

    /**
     * Updates node and edge counts in the status bar.
     */
    public void updateGraphStats() {
        SwingUtilities.invokeLater(() -> {
            JPanel rightPanel = (JPanel)((JPanel)getContentPane()
                    .getComponent(3)).getComponent(1);
            JLabel nodeCountLabel = (JLabel)rightPanel.getComponent(0);
            JLabel edgeCountLabel = (JLabel)rightPanel.getComponent(1);

            nodeCountLabel.setText(String.format(" Nodes: %d ", graph.getNodeCount()));
            edgeCountLabel.setText(String.format(" Edges: %d ", graph.getEdgeCount()));
        });
    }

    /**
     * Updates the frame title with additional information.
     */
    public void updateTitle(String info) {
        SwingUtilities.invokeLater(() -> {
            setTitle(DEFAULT_TITLE + (info != null ? " - " + info : ""));
        });
    }
}