package GUI;

import TwitRank.graph.Graph;
import TwitRank.graph.GraphLoader;
import TwitRank.elements.KoL;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import TwitRank.rank.PageRank;
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

        Button selectFileButton = new Button("Select Input File");
        selectFileButton.setOnAction(e -> selectInputFile(primaryStage));

        Button computeRankButton = new Button("Compute Rankings");
        computeRankButton.setOnAction(e -> computePageRank());

        statusLabel = new Label("Ready to load data");

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
                GraphLoader graphLoader = new GraphLoader();
                graph = graphLoader.loadGraphFromExcel(selectedFile);
                Platform.runLater(() -> {
                    if (graph != null && !graph.getAllNodes().isEmpty()) {
                        statusLabel.setText("Graph loaded successfully with " + graph.getAllNodes().size() + " nodes");
                    } else {
                        statusLabel.setText("Error loading graph");
                    }
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

            List<Map.Entry<Node, Double>> sortedScores = new ArrayList<>(pageRankScores.entrySet());
            sortedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

            Platform.runLater(() -> {
                ObservableList<RankingEntry> rankings = FXCollections.observableArrayList();
                int rank = 1;
                int kolCount = 0;

                for (Map.Entry<Node, Double> entry : sortedScores) {
                    if (entry.getKey() instanceof User user) {
                        boolean isKoL = user.getFollowerCount() >= KoL.getMinFollowerCount() &&
                                user.getReacts() >= KoL.getMinReacts() &&
                                user.getComments() >= KoL.getMinComments() &&
                                user.getReposts() >= KoL.getMinReposts();

                        if (isKoL) {
                            rankings.add(new RankingEntry(
                                    rank++,
                                    user.getId(),
                                    user.getName(),
                                    user.getUsername(),
                                    user.getFollowerCount(),
                                    entry.getValue()
                            ));
                            kolCount++;
                        }
                    }
                }

                rankingTable.setItems(rankings);
                statusLabel.setText("Found " + kolCount + " KoLs");
            });
        }).start();
    }

    private TableView<RankingEntry> createRankingTable() {
        TableView<RankingEntry> table = new TableView<>();

        TableColumn<RankingEntry, Number> rankCol = new TableColumn<>("Rank");
        rankCol.setCellValueFactory(cellData -> cellData.getValue().rankProperty());

        TableColumn<RankingEntry, Number> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(cellData -> cellData.getValue().userIdProperty());

        TableColumn<RankingEntry, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<RankingEntry, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        TableColumn<RankingEntry, Number> followersCol = new TableColumn<>("Followers Count");
        followersCol.setCellValueFactory(cellData -> cellData.getValue().followersProperty());

        TableColumn<RankingEntry, Number> pageRankCol = new TableColumn<>("PageRank Score");
        pageRankCol.setCellValueFactory(cellData -> cellData.getValue().pageRankProperty());

        table.getColumns().addAll(rankCol, userIdCol, nameCol, usernameCol, followersCol, pageRankCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    public static class RankingEntry {
        private final SimpleIntegerProperty rank;
        private final SimpleIntegerProperty userId;
        private final SimpleStringProperty name;
        private final SimpleStringProperty username;
        private final SimpleIntegerProperty followers;
        private final SimpleDoubleProperty pageRank;

        public RankingEntry(int rank, int userId, String name, String username, int followers, double pageRankScore) {
            this.rank = new SimpleIntegerProperty(rank);
            this.userId = new SimpleIntegerProperty(userId);
            this.name = new SimpleStringProperty(name);
            this.username = new SimpleStringProperty(username);
            this.followers = new SimpleIntegerProperty(followers);
            this.pageRank = new SimpleDoubleProperty(pageRankScore);
        }

        public IntegerProperty rankProperty() { return rank; }
        public IntegerProperty userIdProperty() { return userId; }
        public StringProperty nameProperty() { return name; }
        public StringProperty usernameProperty() { return username; }
        public IntegerProperty followersProperty() { return followers; }
        public DoubleProperty pageRankProperty() { return pageRank; }
    }
}