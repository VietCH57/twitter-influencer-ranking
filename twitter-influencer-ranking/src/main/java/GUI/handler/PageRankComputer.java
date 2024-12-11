package GUI.handler;

import GUI.util.RankingEntry;
import TwitRank.graph.Graph;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import TwitRank.rank.PageRank;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PageRankComputer {
    private final Label label_pagerank;
    private final TableView<RankingEntry> rankingTable;

    public PageRankComputer(Label statusLabel_pagerank, TableView<RankingEntry> rankingTable) {
        this.label_pagerank = statusLabel_pagerank;
        this.rankingTable = rankingTable;
    }

    public void computePageRank(Graph graph) {

        // Input validation
        if (graph == null || graph.getAllNodes().isEmpty()) {
            label_pagerank.setText("Error: No graph data loaded");
            return;
        }

        label_pagerank.setManaged(true);
        label_pagerank.setText("Computing PageRank...");

        // Clear existing table data
        Platform.runLater(() -> rankingTable.getItems().clear());

        new Thread(() -> {
            try {
                // Compute PageRank
                PageRank pageRank = new PageRank(graph, 0.85, 100, 1e-6);
                Map<Node, Double> pageRankScores = pageRank.computePageRank();

                // Sort scores
                List<Map.Entry<Node, Double>> sortedScores = new ArrayList<>(pageRankScores.entrySet());
                sortedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

                // Process results
                ObservableList<RankingEntry> rankings = FXCollections.observableArrayList();
                AtomicInteger rank = new AtomicInteger(1);

                for (Map.Entry<Node, Double> entry : sortedScores) {
                    if (!(entry.getKey() instanceof User)) {
                        continue;
                    }

                    User user = (User) entry.getKey();
                    if (TwitRank.graph.GraphLoader.meetsKoLCriteria(
                            user.getFollowerCount(),
                            user.getReacts(),
                            user.getComments(),
                            user.getReposts()
                    )) {
                        rankings.add(new RankingEntry(
                                rank.getAndIncrement(),
                                user.getId(),
                                user.getUsername(),
                                user.getFollowerCount(),
                                entry.getValue()
                        ));
                    }
                }

                // Update UI
                Platform.runLater(() -> {
                    try {
                        rankingTable.setItems(rankings);
                        label_pagerank.setText("PageRank computed for " + rankings.size() + " users");
                    } catch (Exception e) {
                        label_pagerank.setText("Error updating results: " + e.getMessage());
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() ->
                        label_pagerank.setText("Error computing PageRank: " + e.getMessage())
                );
            }
        }).start();
    }
}
