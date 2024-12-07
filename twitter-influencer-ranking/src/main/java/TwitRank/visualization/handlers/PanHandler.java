package TwitRank.visualization.handlers;

import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.geom.Point3;

import java.awt.*;
import java.awt.event.*;

/**
 * Handles panning functionality for the graph visualization.
 * Provides smooth panning using right mouse button.
 */
public class PanHandler {
    private final Camera camera;
    private final View view;
    private Point lastMousePos;
    private boolean isPanning;

    // Constants for panning behavior
    private static final double PAN_SMOOTHNESS = 1.0;
    private static final int RIGHT_MOUSE_BUTTON = MouseEvent.BUTTON3;

    /**
     * Constructs a new PanHandler.
     *
     * @param viewer The viewer containing the view to be panned
     */
    public PanHandler(org.graphstream.ui.view.Viewer viewer) {
        this.view = viewer.getDefaultView();
        this.camera = view.getCamera();
        this.lastMousePos = null;
        this.isPanning = false;
        setupPanListeners();
    }

    /**
     * Sets up mouse listeners for panning functionality.
     */
    private void setupPanListeners() {
        Component viewComponent = (Component) view;

        // Mouse listener for handling press and release events
        viewComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == RIGHT_MOUSE_BUTTON) {
                    startPanning(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == RIGHT_MOUSE_BUTTON) {
                    stopPanning();
                }
            }
        });

        // Mouse motion listener for handling drag events
        viewComponent.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isPanning) {
                    handlePanning(e);
                }
            }
        });
    }

    /**
     * Initiates the panning operation.
     *
     * @param e MouseEvent that triggered the panning
     */
    private void startPanning(MouseEvent e) {
        isPanning = true;
        lastMousePos = e.getPoint();
        view.getCamera().setAutoFitView(false);
        ((Component) view).setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    /**
     * Stops the panning operation.
     */
    private void stopPanning() {
        isPanning = false;
        lastMousePos = null;
        ((Component) view).setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Handles the panning movement.
     *
     * @param e MouseEvent containing the current mouse position
     */
    private void handlePanning(MouseEvent e) {
        if (lastMousePos != null) {
            // Convert screen coordinates to graph coordinates
            Point3 p1 = camera.transformPxToGu(lastMousePos.x, lastMousePos.y);
            Point3 p2 = camera.transformPxToGu(e.getX(), e.getY());

            // Calculate the difference in position
            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;

            // Get current view center
            Point3 center = camera.getViewCenter();

            // Apply smooth panning
            double newX = center.x - dx * PAN_SMOOTHNESS;
            double newY = center.y - dy * PAN_SMOOTHNESS;

            // Update camera position
            camera.setViewCenter(newX, newY, center.z);
        }

        // Update last mouse position
        lastMousePos = e.getPoint();
    }

    /**
     * Centers the view on a specific point in the graph.
     *
     * @param x X coordinate in graph units
     * @param y Y coordinate in graph units
     */
    public void centerOn(double x, double y) {
        camera.setViewCenter(x, y, 0);
    }

    /**
     * Resets the view to its default position.
     */
    public void resetView() {
        camera.resetView();
    }

    /**
     * Checks if panning is currently active.
     *
     * @return true if currently panning, false otherwise
     */
    public boolean isPanning() {
        return isPanning;
    }

    /**
     * Gets the current view center.
     *
     * @return Point3 representing the current view center
     */
    public Point3 getViewCenter() {
        return camera.getViewCenter();
    }

    /**
     * Enables or disables auto-fit view.
     *
     * @param enabled true to enable auto-fit, false to disable
     */
    public void setAutoFitView(boolean enabled) {
        camera.setAutoFitView(enabled);
    }
}