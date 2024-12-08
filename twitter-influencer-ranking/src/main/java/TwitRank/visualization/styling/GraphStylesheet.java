package TwitRank.visualization.styling;

import org.graphstream.graph.Graph;

/**
 * Handles the styling of the graph visualization using GraphStream's CSS-like styling system.
 */
public class GraphStylesheet {
    // Base node styling
    private static final String NODE_STYLE =
            "node {" +
                    "   size: 10px;" +
                    "   fill-color: #66ccff;" +
                    "   text-size: 12;" +
                    "   text-visibility-mode: hidden;" +
                    "   text-background-mode: rounded-box;" +
                    "   text-background-color: white;" +
                    "   text-padding: 2px;" +
                    "   text-offset: 0px, -5px;" +
                    "}";

    // Highlighted node styling
    private static final String HIGHLIGHTED_NODE_STYLE =
            "node.highlighted {" +
                    "   fill-color: #ff6666;" +
                    "   size: 15px;" +
                    "   text-visibility-mode: normal;" +
                    "}";

    // Selected node styling
    private static final String SELECTED_NODE_STYLE =
            "node.selected {" +
                    "   fill-color: #ff0000;" +
                    "   size: 20px;" +
                    "   stroke-mode: plain;" +
                    "   stroke-color: blue;" +
                    "   stroke-width: 2px;" +
                    "   text-visibility-mode: normal;" +
                    "}";

    // Base edge styling
    private static final String EDGE_STYLE =
            "edge {" +
                    "   fill-color: #999999;" +
                    "   size: 1px;" +
                    "   arrow-size: 8px, 4px;" +
                    "}";

    // Edge type-specific styling
    private static final String EDGE_TYPES_STYLE =
            "edge.follow { fill-color: #666666; }" +
                    "edge.retweet { fill-color: #00ff00; size: 2px; }" +
                    "edge.reply { fill-color: #ff0000; size: 2px; }";

    // Sprite styling for any additional visual elements
    private static final String SPRITE_STYLE =
            "sprite {" +
                    "   size: 0px;" +
                    "   text-visibility-mode: normal;" +
                    "   text-size: 11;" +
                    "}";

    // Complete stylesheet combining all styles
    public static final String DEFAULT_STYLESHEET =
            NODE_STYLE +
                    HIGHLIGHTED_NODE_STYLE +
                    SELECTED_NODE_STYLE +
                    EDGE_STYLE +
                    EDGE_TYPES_STYLE +
                    SPRITE_STYLE;

    /**
     * Applies the default stylesheet to a graph.
     *
     * @param graph The graph to apply the stylesheet to
     */
    public static void applyTo(org.graphstream.graph.Graph graph) {
        graph.setAttribute("ui.quality", true);
        graph.setAttribute("ui.antialias", true);

        String stylesheet =
                "node {" +
                        "   size: 10px;" +
                        "   fill-color: #66B2FF;" +
                        "   stroke-mode: plain;" +
                        "   stroke-color: #004C99;" +
                        "   stroke-width: 1px;" +
                        "   text-alignment: center;" +
                        "   text-size: 12;" +
                        "   text-color: #000000;" +
                        "   text-style: bold;" +
                        "   text-background-mode: rounded-box;" +
                        "   text-background-color: #FFFFFF80;" +
                        "   text-padding: 2px;" +
                        "   text-offset: 0px, -8px;" +
                        "}" +
                        "node.highlighted {" +
                        "   fill-color: #FF3333;" +
                        "   size: 15px;" +
                        "}" +
                        "edge {" +
                        "   size: 1px;" +
                        "   fill-color: #999999;" +
                        "   arrow-size: 8px, 4px;" +
                        "}" +
                        "edge.follow { fill-color: #66B2FF; }" +
                        "edge.retweet { fill-color: #33CC33; }" +
                        "edge.reply { fill-color: #FF9933; }";

        graph.setAttribute("ui.stylesheet", stylesheet);
    }

    public static void applyCustomStyle(Graph graph, String customStyle) {
        graph.setAttribute("ui.stylesheet", DEFAULT_STYLESHEET + customStyle);
    }

    /**
     * Updates the node size styling based on a metric.
     *
     * @param minSize Minimum node size
     * @param maxSize Maximum node size
     * @return CSS rule for the new node sizing
     */
    public static String createNodeSizeStyle(double minSize, double maxSize) {
        return "node {" +
                "   size: " + minSize + "px;" +
                "   min-size: " + minSize + "px;" +
                "   max-size: " + maxSize + "px;" +
                "}";
    }

    /**
     * Creates a style for nodes based on their degree (number of connections).
     *
     * @param baseSize Base size for nodes
     * @param scaleFactor Factor to scale size by degree
     * @return CSS rule for degree-based sizing
     */
    public static String createDegreeBasedStyle(double baseSize, double scaleFactor) {
        return "node {" +
                "   size: " + baseSize + "px;" +
                "   size-mode: dyn-size;" +
                "   size-factor: " + scaleFactor + ";" +
                "}";
    }

    /**
     * Updates the edge visibility style based on edge type.
     *
     * @param edgeType The type of edge to show ("follow", "retweet", or "reply")
     * @return CSS rule for edge visibility
     */
    public static String createEdgeVisibilityStyle(String edgeType) {
        return "edge {" +
                "   visibility-mode: hidden;" +
                "}" +
                "edge." + edgeType + " {" +
                "   visibility-mode: normal;" +
                "}";
    }

    /**
     * Creates a hover effect style for nodes.
     *
     * @return CSS rule for node hover effects
     */
    public static String createHoverEffectStyle() {
        return "node:hover {" +
                "   fill-color: #ff9900;" +
                "   size: 15px;" +
                "   text-visibility-mode: normal;" +
                "   z-index: 100;" +
                "}";
    }
}