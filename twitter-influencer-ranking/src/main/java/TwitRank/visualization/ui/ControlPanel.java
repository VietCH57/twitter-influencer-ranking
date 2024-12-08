package TwitRank.visualization.ui;

import TwitRank.visualization.ui.components.*;
import org.graphstream.graph.Graph;
import org.graphstream.ui.layout.Layout;
import javax.swing.*;
import java.awt.*;

/**
 * Main control panel containing all UI components for graph manipulation.
 */
public class ControlPanel extends JPanel {
    private final SearchPanel searchPanel;
    private final SizeControlPanel sizePanel;
    private final EdgeFilterPanel filterPanel;
    private final LayoutControlPanel layoutPanel;
    private final StatisticsPanel statsPanel;

    // Constants for panel styling
    private static final int PANEL_WIDTH = 250;
    private static final int PANEL_PADDING = 10;
    private static final Color PANEL_BACKGROUND = new Color(245, 245, 245);
    private static final String PANEL_TITLE = "Control Panel";

    /**
     * Constructs a new ControlPanel.
     *
     * @param graph The graph being visualized
     * @param layout The layout manager for the graph
     */
    public ControlPanel(Graph graph, Layout layout) {
        setupPanelProperties();

        // Initialize sub-panels
        searchPanel = new SearchPanel(graph);
        sizePanel = new SizeControlPanel(graph);
        filterPanel = new EdgeFilterPanel(graph);
        layoutPanel = new LayoutControlPanel(graph, layout);
        statsPanel = new StatisticsPanel(graph);

        // Add title to panel
        addTitleLabel();

        // Add components with spacing
        addComponentsWithSpacing();
    }

    /**
     * Sets up the basic properties of the control panel.
     */
    private void setupPanelProperties() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(PANEL_WIDTH, getPreferredSize().height));
        setBorder(BorderFactory.createEmptyBorder(PANEL_PADDING, PANEL_PADDING,
                PANEL_PADDING, PANEL_PADDING));
        setBackground(PANEL_BACKGROUND);
    }

    /**
     * Adds the title label to the panel.
     */
    private void addTitleLabel() {
        JLabel titleLabel = new JLabel(PANEL_TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(15));
    }

    /**
     * Adds all components to the panel with proper spacing.
     */
    private void addComponentsWithSpacing() {
        // Add search panel
        addComponentWithHeader("Search", searchPanel);

        // Add size control panel
        addComponentWithHeader("Node Size Controls", sizePanel);

        // Add edge filter panel
        addComponentWithHeader("Edge Filters", filterPanel);

        // Add layout control panel
        addComponentWithHeader("Layout Controls", layoutPanel);

        // Add statistics panel
        addComponentWithHeader("Statistics", statsPanel);

        // Add remaining vertical space
        add(Box.createVerticalGlue());
    }

    /**
     * Adds a component with a header label and spacing.
     *
     * @param headerText The text for the header
     * @param component The component to add
     */
    private void addComponentWithHeader(String headerText, JComponent component) {
        // Add section header
        JLabel header = new JLabel(headerText);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(header);
        add(Box.createVerticalStrut(5));

        // Add component
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(component);
        add(Box.createVerticalStrut(15));

        // Add separator
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setMaximumSize(new Dimension(PANEL_WIDTH, 1));
        add(separator);
        add(Box.createVerticalStrut(15));
    }

    /**
     * Updates all panels with current graph state.
     */
    public void updatePanels() {
        searchPanel.clearSearch();
        sizePanel.resetSizes();
        filterPanel.showAllEdges();
        layoutPanel.resetLayout();
        statsPanel.updateStatistics();
    }

    /**
     * Gets the search panel instance.
     *
     * @return The search panel
     */
    public SearchPanel getSearchPanel() {
        return searchPanel;
    }

    /**
     * Gets the size control panel instance.
     *
     * @return The size control panel
     */
    public SizeControlPanel getSizePanel() {
        return sizePanel;
    }

    /**
     * Gets the edge filter panel instance.
     *
     * @return The edge filter panel
     */
    public EdgeFilterPanel getFilterPanel() {
        return filterPanel;
    }

    /**
     * Gets the layout control panel instance.
     *
     * @return The layout control panel
     */
    public LayoutControlPanel getLayoutPanel() {
        return layoutPanel;
    }

    /**
     * Gets the statistics panel instance.
     *
     * @return The statistics panel
     */
    public StatisticsPanel getStatsPanel() {
        return statsPanel;
    }

    /**
     * Sets the enabled state of all controls.
     *
     * @param enabled true to enable controls, false to disable
     */
    public void setControlsEnabled(boolean enabled) {
        searchPanel.setEnabled(enabled);
        sizePanel.setEnabled(enabled);
        filterPanel.setEnabled(enabled);
        layoutPanel.setEnabled(enabled);
        statsPanel.setEnabled(enabled);
    }
}