package Visualizer;

public class StyleSheet {
    public static final String DEFAULT_STYLE = """
            graph {
                padding: 50px;
                fill-color: white;
            }
            node {
                size: 25px;
                fill-color: rgb(180,214,255);
                text-size: 12;
                text-color: rgb(74,74,74);
                text-style: bold;
                text-alignment: center;
                text-background-mode: rounded-box;
                text-background-color: white;
                text-padding: 2px;
                text-offset: 0px, -30px;
                z-index: 1;
                fill-mode: plain;
                stroke-mode: plain;
                stroke-color: rgb(100,100,100);
                stroke-width: 1px;
            }
            node.user {
                size: 25px;
                fill-color: rgb(180,214,255);
                stroke-width: 1px;
            }
            node.kol {
                size: 35px;
                fill-color: rgb(255,180,180);
                text-size: 14;
                z-index: 2;
                stroke-width: 2px;
            }
            edge {
                shape: line;
                arrow-shape: arrow;
                arrow-size: 5px, 3px;
                size: 1.5px;
                z-index: 0;
            }
            edge.useruser {
                fill-color: rgb(180,229,180);
            }
            edge.userkol {
                fill-color: rgb(223,180,255);
                size: 2px;
            }
            edge.kolany {
                fill-color: rgb(255,215,180);
                size: 2px;
            }
            sprite {
                size: 0;
            }
            """;
}