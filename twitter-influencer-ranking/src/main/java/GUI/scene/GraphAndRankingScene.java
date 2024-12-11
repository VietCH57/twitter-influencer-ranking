package GUI.scene;

import GUI.handler.PageRankComputer;
import GUI.util.RankingEntry;
import TwitRank.graph.Graph;
import TwitRank.graph.GraphLoader;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.File;

public class GraphAndRankingScene extends BaseScene {
    private Graph graph;
    private TableView<RankingEntry> rankingTable;
    private Label label_graph, label_pagerank;

    public GraphAndRankingScene(VBox buttonContainer) {
        super(buttonContainer);
    }

    @Override
    protected void createContent() {
        VBox rankingContent = new VBox(10);
        rankingContent.setPadding(new Insets(15));

        Label titleLabel = new Label("Ranking");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Ranking table
        rankingTable = createRankingTable();

        // Status label
        label_graph = new Label(); label_graph.setManaged(false);
        label_pagerank = new Label(); label_pagerank.setManaged(false);

        // Build graph button
        Button buildGraphButton = new Button("Build graph");
        buildGraphButton.setOnAction(e -> {
            try {
                label_graph.setManaged(true);
                label_graph.setText("Building graph...");

                // Verify input file exists
                File inputFile = new File("cleaned_data.xlsx");
                if (!inputFile.exists()) {
                    label_graph.setText("Error: cleaned_data.xlsx not found");
                    return;
                }

                // Disable button during graph building
                buildGraphButton.setDisable(true);

                // Start graph building process
                new Thread(() -> {
                    try {
                        GraphLoader graphLoader = new GraphLoader();
                        Graph builtGraph = graphLoader.loadGraphFromExcel(inputFile);

                        Platform.runLater(() -> {
                            if (builtGraph != null && !builtGraph.getAllNodes().isEmpty()) {
                                graph = builtGraph;
                                label_graph.setText("Graph loaded: " + graph.getAllNodes().size() + " nodes");
                                label_pagerank.setManaged(true);
                                label_pagerank.setText("Ready to compute pagerank");
                            } else {
                                label_graph.setText("Error: Failed to build graph - No nodes loaded");
                            }
                            buildGraphButton.setDisable(false);
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            label_graph.setText("Error building graph: " + ex.getMessage());
                            buildGraphButton.setDisable(false);
                        });
                    }
                }).start();

            } catch (Exception ex) {
                label_graph.setText("Error: " + ex.getMessage());
                buildGraphButton.setDisable(false);
            }
        });

        // Compute PageRank button
        Button computeRankButton = new Button("Compute Rankings");
        PageRankComputer pageRankComputer = new PageRankComputer(label_pagerank, rankingTable);
        computeRankButton.setOnAction(e -> {
            try {
                // Reset and show status label
                label_pagerank.setManaged(true);

                // Check if graph exists and has data
                if (graph == null) {
                    label_pagerank.setText("Error: Graph not built. Please build the graph first.");
                    return;
                }

                // Disable button while computing
                computeRankButton.setDisable(true);

                // Start PageRank computation
                pageRankComputer.computePageRank(graph);

                // Re-enable button after computation starts
                computeRankButton.setDisable(false);

            } catch (Exception ex) {
                label_pagerank.setText("Error: " + ex.getMessage());
                computeRankButton.setDisable(false);
            }
        });

        rankingContent.getChildren().addAll(
                titleLabel,
                buildGraphButton,
                label_graph,
                computeRankButton,
                label_pagerank,
                rankingTable
        );

        layout.setCenter(rankingContent);
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
}
