package GUI.scene;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WaitingScene extends BaseScene {
    private Label statusLabel;
    private Button stopButton;
    private Runnable stopAction;

    public WaitingScene(VBox buttonContainer, Runnable stopAction) {
        super(buttonContainer);
        this.stopAction = stopAction;
        createContent();
    }

    @Override
    protected void createContent() {
        VBox waitingContent = new VBox(10);
        waitingContent.setPadding(new Insets(15));

        Label titleLabel = new Label("Crawling in Progress...");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        statusLabel = new Label("Please wait while crawling is in progress.");
        statusLabel.setStyle("-fx-font-size: 16px;");

        stopButton = new Button("Stop Crawling");
        stopButton.setOnAction(e -> stopCrawling());

        waitingContent.getChildren().addAll(titleLabel, statusLabel, stopButton);
        layout.setCenter(waitingContent);
    }

    private void stopCrawling() {
        if (stopAction != null) {
            stopAction.run();
        }
        statusLabel.setText("Crawling stopped by user.");
        stopButton.setDisable(true);
    }
}
