package Visualizer;

public class StyleSheet {
    // Helper method to create RGB string
    private static String rgb(int r, int g, int b) {
        return String.format("rgb(%d,%d,%d)", r, g, b);
    }

    // Define color components for reuse
    private static final int[] USER_NODE_RGB = {79, 136, 176};
    private static final int[] KOL_NODE_RGB = {212, 99, 99};
    private static final int[] FOLLOW_EDGE_RGB = {67, 160, 71};
    private static final int[] REPOST_EDGE_RGB = {124, 77, 255};
    private static final int[] COMMENT_EDGE_RGB = {255, 111, 0};

    // Public color constants with RGB values for reference
    public static final String USER_NODE_COLOR = rgb(USER_NODE_RGB[0], USER_NODE_RGB[1], USER_NODE_RGB[2]);
    public static final String KOL_NODE_COLOR = rgb(KOL_NODE_RGB[0], KOL_NODE_RGB[1], KOL_NODE_RGB[2]);
    public static final String FOLLOW_EDGE_COLOR = rgb(FOLLOW_EDGE_RGB[0], FOLLOW_EDGE_RGB[1], FOLLOW_EDGE_RGB[2]);
    public static final String REPOST_EDGE_COLOR = rgb(REPOST_EDGE_RGB[0], REPOST_EDGE_RGB[1], REPOST_EDGE_RGB[2]);
    public static final String COMMENT_EDGE_COLOR = rgb(COMMENT_EDGE_RGB[0], COMMENT_EDGE_RGB[1], COMMENT_EDGE_RGB[2]);

    public static final String DEFAULT_STYLE =
            "graph {" +
                    "    padding: 50px;" +
                    "    fill-color: rgb(252, 252, 252);" +
                    "}" +
                    "node {" +
                    "    size: 25px;" +
                    "    fill-color: " + USER_NODE_COLOR + ";" +
                    "    text-size: 12;" +
                    "    text-color: rgb(68, 68, 68);" +
                    "    text-style: bold;" +
                    "    text-alignment: center;" +
                    "    text-background-mode: rounded-box;" +
                    "    text-background-color: rgb(255, 255, 255);" +
                    "    text-padding: 2px;" +
                    "    text-offset: 0px, -30px;" +
                    "    z-index: 1;" +
                    "    fill-mode: plain;" +
                    "    stroke-mode: plain;" +
                    "    stroke-color: rgb(90, 90, 90);" +
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
                    "    shape: line;" +
                    "    arrow-shape: arrow;" +
                    "    arrow-size: 5px, 3px;" +
                    "    size: 1.8px;" +
                    "    z-index: 0;" +
                    "}" +
                    "edge.follow {" +
                    "    fill-color: " + FOLLOW_EDGE_COLOR + ";" +
                    "    size: 1.8px;" +
                    "}" +
                    "edge.repost {" +
                    "    fill-color: " + REPOST_EDGE_COLOR + ";" +
                    "    size: 2.2px;" +
                    "}" +
                    "edge.comment {" +
                    "    fill-color: " + COMMENT_EDGE_COLOR + ";" +
                    "    size: 2.2px;" +
                    "}" +
                    "sprite {" +
                    "    size: 0;" +
                    "}";
}