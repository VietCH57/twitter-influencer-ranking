package GUI.util;

import GUI.ShowingScene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ButtonContainer {
    private VBox buttonContainer;

    public ButtonContainer(ShowingScene showingScene) {
        buttonContainer = createButtonContainer(showingScene);
    }

    private VBox createButtonContainer(ShowingScene showingScene) {
        // Create buttons
        Button homeButton = new Button("Homepage");
        Button crawlButton = new Button("Crawl Data");
        Button processorButton = new Button("Data Processor");
        Button rankingButton = new Button("Ranking");
        Button visualizerButton = new Button("Graph Visualizer");

        // Style all buttons
        String buttonStyle = "-fx-font-size: 14px; -fx-min-width: 150px; -fx-min-height: 40px;";
        homeButton.setStyle(buttonStyle);
        crawlButton.setStyle(buttonStyle);
        processorButton.setStyle(buttonStyle);
        rankingButton.setStyle(buttonStyle);
        visualizerButton.setStyle(buttonStyle);

        // Create container
        VBox buttonContainer = new VBox(10);
        buttonContainer.setPadding(new Insets(15));
        buttonContainer.setStyle("-fx-background-color: #f0f0f0;");
        buttonContainer.getChildren().addAll(homeButton, crawlButton, processorButton, rankingButton, visualizerButton);

        // Add button actions
        homeButton.setOnAction(e -> showingScene.showHomeScene());
        crawlButton.setOnAction(e -> showingScene.showCrawlScene());
        processorButton.setOnAction(e -> showingScene.showProcessorScene());
        rankingButton.setOnAction(e -> showingScene.showRankingScene());
        visualizerButton.setOnAction(e -> showingScene.showVisualizerScene());

        return buttonContainer;
    }

    public VBox getButtonContainerBox() {
        return buttonContainer;
    }
}
