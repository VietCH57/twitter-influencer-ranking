package GUI.scene;

import GUI.handler.DataCleanerHandler;
import GUI.handler.DataTransformerHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ProcessorScene extends BaseScene {
    private Label label_transform, label_clean;

    public ProcessorScene(VBox buttonContainer) {
        super(buttonContainer);
    }

    @Override
    protected void createContent() {
        VBox processorContent = new VBox(10);
        processorContent.setPadding(new Insets(15));

        Label titleLabel = new Label("Data Processor");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        label_transform = new Label(); label_transform.setManaged(false);
        label_clean = new Label(); label_clean.setManaged(false);

        // Transform crawl data file button
        Button transformDataButton = new Button("Transform Data File");
        DataTransformerHandler dataTransformerHandler = new DataTransformerHandler(label_transform, label_clean);
        transformDataButton.setOnAction(e -> {
            try {
                dataTransformerHandler.transformData();
            } catch (Exception ex) {
                label_transform.setText("Error: " + ex.getMessage());
                transformDataButton.setDisable(false);
            }
        });

        // Clean transformed data file button
        Button cleanDataButton = new Button("Clean Transformed File");
        DataCleanerHandler dataCleanerHandler = new DataCleanerHandler(label_clean);
        cleanDataButton.setOnAction(e -> dataCleanerHandler.cleanData());

        processorContent.getChildren().addAll(
                titleLabel,
                transformDataButton,
                label_transform,
                cleanDataButton,
                label_clean
        );

        layout.setCenter(processorContent);
    }
}
