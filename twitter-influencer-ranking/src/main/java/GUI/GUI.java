package GUI;

import TwitterInfluencerRanking.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUI extends Application {
    private Graph graph;
    private TableView<RankingEntry> rankingTable;
    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Twitter Influencer Ranking");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        // Input file selection button
        Button selectFileButton = new Button("Select Input File");
        selectFileButton.setOnAction(e -> selectInputFile(primaryStage));

        // Compute PageRank button
        Button computeRankButton = new Button("Compute Rankings");
        computeRankButton.setOnAction(e -> computePageRank());

        // Status label
        statusLabel = new Label("Ready to load data");

        // Ranking table
        rankingTable = createRankingTable();

        root.getChildren().addAll(
                selectFileButton,
                computeRankButton,
                statusLabel,
                new Label("Rankings:"),
                rankingTable
        );

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void selectInputFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input Excel File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            new Thread(() -> {
                graph = Main.loadGraphFromExcel(selectedFile);
                Platform.runLater(() -> {
                    statusLabel.setText("Graph loaded: " + graph.getAllNodes().size() + " nodes");
                });
            }).start();
        }
    }

    private void computePageRank() {
        if (graph == null || graph.getAllNodes().isEmpty()) {
            statusLabel.setText("Error: No graph data loaded");
            return;
        }

        new Thread(() -> {
            PageRank pageRank = new PageRank(graph, 0.85, 100);
            Map<Node, Double> pageRankScores = pageRank.computePageRank();

            List<Map.Entry<Node, Double>> sortedScores = Main.sortAndFilterPageRankScores(pageRankScores);

            Platform.runLater(() -> {
                ObservableList<RankingEntry> rankings = FXCollections.observableArrayList();
                int rank = 1;
                for (Map.Entry<Node, Double> entry : sortedScores) {
                    User user = (User) entry.getKey();
                    rankings.add(new RankingEntry(
                            rank++,
                            user.getId(),
                            user.getUsername(),
                            user.getFollowerCount(),
                            entry.getValue()
                    ));
                }

                rankingTable.setItems(rankings);
                statusLabel.setText("PageRank computed for " + rankings.size() + " users");
            });
        }).start();
    }

    private TableView<RankingEntry> createRankingTable() {
        TableView<RankingEntry> table = new TableView<>();

        TableColumn<RankingEntry, Number> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(cellData -> cellData.getValue().rankProperty());

        TableColumn<RankingEntry, Number> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cellData -> cellData.getValue().userIdProperty());

        TableColumn<RankingEntry, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        TableColumn<RankingEntry, Number> followersCol = new TableColumn<>("Followers");
        followersCol.setCellValueFactory(cellData -> cellData.getValue().followersProperty());

        TableColumn<RankingEntry, Number> pageRankCol = new TableColumn<>("PageRank");
        pageRankCol.setCellValueFactory(cellData -> cellData.getValue().pageRankProperty());

        table.getColumns().addAll(rankCol, userIdCol, usernameCol, followersCol, pageRankCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    // Additional helper class for JavaFX binding
    public static class RankingEntry {
        private final SimpleIntegerProperty rank;
        private final SimpleIntegerProperty userId;
        private final SimpleStringProperty username;
        private final SimpleIntegerProperty followers;
        private final SimpleDoubleProperty pageRank;

        public RankingEntry(int rank, int userId, String username, int followers, double pageRankScore) {
            this.rank = new SimpleIntegerProperty(rank);
            this.userId = new SimpleIntegerProperty(userId);
            this.username = new SimpleStringProperty(username);
            this.followers = new SimpleIntegerProperty(followers);
            this.pageRank = new SimpleDoubleProperty(pageRankScore);
        }

        // Getters for properties
        public IntegerProperty rankProperty() { return rank; }
        public IntegerProperty userIdProperty() { return userId; }
        public StringProperty usernameProperty() { return username; }
        public IntegerProperty followersProperty() { return followers; }
        public DoubleProperty pageRankProperty() { return pageRank; }
    }
}
