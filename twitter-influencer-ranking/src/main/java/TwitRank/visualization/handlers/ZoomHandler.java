package TwitRank.visualization.handlers;

import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.geom.Point3;
import org.graphstream.graph.Graph;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.layout.Layout;

import java.awt.Component;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handles zoom functionality for the graph visualization.
 */
public class ZoomHandler {
    public static final double MIN_ZOOM = 0.01;
    public static final double MAX_ZOOM = 5.0;
    public static final double ZOOM_SMOOTHNESS = 0.15;
    public static final double ZOOM_SENSITIVITY = 2.00;
    public static final double ZOOM_STEP = 0.1;

    private final Camera camera;
    private final View view;
    private final Graph graph;
    private final ViewerPipe viewerPipe;
    private final Layout layout;
    private double targetZoom;
    private Point3 lastMousePoint;
    private final ReentrantLock updateLock;

    /**
     * Constructs a new ZoomHandler.
     */
    public ZoomHandler(Viewer viewer, Graph graph, Layout layout) {  // Add Layout parameter
        this.view = viewer.getDefaultView();
        this.camera = view.getCamera();
        this.graph = graph;
        this.layout = layout;  // Store the layout
        this.viewerPipe = viewer.newViewerPipe();
        this.targetZoom = camera.getViewPercent();
        this.lastMousePoint = null;
        this.updateLock = new ReentrantLock();

        setupZoomListener();
    }

    private void setupZoomListener() {
        ((Component) view).addMouseWheelListener(e -> {
            try {
                handleZoom(e);
            } catch (Exception ex) {
                System.err.println("Error handling zoom: " + ex.getMessage());
            }
        });
    }

    private void handleZoom(MouseWheelEvent e) {
        updateLock.lock();
        try {
            Point3 mousePoint = camera.transformPxToGu(e.getX(), e.getY());

            if (lastMousePoint == null) {
                lastMousePoint = mousePoint;
            }

            // Modified zoom factor calculation for more aggressive zooming
            double zoomFactor;
            if (e.getWheelRotation() < 0) {
                // Zooming in
                zoomFactor = Math.pow(0.7, ZOOM_SENSITIVITY * 1.5);
            } else {
                // Zooming out
                zoomFactor = Math.pow(1.3, ZOOM_SENSITIVITY * 1.5);
            }

            targetZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM,
                    camera.getViewPercent() * zoomFactor));

            // Apply smooth zoom
            double currentZoom = camera.getViewPercent();
            double newZoom = currentZoom + (targetZoom - currentZoom) * ZOOM_SMOOTHNESS;
            camera.setViewPercent(newZoom);

            // Update layout forces based on zoom level
            if (layout instanceof SpringBox) {
                SpringBox springBox = (SpringBox) layout;
                double adjustedForce = Math.max(0.05, 0.3 * newZoom);
                springBox.setForce(adjustedForce);

                double adjustedStability = Math.max(0.0001, 0.001 * newZoom);
                springBox.setStabilizationLimit(adjustedStability);
            }

            updateViewCenter(mousePoint);
            updateNodeLabels(newZoom);

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

    /**
     * Updates the node labels based on the current zoom level.
     */
    private void updateNodeLabels(double zoomLevel) {
        boolean showLabels = zoomLevel < 1.0; // Adjusted threshold for label visibility
        String style = "text-visibility-mode: " + (showLabels ? "normal" : "hidden") + ";";

        graph.nodes().forEach(node -> {
            try {
                // Adjust node size based on zoom level for better visibility
                double baseSize = 10.0;
                double sizeMultiplier = Math.min(2.0, 1.0 / zoomLevel);
                String nodeStyle = style + String.format("size: %.1f;", baseSize * sizeMultiplier);
                node.setAttribute("ui.style", nodeStyle);
            } catch (Exception e) {
                // Silently ignore concurrent modification exceptions
            }
        });
    }

    /**
     * Zooms in on the graph.
     */
    public void zoomIn() {
        updateLock.lock();
        try {
            double currentZoom = camera.getViewPercent();
            // More aggressive zoom in
            double newZoom = Math.max(MIN_ZOOM, currentZoom * 0.7);

            Point3 center = camera.getViewCenter();
            camera.setViewPercent(newZoom);

            updateNodeLabels(newZoom);
            targetZoom = newZoom;
        } finally {
            updateLock.unlock();
        }
    }

    /**
     * Zooms out on the graph.
     */
    public void zoomOut() {
        updateLock.lock();
        try {
            double currentZoom = camera.getViewPercent();
            // More aggressive zoom out
            double newZoom = Math.min(MAX_ZOOM, currentZoom * 1.3);

            Point3 center = camera.getViewCenter();
            camera.setViewPercent(newZoom);

            updateNodeLabels(newZoom);
            targetZoom = newZoom;
        } finally {
            updateLock.unlock();
        }
    }

    /**
     * Resets the zoom level to default.
     */
    public void resetZoom() {
        updateLock.lock();
        try {
            camera.setViewPercent(1.0);
            targetZoom = 1.0;
            lastMousePoint = null;
            updateNodeLabels(1.0);
        } finally {
            updateLock.unlock();
        }
    }

    /**
     * Gets the current zoom level.
     */
    public double getCurrentZoom() {
        return camera.getViewPercent();
    }

    /**
     * Checks if labels should be shown at current zoom level.
     */
    public boolean shouldShowLabels() {
        return camera.getViewPercent() < 0.5;
    }
}