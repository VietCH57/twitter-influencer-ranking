package GUI;

import GUI.scene.*;
import GUI.util.ButtonContainer;
import javafx.application.Application;
import javafx.geometry.Insets;
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

        // Create the button container
        this.buttonContainer = new ButtonContainer(this);

        // Create the main container
        BorderPane mainLayout = new BorderPane();

        // Add the button container to the left side
        mainLayout.setLeft(buttonContainer);

        showHomeScene();
        primaryStage.setScene(new HomeScene(buttonContainer));  // Set initial scene
        primaryStage.show();
    }

    public void showHomeScene() { primaryStage.setScene(new HomeScene(buttonContainer)); }

    public void showCrawlScene() { primaryStage.setScene(new CrawlScene(buttonContainer)); }

    public void showProcessorScene() { primaryStage.setScene(new ProcessorScene(buttonContainer)); }

    public void showRankingScene() { primaryStage.setScene(new GraphAndRankingScene(buttonContainer)); }

    public void showVisualizerScene() { primaryStage.setScene(new VisualizerScene(buttonContainer)); }
}
