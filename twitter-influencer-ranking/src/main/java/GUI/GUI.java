package GUI;

import TwitRank.graph.Graph;
import TwitRank.graph.GraphLoader;
import TwitRank.elements.KoL;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import TwitRank.rank.PageRank;
import ExcelDataTransformer.DataTransformer;
import ExcelDataTransformer.processors.DataCleaner;

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
    private Label statusLabel_transform, statusLabel_clean, statusLabel_graph, statusLabel_pagerank;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Twitter Influencer Ranking");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));


        // Transform crawl data file button
        Button transformDataButton = new Button("Transform Data File");
        transformDataButton.setOnAction(e -> transformData(primaryStage));

        // Clean transformed data file button
        Button cleanDataButton = new Button("Clean Transformed File");
        cleanDataButton.setOnAction(e -> cleanData());

        // Build graph button
        Button buildGraphButton = new Button("Build graph");
        buildGraphButton.setOnAction(e -> buildGraph());

        // Compute PageRank button
        Button computeRankButton = new Button("Compute Rankings");
        computeRankButton.setOnAction(e -> computePageRank());

        // Status label
        statusLabel_transform = new Label(); statusLabel_transform.setManaged(false);
        statusLabel_clean = new Label(); statusLabel_clean.setManaged(false);
        statusLabel_graph = new Label(); statusLabel_graph.setManaged(false);
        statusLabel_pagerank = new Label(); statusLabel_pagerank.setManaged(false);

        // Ranking table
        rankingTable = createRankingTable();

        root.getChildren().addAll(
                transformDataButton,
                statusLabel_transform,
                cleanDataButton,
                statusLabel_clean,
                buildGraphButton,
                statusLabel_graph,
                computeRankButton,
                statusLabel_pagerank,
                rankingTable
        );

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private File selectInputFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input Excel File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);

        return selectedFile;
    }

    private void transformData(Stage stage) {
        File selectedFile = selectInputFile(stage);
        String inputFile = selectedFile.toString();

        statusLabel_transform.setManaged(true);
        statusLabel_transform.setText("Progressing...");

        if (selectedFile != null) {
            new Thread(() -> {
                DataTransformer dataTransformer = new DataTransformer();
                dataTransformer.transformData(inputFile);

                Platform.runLater(() -> {
                    statusLabel_transform.setText("Data transformed");
                    statusLabel_clean.setManaged(true);
                    statusLabel_clean.setText("Ready to clean data");
                });
            }).start();
        }
    }

    private void cleanData() {
        statusLabel_clean.setText("Progressing...");

        new Thread(() -> {
            DataCleaner dataCleaner = new DataCleaner();
            dataCleaner.cleanData();

            Platform.runLater(() -> {
                statusLabel_transform.setText("Data cleaned");
                statusLabel_graph.setManaged(true);
                statusLabel_clean.setText("Ready to build graph");
            });
        }).start();
    }

    private void buildGraph() {
        File inputFile = new File("cleaned_data.xlsx");

        statusLabel_graph.setText("Progressing...");

        new Thread(() -> {
            GraphLoader graphLoader = new GraphLoader();
            graph = graphLoader.loadGraphFromExcel(inputFile);
            Platform.runLater(() -> {
                statusLabel_graph.setText("Graph loaded: " + graph.getAllNodes().size() + " nodes");
                statusLabel_pagerank.setManaged(true);
                statusLabel_pagerank.setText("Ready to compute pagerank");
            });
        }).start();
    }

    private void computePageRank() {
        if (graph == null || graph.getAllNodes().isEmpty()) {
            statusLabel_pagerank.setText("Error: No graph data loaded");
            return;
        }

        statusLabel_pagerank.setText("Progressing...");

        new Thread(() -> {
            PageRank pageRank = new PageRank(graph, 0.85, 100);
            Map<Node, Double> pageRankScores = pageRank.computePageRank();

            List<Map.Entry<Node, Double>> sortedScores = new ArrayList<>(pageRankScores.entrySet());
            sortedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

            Platform.runLater(() -> {
                ObservableList<RankingEntry> rankings = FXCollections.observableArrayList();
                int rank = 1;
                for (Map.Entry<Node, Double> entry : sortedScores) {
                    User user = (User) entry.getKey();
                    if (user.getFollowerCount() >= KoL.getMinFollowerCount()) {
                        rankings.add(new RankingEntry(
                                rank++,
                                user.getId(),
                                user.getUsername(),
                                user.getFollowerCount(),
                                entry.getValue()
                        ));
                    }
                }

                rankingTable.setItems(rankings);
                statusLabel_pagerank.setText("PageRank computed for " + rankings.size() + " users");
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