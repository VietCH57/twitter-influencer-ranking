package GUI.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class HomeScene extends BaseScene {
    public HomeScene(VBox buttonContainer) {
        super(buttonContainer);
    }

    @Override
    protected void createContent() {
        VBox homeContent = new VBox(20); // Container for all content
        homeContent.setPadding(new Insets(15));

        // Title Section
        VBox title = new VBox(10);
        title.setPadding(new Insets(15));

        Label welcome = new Label("WELCOME!");
        welcome.setPrefSize(350, 107);
        welcome.setStyle("-fx-text-fill: #33e7ff; -fx-font-size: 70px;");

        Label label1 = new Label("The software is designed and developed");
        label1.setStyle("-fx-font-size: 15px;");

        Label label2 = new Label("by Group 18");
        label2.setStyle("-fx-font-size: 15px;");

        // Content Section
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        Label label3 = new Label("Twitter Influencer Ranking");
        label3.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label label4 = new Label("Overview");
        label4.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Text label5Text = new Text("Twitter Influencer Ranking is a Java-based application designed to analyze and rank Twitter influencers based on various metrics. This project leverages Twitter API to gather data and provides a comprehensive ranking system.");
        TextFlow label5 = new TextFlow(label5Text);

        Label label6 = new Label("Features");
        label6.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Text label7Text = new Text(
                "- Fetches data from Twitter API\n" +
                        "- Analyzes Twitter influencers based on follower count, engagement, and other metrics\n" +
                        "- Provides a ranking system for influencers\n" +
                        "- Generates reports and visualizations"
        );
        TextFlow label7 = new TextFlow(label7Text);

        title.getChildren().addAll(
                welcome,
                label1,
                label2,
                label3
        );

        content.getChildren().addAll(
                label4,
                label5,
                label6,
                label7
        );

        // Add both sections to the main content container
        homeContent.getChildren().addAll(title, content);

        title.setAlignment(Pos.CENTER);

        // Set the main content container to the center of the layout
        layout.setCenter(homeContent);
    }
}
