package GUI.scene;

import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class WaitingScene {
    protected VBox waitingContent;
    protected HBox hbox1, hbox2, hboxButton;
    protected ArrayList<Rectangle> squares;
    protected static Label label = new Label();
    protected Button stopButton;

    public WaitingScene(Runnable stopCrawlingAction) {
        waitingContent = new VBox(20);
        waitingContent.setPadding(new Insets(15));
        waitingContent.setAlignment(Pos.CENTER);

        hboxButton = new HBox();
        hboxButton.setAlignment(Pos.CENTER);
        stopButton = new Button("Stop Crawling");
        stopButton.setOnAction(e -> stopCrawlingAction.run());
        hboxButton.getChildren().add(stopButton);

        hbox2 = new HBox();
        hbox2.setPrefSize(200, 100);
        hbox2.setAlignment(Pos.CENTER);
        label.setText("Crawling Data...");
        hbox2.getChildren().add(label);
        label.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 15; -fx-alignment: center;");
        label.styleProperty().bind(
                Bindings.createStringBinding(
                        () -> String.format("-fx-font-size: %.1fpx;", hbox2.getWidth() / 25),
                        hbox2.widthProperty()))
        ;

        hbox1 = new HBox();
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setSpacing(20);

        // Progress Indicator
        squares = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            Circle circle = new Circle(10);
            hbox1.getChildren().add(circle);

            TranslateTransition transition = new TranslateTransition();
            transition.setDuration(Duration.millis(1000));
            transition.setNode(circle);
            transition.setByY(10);
            transition.setCycleCount(TranslateTransition.INDEFINITE);
            transition.setAutoReverse(true);
            transition.setDelay(Duration.millis(i * 200));

            transition.play();
        }

        waitingContent.getChildren().addAll(hbox1, hbox2, hboxButton);
        waitingContent.setAlignment(Pos.CENTER);
        waitingContent.setSpacing(0);
        waitingContent.setStyle("-fx-padding: 10;");
    }

    public VBox getWaitingContent() {
        return waitingContent;
    }
}
