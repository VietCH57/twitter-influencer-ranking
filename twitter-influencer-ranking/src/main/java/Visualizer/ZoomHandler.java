package Visualizer;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.geom.Point3;

import java.awt.event.*;
import java.util.concurrent.locks.ReentrantLock;

public class ZoomHandler implements MouseWheelListener {
    private final View view;
    private final Camera camera;
    private final Graph graph;
    public static final double ZOOM_FACTOR = 1.1;
    public static final double MIN_ZOOM = 0.01;
    public static final double MAX_ZOOM = 10.0;
    public static final double LABEL_VISIBILITY_THRESHOLD = 0.075;
    public static final double ZOOM_SMOOTHNESS = 0.15;
    public static final double ZOOM_SENSITIVITY = 2.00;

    private double targetZoom;
    private Point3 lastMousePoint;
    private final ReentrantLock updateLock;

    public ZoomHandler(View view, Graph graph) {
        this.view = view;
        this.camera = view.getCamera();
        this.graph = graph;
        this.targetZoom = camera.getViewPercent();
        this.lastMousePoint = null;
        this.updateLock = new ReentrantLock();
        updateLabelVisibility(camera.getViewPercent());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        updateLock.lock();
        try {
            // Get mouse position in graph units before zoom
            Point3 mousePoint = camera.transformPxToGu(e.getX(), e.getY());

            if (lastMousePoint == null) {
                lastMousePoint = mousePoint;
            }

            // Calculate zoom factor based on wheel rotation
            double zoomFactor;
            if (e.getWheelRotation() < 0) {
                // Zooming in
                zoomFactor = Math.pow(0.7, ZOOM_SENSITIVITY);
            } else {
                // Zooming out
                zoomFactor = Math.pow(1.3, ZOOM_SENSITIVITY);
            }

            // Calculate and constrain target zoom
            targetZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM,
                    camera.getViewPercent() * zoomFactor));

            // Apply smooth zoom
            double currentZoom = camera.getViewPercent();
            double newZoom = currentZoom + (targetZoom - currentZoom) * ZOOM_SMOOTHNESS;
            camera.setViewPercent(newZoom);

            // Update view center to follow cursor
            updateViewCenter(mousePoint);

            // Update labels
            updateLabelVisibility(newZoom);

            lastMousePoint = mousePoint;
        } finally {
            updateLock.unlock();
        }
    }

    private void updateViewCenter(Point3 mousePoint) {
        Point3 currentCenter = camera.getViewCenter();
        double interpolationFactor = ZOOM_SMOOTHNESS;

        double newX = currentCenter.x + (mousePoint.x - currentCenter.x) * interpolationFactor;
        double newY = currentCenter.y + (mousePoint.y - currentCenter.y) * interpolationFactor;

        camera.setViewCenter(newX, newY, 0);
    }

    private void updateLabelVisibility(double zoomLevel) {
        graph.nodes().forEach(node -> {
            Object username = node.getAttribute("username");
            if (username != null) {
                if (zoomLevel < LABEL_VISIBILITY_THRESHOLD) {
                    node.setAttribute("ui.label", username.toString());
                } else {
                    node.setAttribute("ui.label", "");
                }
            }
        });
    }

    public static void install(Viewer viewer, Graph graph) {
        View view = viewer.getDefaultView();
        if (view == null) return;

        ZoomHandler handler = new ZoomHandler(view, graph);
        if (view instanceof javax.swing.JPanel) {
            ((javax.swing.JPanel) view).addMouseWheelListener(handler);
        }
    }
}