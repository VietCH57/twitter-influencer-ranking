package Visualizer;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.geom.Point3;

import java.awt.event.*;

public class ZoomHandler implements MouseWheelListener {
    private final View view;
    private final Camera camera;
    private final Graph graph;
    public static final double MIN_ZOOM = 0.001;
    public static final double MAX_ZOOM = 10.0;
    public static final double LABEL_VISIBILITY_THRESHOLD = 0.075;
    public static final double ZOOM_SENSITIVITY = 0.15;

    public ZoomHandler(View view, Graph graph) {
        this.view = view;
        this.camera = view.getCamera();
        this.graph = graph;
        updateVisibility(camera.getViewPercent());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Get mouse position in graph units before zoom
        Point3 mousePoint = camera.transformPxToGu(e.getX(), e.getY());
        Point3 currentCenter = camera.getViewCenter();

        // Calculate new zoom level
        double currentZoom = camera.getViewPercent();
        double zoomFactor = e.getWheelRotation() < 0 ? (1.0 - ZOOM_SENSITIVITY) : (1.0 + ZOOM_SENSITIVITY);
        double newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, currentZoom * zoomFactor));

        // Calculate the offset between mouse point and center
        double dx = mousePoint.x - currentCenter.x;
        double dy = mousePoint.y - currentCenter.y;

        // Apply zoom
        camera.setViewPercent(newZoom);

        // Calculate new center to keep mouse point stable
        double zoomRatio = newZoom / currentZoom;
        double newX = mousePoint.x - dx * zoomRatio;
        double newY = mousePoint.y - dy * zoomRatio;

        camera.setViewCenter(newX, newY, currentCenter.z);

        // Update visibility for labels only
        updateVisibility(newZoom);
    }

    private void updateVisibility(double zoomLevel) {
        // Update labels visibility only
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