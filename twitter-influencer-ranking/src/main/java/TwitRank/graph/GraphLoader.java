package TwitRank.graph;

import TwitRank.model.EdgeType;
import TwitRank.model.Node;
import TwitRank.model.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class GraphLoader {

    public Graph loadGraphFromExcel(File inputFile) {
        Graph graph = new Graph();

        try (FileInputStream fis = new FileInputStream(inputFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet userSheet = workbook.getSheet("User");
            if (userSheet == null) {
                System.out.println("Error: 'User' sheet not found in input file");
                return graph;
            }

            System.out.println("Loading users from Excel...");
            for (Row row : userSheet) {
                if (row.getRowNum() == 0) continue;
                try {
                    int userId = (int) getNumericCellValue(row.getCell(0));
                    String name = row.getCell(1).getStringCellValue();
                    String username = row.getCell(2).getStringCellValue();
                    int followerCount = (int) getNumericCellValue(row.getCell(3));
                    int followingCount = (int) getNumericCellValue(row.getCell(4));
                    String linkToProfile = row.getCell(5).getStringCellValue();
                    String linkToTweet = row.getCell(6).getStringCellValue();
                    int views = (int) getNumericCellValue(row.getCell(7));
                    int reacts = (int) getNumericCellValue(row.getCell(8));
                    int comments = (int) getNumericCellValue(row.getCell(9));
                    int reposts = (int) getNumericCellValue(row.getCell(10));

                    User user = new User(userId, name, username, followerCount, followingCount,
                            linkToProfile, views, reacts, comments, reposts);
                    graph.addNode(user);
                } catch (Exception e) {
                    System.out.println("Warning: Skipping invalid user row " + (row.getRowNum() + 1));
                }
            }

            for (Sheet sheet : workbook) {
                if (!sheet.getSheetName().equals("User")) {
                    System.out.println("Loading relationships from sheet: " + sheet.getSheetName());
                    for (Row row : sheet) {
                        if (row.getRowNum() == 0) continue;
                        try {
                            int sourceId = (int) getNumericCellValue(row.getCell(1));
                            int targetId = (int) getNumericCellValue(row.getCell(3));
                            Node sourceNode = findNodeById(graph, sourceId);
                            Node targetNode = findNodeById(graph, targetId);
                            if (sourceNode != null && targetNode != null) {
                                graph.addEdge(sourceNode, targetNode, EdgeType.FOLLOW, 1.0);
                            }
                        } catch (Exception e) {
                            System.out.println("Warning: Skipping invalid relationship row " + (row.getRowNum() + 1));
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading input file: " + e.getMessage());
        }

        return graph;
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

    private Node findNodeById(Graph graph, int id) {
        for (Node node : graph.getAllNodes()) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }
}