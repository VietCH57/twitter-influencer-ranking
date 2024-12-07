package TwitRank.graph;

import TwitRank.elements.EdgeType;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import TwitRank.elements.Edge;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class GraphLoader {
    public Graph loadGraphFromExcel(File inputFile) {
        Graph graph = new Graph();

        try (FileInputStream fis = new FileInputStream(inputFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Load users first
            loadUsers(workbook, graph);

            // Load all types of interactions
            String[] sheetNames = {"User", "User Follower", "User Following", "User Repost", "User Comment"};
            for (String sheetName : sheetNames) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet != null && !sheetName.equals("User")) {
                    loadInteractions(sheet, graph, getEdgeTypeFromSheet(sheetName));
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading input file: " + e.getMessage());
        }

        return graph;
    }

    private void loadUsers(Workbook workbook, Graph graph) {
        Sheet userSheet = workbook.getSheet("User");
        if (userSheet == null) {
            System.out.println("Error: 'User' sheet not found in input file");
            return;
        }

        System.out.println("Loading users from Excel...");
        int rowCount = 0;
        for (Row row : userSheet) {
            if (row.getRowNum() == 0) continue; // Skip header row
            try {
                User user = createUserFromRow(row);
                graph.addNode(user);
                rowCount++;
            } catch (Exception e) {
                System.out.println("Warning: Skipping invalid user row " + (row.getRowNum() + 1) +
                        ". Error: " + e.getMessage());
            }
        }
        System.out.println("Successfully loaded " + rowCount + " users");
    }

    private void loadInteractions(Sheet sheet, Graph graph, EdgeType edgeType) {
        System.out.println("Loading " + edgeType + " relationships from sheet: " + sheet.getSheetName());
        int relationCount = 0;
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header row
            try {
                int sourceId = (int) getNumericCellValue(row.getCell(1)); // Source User ID
                int targetId = (int) getNumericCellValue(row.getCell(3)); // Target User ID
                Node sourceNode = findNodeById(graph, sourceId);
                Node targetNode = findNodeById(graph, targetId);

                if (sourceNode != null && targetNode != null) {
                    double weight = calculateEdgeWeight(edgeType);
                    graph.addEdge(sourceNode, targetNode, edgeType, weight);
                    relationCount++;
                }
            } catch (Exception e) {
                System.out.println("Warning: Skipping invalid relationship row " + (row.getRowNum() + 1) +
                        ". Error: " + e.getMessage());
            }
        }
        System.out.println("Successfully loaded " + relationCount + " " + edgeType + " relationships");
    }

    private double calculateEdgeWeight(EdgeType type) {
        // Assign different weights based on interaction type
        return switch (type) {
            case FOLLOW -> 1.0;
            case RETWEET -> 2.0;
            case REPLY -> 1.5;
            case LIKE -> 1.0;
            default -> 1.0;
        };
    }

    private EdgeType getEdgeTypeFromSheet(String sheetName) {
        return switch (sheetName.toUpperCase()) {
            case "USER FOLLOWER" -> EdgeType.FOLLOW;
            case "USER FOLLOWING" -> EdgeType.FOLLOW;
            case "USER REPOST" -> EdgeType.RETWEET;
            case "USER COMMENT" -> EdgeType.REPLY;
            default -> EdgeType.FOLLOW;
        };
    }

    private User createUserFromRow(Row row) {
        try {
            int userId = (int) getNumericCellValue(row.getCell(0));
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

            return new User(userId, name, username, followerCount, followingCount,
                    linkToProfile, linkToTweet, views, reacts, comments, reposts);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating user from row: " + e.getMessage());
        }
    }

    private double getNumericCellValue(Cell cell) {
        if (cell == null) return 0;
        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> cell.getNumericCellValue();
                case STRING -> Double.parseDouble(cell.getStringCellValue());
                default -> 0;
            };
        } catch (Exception e) {
            return 0;
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                default -> "";
            };
        } catch (Exception e) {
            return "";
        }
    }

    private Node findNodeById(Graph graph, int id) {
        for (Node node : graph.getAllNodes()) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }
}