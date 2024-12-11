package GUI.scene;

import Visualizer.VisualizationApp;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class VisualizerScene extends BaseScene {
    private Label label_visualizer;

    public VisualizerScene(VBox buttonContainer) {
        super(buttonContainer);
    }

    @Override
    protected void createContent() {
        VBox visualizerContent = new VBox(10);
        visualizerContent.setPadding(new Insets(15));

        Label titleLabel = new Label("Graph Visualizer");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        label_visualizer = new Label(); label_visualizer.setManaged(false);

        Button visualizeButton = new Button("Graph Visualization");
        visualizeButton.setOnAction(e -> {
            try {
                VisualizationApp.main(new String[]{});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        visualizerContent.getChildren().addAll(
                titleLabel,
                visualizeButton
        );
        layout.setCenter(visualizerContent);
    }
}
