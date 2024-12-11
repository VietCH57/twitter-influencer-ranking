package GUI;

import GUI.scene.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApplication extends Application {
    private Stage primaryStage;
    private VBox buttonContainer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Twitter Influencer Ranking");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Create button container
        this.buttonContainer = createButtonContainer();

        // Create the main container
        BorderPane mainLayout = new BorderPane();

        // Add the button container to the left side
        VBox buttonContainer = createButtonContainer();
        mainLayout.setLeft(buttonContainer);

        showHomeScene();
        primaryStage.show();
    }

    private VBox createButtonContainer() {
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
        homeButton.setOnAction(e -> showHomeScene());
        crawlButton.setOnAction(e -> showCrawlScene());
        processorButton.setOnAction(e -> showProcessorScene());
        rankingButton.setOnAction(e -> showRankingScene());
        visualizerButton.setOnAction(e -> showVisualizerScene());

        return buttonContainer;
    }

    private void showHomeScene() {
        primaryStage.setScene(new HomeScene(buttonContainer));
        primaryStage.setTitle("Homepage");
    }

    private void showCrawlScene() {
        primaryStage.setScene(new CrawlScene(buttonContainer));
        primaryStage.setTitle("Crawl Data");
    }

    private void showProcessorScene() {
        primaryStage.setScene(new ProcessorScene(buttonContainer));
        primaryStage.setTitle("Data Processor");
    }

    private void showRankingScene() {
        primaryStage.setScene(new GraphAndRankingScene(buttonContainer));
        primaryStage.setTitle("Ranking");
    }

    private void showVisualizerScene() {
        primaryStage.setScene(new VisualizerScene(buttonContainer));
        primaryStage.setTitle("Graph Visualizer");
    }
}
