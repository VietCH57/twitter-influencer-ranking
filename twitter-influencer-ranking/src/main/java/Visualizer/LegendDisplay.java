package Visualizer;

import javax.swing.*;
import java.awt.*;

public class LegendDisplay extends JPanel {
    private static final int PANEL_WIDTH = 180;
    private static final int PANEL_HEIGHT = 150;
    private static final int MARGIN = 15;
    private static final int ITEM_HEIGHT = 24;
    private static final int SYMBOL_SIZE = 14;
    private static final int CORNER_RADIUS = 15;
    private static final Color TEXT_COLOR = new Color(0, 0, 0);

    private static final Color USER_NODE_COLOR = parseRgbString(StyleSheet.USER_NODE_COLOR);
    private static final Color KOL_NODE_COLOR = parseRgbString(StyleSheet.KOL_NODE_COLOR);
    private static final Color FOLLOW_EDGE_COLOR = parseRgbString(StyleSheet.FOLLOW_EDGE_COLOR);
    private static final Color REPOST_EDGE_COLOR = parseRgbString(StyleSheet.REPOST_EDGE_COLOR);
    private static final Color COMMENT_EDGE_COLOR = parseRgbString(StyleSheet.COMMENT_EDGE_COLOR);

    public LegendDisplay() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(0, 0, 0, 0)); // Transparent background
        setBorder(null); // Remove the border
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Draw background with rounded corners
        g2d.setColor(new Color(255, 255, 255, 240));
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);

        // Calculate total content height
        int titleHeight = ITEM_HEIGHT;
        int itemsHeight = ITEM_HEIGHT * 5;
        int spacingAfterTitle = 5;
        int totalContentHeight = titleHeight + spacingAfterTitle + itemsHeight;

        // Calculate starting Y position to center content vertically
        int startY = (PANEL_HEIGHT - totalContentHeight) / 2;
        int y = startY;

        // Draw title
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("Legend", MARGIN, y + (ITEM_HEIGHT * 2/3));
        y += titleHeight + spacingAfterTitle;

        // Draw items
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Nodes
        drawNodeSymbol(g2d, USER_NODE_COLOR, MARGIN, y);
        g2d.drawString("Regular User", MARGIN + SYMBOL_SIZE + 12, y + (ITEM_HEIGHT * 2/3));
        y += ITEM_HEIGHT;

        drawNodeSymbol(g2d, KOL_NODE_COLOR, MARGIN, y);
        g2d.drawString("Key Opinion Leader", MARGIN + SYMBOL_SIZE + 12, y + (ITEM_HEIGHT * 2/3));
        y += ITEM_HEIGHT;

        // Edges
        drawEdgeSymbol(g2d, FOLLOW_EDGE_COLOR, MARGIN, y);
        g2d.drawString("Follow", MARGIN + SYMBOL_SIZE + 12, y + (ITEM_HEIGHT * 2/3));
        y += ITEM_HEIGHT;

        drawEdgeSymbol(g2d, REPOST_EDGE_COLOR, MARGIN, y);
        g2d.drawString("Repost", MARGIN + SYMBOL_SIZE + 12, y + (ITEM_HEIGHT * 2/3));
        y += ITEM_HEIGHT;

        drawEdgeSymbol(g2d, COMMENT_EDGE_COLOR, MARGIN, y);
        g2d.drawString("Comment", MARGIN + SYMBOL_SIZE + 12, y + (ITEM_HEIGHT * 2/3));
    }

    private void drawNodeSymbol(Graphics2D g2d, Color color, int x, int y) {
        int symbolY = y + (ITEM_HEIGHT - SYMBOL_SIZE) / 2;
        g2d.setColor(color);
        g2d.fillOval(x, symbolY, SYMBOL_SIZE, SYMBOL_SIZE);
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawOval(x, symbolY, SYMBOL_SIZE, SYMBOL_SIZE);
    }

    private void drawEdgeSymbol(Graphics2D g2d, Color color, int x, int y) {
        int symbolY = y + ITEM_HEIGHT / 2;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawLine(x, symbolY, x + SYMBOL_SIZE, symbolY);

        int[] xPoints = {x + SYMBOL_SIZE - 5, x + SYMBOL_SIZE, x + SYMBOL_SIZE - 5};
        int[] yPoints = {symbolY - 4, symbolY, symbolY + 4};
        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    public static void install(JFrame frame) {
        LegendDisplay legend = new LegendDisplay();
        legend.setBounds(20, 20, PANEL_WIDTH, PANEL_HEIGHT);

        JPanel glassPane = (JPanel) frame.getGlassPane();
        glassPane.setLayout(null);
        glassPane.add(legend);
        glassPane.setVisible(true);
    }
}