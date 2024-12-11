package GUI.util;

import javafx.beans.property.*;

public class RankingEntry {
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
