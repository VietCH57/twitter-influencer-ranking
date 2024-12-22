package GUI.util;

import GUI.MainApplication;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ButtonContainer extends VBox {
    private MainApplication mainApp;

    public ButtonContainer(MainApplication mainApp) {
        this.mainApp = mainApp;
        createButtons();
        styleButtons();
    }

    private void createButtons() {
        // Create buttons
        Button homeButton = new Button("Homepage");
        Button crawlButton = new Button("Crawl Data");
        Button processorButton = new Button("Data Processor");
        Button rankingButton = new Button("Ranking");
        Button visualizerButton = new Button("Graph Visualizer");

        // Add button actions
        homeButton.setOnAction(e -> mainApp.showHomeScene());
        crawlButton.setOnAction(e -> mainApp.showCrawlScene());
        processorButton.setOnAction(e -> mainApp.showProcessorScene());
        rankingButton.setOnAction(e -> mainApp.showRankingScene());
        visualizerButton.setOnAction(e -> mainApp.showVisualizerScene());

        this.getChildren().addAll(homeButton, crawlButton, processorButton, rankingButton, visualizerButton);
    }

    private void styleButtons() {
        // Style all buttons
        String buttonStyle = "-fx-font-size: 14px; -fx-min-width: 150px; -fx-min-height: 40px;";
        for (var node : this.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setStyle(buttonStyle);
            }
        }

        this.setSpacing(10);
        this.setPadding(new Insets(15));
        this.setStyle("-fx-background-color: #f0f0f0;");
    }
}
