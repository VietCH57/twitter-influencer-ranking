package Visualizer;

import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.util.MouseManager;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.util.InteractiveElement;

import java.util.EnumSet;
import java.awt.event.*;
import java.awt.Cursor;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PanHandler implements MouseListener, MouseMotionListener, MouseManager {
    private final View view;
    private final Camera camera;
    private Point3 lastMousePosition;
    private boolean isPanning;
    private final JPanel panel;
    private GraphicGraph graph;

    public PanHandler(View view) {
        this.view = view;
        this.camera = view.getCamera();
        this.isPanning = false;
        this.panel = (JPanel) view;
        view.setMouseManager(this);
    }

    // Let's try with the most basic implementation
    @Override
    public EnumSet<InteractiveElement> getManagedTypes() {
        return EnumSet.noneOf(InteractiveElement.class);
    }

    @Override
    public void init(GraphicGraph graph, View view) {
        this.graph = graph;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            isPanning = true;
            lastMousePosition = camera.transformPxToGu(e.getX(), e.getY());
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            e.consume();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            isPanning = false;
            panel.setCursor(Cursor.getDefaultCursor());
            e.consume();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isPanning) {
            Point3 currentPosition = camera.transformPxToGu(e.getX(), e.getY());
            Point3 center = camera.getViewCenter();

            double dx = currentPosition.x - lastMousePosition.x;
            double dy = currentPosition.y - lastMousePosition.y;

            camera.setViewCenter(
                    center.x - dx,
                    center.y - dy,
                    center.z
            );

            lastMousePosition = currentPosition;
            e.consume();
        }
    }

    @Override
    public void release() {
        this.graph = null;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        event.consume();
    }

    @Override
    public void mouseMoved(MouseEvent event) {}

    @Override
    public void mouseEntered(MouseEvent event) {}

    @Override
    public void mouseExited(MouseEvent event) {}

    public static void install(View view) {
        if (view == null || !(view instanceof JPanel)) return;
        PanHandler handler = new PanHandler(view);
        JPanel panel = (JPanel) view;
        panel.addMouseListener(handler);
        panel.addMouseMotionListener(handler);
    }
}