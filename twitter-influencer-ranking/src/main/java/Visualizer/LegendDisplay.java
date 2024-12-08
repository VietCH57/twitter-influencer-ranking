package Visualizer;

import javax.swing.*;
import java.awt.*;

public class LegendDisplay extends JPanel {
    private static final int PANEL_WIDTH = 180;
    private static final int PANEL_HEIGHT = 150;
    private static final int MARGIN = 15;
    private static final int ITEM_HEIGHT = 22;
    private static final int SYMBOL_SIZE = 12;

    private static final Color USER_NODE_COLOR = parseRgbString(StyleSheet.USER_NODE_COLOR);
    private static final Color KOL_NODE_COLOR = parseRgbString(StyleSheet.KOL_NODE_COLOR);
    private static final Color FOLLOW_EDGE_COLOR = parseRgbString(StyleSheet.FOLLOW_EDGE_COLOR);
    private static final Color REPOST_EDGE_COLOR = parseRgbString(StyleSheet.REPOST_EDGE_COLOR);
    private static final Color COMMENT_EDGE_COLOR = parseRgbString(StyleSheet.COMMENT_EDGE_COLOR);

    public LegendDisplay() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(255, 255, 255, 220));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setLayout(null);
    }

    private static Color parseRgbString(String rgbString) {
        String[] components = rgbString
                .replace("rgb(", "")
                .replace(")", "")
                .split(",");
        return new Color(
                Integer.parseInt(components[0].trim()),
                Integer.parseInt(components[1].trim()),
                Integer.parseInt(components[2].trim())
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calculate vertical centering
        int totalContentHeight = ITEM_HEIGHT * 5 + 10; // 5 items + title spacing
        int startY = (PANEL_HEIGHT - totalContentHeight) / 2;
        int y = startY;

        // Draw title
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Legend", MARGIN, y + 12);
        y += ITEM_HEIGHT + 5; // Extra spacing after title

        // Draw items with consistent spacing
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));

        // Nodes
        drawNodeSymbol(g2d, USER_NODE_COLOR, MARGIN, y);
        g2d.drawString("Regular User", MARGIN + SYMBOL_SIZE + 10, y + 9);
        y += ITEM_HEIGHT;

        drawNodeSymbol(g2d, KOL_NODE_COLOR, MARGIN, y);
        g2d.drawString("Key Opinion Leader", MARGIN + SYMBOL_SIZE + 10, y + 9);
        y += ITEM_HEIGHT;

        // Edges
        drawEdgeSymbol(g2d, FOLLOW_EDGE_COLOR, MARGIN, y);
        g2d.drawString("Follow", MARGIN + SYMBOL_SIZE + 10, y + 9);
        y += ITEM_HEIGHT;

        drawEdgeSymbol(g2d, REPOST_EDGE_COLOR, MARGIN, y);
        g2d.drawString("Repost", MARGIN + SYMBOL_SIZE + 10, y + 9);
        y += ITEM_HEIGHT;

        drawEdgeSymbol(g2d, COMMENT_EDGE_COLOR, MARGIN, y);
        g2d.drawString("Comment", MARGIN + SYMBOL_SIZE + 10, y + 9);
    }

    private void drawNodeSymbol(Graphics2D g2d, Color color, int x, int y) {
        g2d.setColor(color);
        g2d.fillOval(x, y, SYMBOL_SIZE, SYMBOL_SIZE);
        g2d.setColor(Color.GRAY);
        g2d.drawOval(x, y, SYMBOL_SIZE, SYMBOL_SIZE);
    }

    private void drawEdgeSymbol(Graphics2D g2d, Color color, int x, int y) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x, y + SYMBOL_SIZE/2, x + SYMBOL_SIZE, y + SYMBOL_SIZE/2);

        // Draw arrow
        int[] xPoints = {x + SYMBOL_SIZE - 4, x + SYMBOL_SIZE, x + SYMBOL_SIZE - 4};
        int[] yPoints = {y + SYMBOL_SIZE/2 - 3, y + SYMBOL_SIZE/2, y + SYMBOL_SIZE/2 + 3};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    public static void install(JFrame frame) {
        LegendDisplay legend = new LegendDisplay();
        legend.setBounds(10, 50, PANEL_WIDTH, PANEL_HEIGHT);

        JPanel glassPane = (JPanel) frame.getGlassPane();
        glassPane.setLayout(null);
        glassPane.add(legend);
        glassPane.setVisible(true);
    }
}