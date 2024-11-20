import java.sql.*;

public class Main {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/your_database";
        String dbUser = "your_username";
        String dbPassword = "your_password";

        Graph graph = new Graph();

        try (Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {
            // Load users
            String userQuery = "SELECT * FROM users";
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(userQuery)) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String username = rs.getString("username");

                    User user = new User(id, name, username);
                    graph.addUser(user);
                }
            }

            // Load user relationships
            String relationshipQuery = "SELECT * FROM user_relationships";
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(relationshipQuery)) {
                while (rs.next()) {
                    int sourceId = rs.getInt("user_id");
                    int targetId = rs.getInt("related_user_id");
                    graph.addEdge(sourceId, targetId);
                }
            }

            // Calculate PageRank
            PageRankCalculator.calculate(graph);

            // Print results
            graph.getNodes().values().forEach(node -> {
                System.out.printf("Node %d (PageRank: %.6f)%n", node.getId(), node.getPageRank());
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
