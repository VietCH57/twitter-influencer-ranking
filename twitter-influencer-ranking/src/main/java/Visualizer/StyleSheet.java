package Visualizer;

public class StyleSheet {
    public static final String DEFAULT_STYLE = """
            node {
                size: 25px;
                fill-color: #6699ff;
                text-size: 12;
                text-color: #000000;
                text-style: bold;
                text-alignment: center;
                text-background-mode: rounded-box;
                text-background-color: white;
                text-padding: 2px;
                text-offset: 0px, -30px;
                z-index: 1;
            }
            node.user {
                size: 25px;
                fill-color: #6699ff;
            }
            node.kol {
                size: 35px;
                fill-color: #ff6666;
                text-size: 14;
                z-index: 2;
            }
            edge {
                arrow-size: 5px, 3px;
                size: 1px;
                z-index: 0;
                fill-color: #999999;
            }
            sprite {
                size: 0;
            }
            """;
}