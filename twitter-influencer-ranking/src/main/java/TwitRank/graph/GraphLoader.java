package TwitRank.graph;

import TwitRank.elements.Edge;
import TwitRank.elements.EdgeType;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class GraphLoader {

    public Graph loadGraphFromExcel(File inputFile) {
        Graph graph = new Graph();
        System.out.println("Looking for input file at: " + inputFile.getAbsolutePath());

        try (FileInputStream fis = new FileInputStream(inputFile);
             Workbook workbook = WorkbookFactory.create(fis)) {

            loadUsers(workbook, graph);
            loadInteractions(workbook.getSheet("User Follower"), graph, EdgeType.FOLLOW);
            loadInteractions(workbook.getSheet("User Following"), graph, EdgeType.FOLLOW);
            loadInteractions(workbook.getSheet("User Repost"), graph, EdgeType.RETWEET);
            loadInteractions(workbook.getSheet("User Comment"), graph, EdgeType.REPLY);

            System.out.println("Successfully loaded graph data. Computing PageRank scores...");
            return graph;

        } catch (Exception e) {
            System.out.println("Error loading graph data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void loadUsers(Workbook workbook, Graph graph) {
        Sheet userSheet = workbook.getSheet("User");
        if (userSheet == null) {
            System.out.println("Error: 'User' sheet not found in input file");
            return;
        }

        System.out.println("Loading users from Excel...");
        Map<Integer, String> userDebugInfo = new HashMap<>();
        int rowCount = 0;

        for (Row row : userSheet) {
            if (row.getRowNum() == 0) continue; // Skip header row

            try {
                User user = createUserFromRow(row);
                graph.addNode(user);
                rowCount++;
            } catch (Exception e) {
                System.out.println("Warning: Error processing row " + (row.getRowNum() + 1) +
                        ". Error: " + e.getMessage());
            }
        }

        System.out.println("Successfully loaded " + rowCount + " users");
        System.out.println("Total unique user IDs found: " + rowCount);
    }

    private void loadInteractions(Sheet sheet, Graph graph, EdgeType edgeType) {
        if (sheet == null) return;

        System.out.println("Loading " + edgeType + " relationships from sheet: " + sheet.getSheetName());
        Set<Integer> missingUsers = new HashSet<>();
        int relationCount = 0;
        int skippedCount = 0;

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row

            try {
                Cell sourceCell = row.getCell(1);
                Cell targetCell = row.getCell(3);

                if (sourceCell == null || targetCell == null) {
                    skippedCount++;
                    continue;
                }

                int sourceId = (int) getNumericCellValue(sourceCell);
                int targetId = (int) getNumericCellValue(targetCell);

                Node sourceNode = findNodeById(graph, sourceId);
                Node targetNode = findNodeById(graph, targetId);

                if (sourceNode == null) {
                    missingUsers.add(sourceId);
                    skippedCount++;
                    continue;
                }
                if (targetNode == null) {
                    missingUsers.add(targetId);
                    skippedCount++;
                    continue;
                }

                double weight = calculateEdgeWeight(edgeType);
                graph.addEdge(sourceNode, targetNode, edgeType, weight);
                relationCount++;

            } catch (Exception e) {
                skippedCount++;
            }
        }

        System.out.println("Successfully loaded " + relationCount + " " + edgeType + " relationships");
        System.out.println("Skipped " + skippedCount + " invalid rows");
        if (!missingUsers.isEmpty()) {
            System.out.println("Missing users in relationship sheet: " + missingUsers);
        }
    }

    private User createUserFromRow(Row row) {
        try {
            int id = (int) getNumericCellValue(row.getCell(0));
            String name = getStringCellValue(row.getCell(1));
            String username = getStringCellValue(row.getCell(2));
            int followerCount = (int) getNumericCellValue(row.getCell(3));
            int followingCount = (int) getNumericCellValue(row.getCell(4));
            String linkToProfile = getStringCellValue(row.getCell(5));
            String linkToTweet = getStringCellValue(row.getCell(6));
            int views = (int) getNumericCellValue(row.getCell(7));
            int reacts = (int) getNumericCellValue(row.getCell(8));
            int comments = (int) getNumericCellValue(row.getCell(9));
            int reposts = (int) getNumericCellValue(row.getCell(10));

            return new User(id, name, username, followerCount, followingCount,
                    linkToProfile, linkToTweet, views, reacts, comments, reposts);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating user from row " + (row.getRowNum() + 1) +
                    ": " + e.getMessage());
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long)cell.getNumericCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf((long)cell.getNumericCellValue());
                    } catch (Exception e2) {
                        return "";
                    }
                }
            default:
                return "";
        }
    }

    private double getNumericCellValue(Cell cell) {
        if (cell == null) throw new IllegalArgumentException("Cell is null");
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid numeric value: " + cell.getStringCellValue());
                }
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid formula result");
                }
            default:
                throw new IllegalArgumentException("Unsupported cell type: " + cell.getCellType());
        }
    }

    private Node findNodeById(Graph graph, int id) {
        return graph.getAllNodes().stream()
                .filter(node -> node instanceof User && ((User) node).getId() == id)
                .findFirst()
                .orElse(null);
    }

    private double calculateEdgeWeight(EdgeType edgeType) {
        switch (edgeType) {
            case FOLLOW:
                return 1.0;
            case RETWEET:
                return 2.0;
            case REPLY:
                return 3.0;
            default:
                return 1.0;
        }
    }
}