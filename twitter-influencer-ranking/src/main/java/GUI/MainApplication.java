package GUI;

import GUI.scene.*;
import GUI.util.ButtonContainer;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApplication extends Application implements ShowingScene {
    private Stage primaryStage;
    private ButtonContainer buttonContainer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Twitter Influencer Ranking");

        // Create the main container
        BorderPane mainLayout = new BorderPane();

        // Initialize buttonContainer with this (implementing ShowingScene)
        buttonContainer = new ButtonContainer(this);
        VBox buttonContainerBox = buttonContainer.getButtonContainerBox();
        mainLayout.setLeft(buttonContainerBox);

        showHomeScene();
        primaryStage.show();
    }

    @Override
    public void showHomeScene() { primaryStage.setScene(new HomeScene(buttonContainer.getButtonContainerBox())); }

    @Override
    public void showCrawlScene() { primaryStage.setScene(new CrawlScene(buttonContainer.getButtonContainerBox())); }

    @Override
    public void showProcessorScene() { primaryStage.setScene(new ProcessorScene(buttonContainer.getButtonContainerBox())); }

    @Override
    public void showRankingScene() { primaryStage.setScene(new GraphAndRankingScene(buttonContainer.getButtonContainerBox())); }

    @Override
    public void showVisualizerScene() { primaryStage.setScene(new VisualizerScene(buttonContainer.getButtonContainerBox())); }
}
