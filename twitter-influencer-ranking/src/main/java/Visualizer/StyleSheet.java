package Visualizer;

public class StyleSheet {
    // Modern color palette constants
    public static final String BACKGROUND_COLOR = "rgb(27, 32, 43)";
    public static final String USER_NODE_COLOR = "rgb(88, 180, 204)";
    public static final String KOL_NODE_COLOR = "rgb(255, 126, 103)";
    public static final String FOLLOW_EDGE_COLOR = "rgb(145, 151, 174)";
    public static final String REPOST_EDGE_COLOR = "rgb(130, 207, 174)";
    public static final String COMMENT_EDGE_COLOR = "rgb(255, 183, 121)";
    public static final String TEXT_COLOR = "rgb(233, 236, 241)";

    public static final String DEFAULT_STYLE =
            "graph {" +
                    "    padding: 50px;" +
                    "    fill-color: " + BACKGROUND_COLOR + ";" +
                    "}" +
                    "node {" +
                    "    size: 25px;" +
                    "    fill-color: " + USER_NODE_COLOR + ";" +
                    "    text-size: 12;" +
                    "    text-color: " + TEXT_COLOR + ";" +
                    "    text-style: bold;" +
                    "    text-alignment: center;" +
                    "    text-background-mode: rounded-box;" +
                    "    text-background-color: rgba(27, 32, 43, 0.8);" +
                    "    text-padding: 2px;" +
                    "    text-offset: 0px, -30px;" +
                    "    z-index: 1;" +
                    "    fill-mode: plain;" +
                    "    stroke-mode: plain;" +
                    "    stroke-color: rgba(255, 255, 255, 0.2);" +
                    "    stroke-width: 1px;" +
                    "}" +
                    "node.user {" +
                    "    size: 25px;" +
                    "    fill-color: " + USER_NODE_COLOR + ";" +
                    "    stroke-width: 1px;" +
                    "}" +
                    "node.kol {" +
                    "    size: 35px;" +
                    "    fill-color: " + KOL_NODE_COLOR + ";" +
                    "    text-size: 14;" +
                    "    z-index: 2;" +
                    "    stroke-width: 2px;" +
                    "}" +
                    "edge {" +
                    "    shape: cubic-curve;" +
                    "    arrow-shape: arrow;" +
                    "    arrow-size: 5px, 3px;" +
                    "    size: 1.5px;" +
                    "    z-index: 0;" +
                    "}" +
                    "edge.follow {" +
                    "    fill-color: " + FOLLOW_EDGE_COLOR + ";" +
                    "    size: 1.5px;" +
                    "}" +
                    "edge.repost {" +
                    "    fill-color: " + REPOST_EDGE_COLOR + ";" +
                    "    size: 2px;" +
                    "}" +
                    "edge.comment {" +
                    "    fill-color: " + COMMENT_EDGE_COLOR + ";" +
                    "    size: 2px;" +
                    "}" +
                    "sprite {" +
                    "    size: 0;" +
                    "}";
}