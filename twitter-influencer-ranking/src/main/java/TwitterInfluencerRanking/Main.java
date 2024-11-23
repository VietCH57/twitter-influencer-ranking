package TwitterInfluencerRanking;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class Main {

    private static final String INPUT_FILE = "transformed_data.xlsx";
    private static final String OUTPUT_FILE = "ranking_output.xlsx";
    private static final int MIN_FOLLOWER_COUNT = KoL.MIN_FOLLOWER_COUNT;

    public static void main(String[] args) {
        // Print working directory and input file path for verification
        File inputFile = new File(INPUT_FILE);
        if (!inputFile.exists()) {
            System.out.println("Error: Input file not found at: " + inputFile.getAbsolutePath());
            return;
        }

        Graph graph = loadGraphFromExcel(inputFile);
        if (graph.getAllNodes().isEmpty()) {
            System.out.println("Error: No data was loaded from the input file");
            return;
        }

        PageRank pageRank = new PageRank(graph, 0.85, 100);
        Map<Node, Double> pageRankScores = pageRank.computePageRank();

        List<Map.Entry<Node, Double>> sortedScores = new ArrayList<>(pageRankScores.entrySet());
        sortedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Create and write to Excel file
        createExcelFile(sortedScores);
    }

    private static Graph loadGraphFromExcel(File inputFile) {
        Graph graph = new Graph();

        try (FileInputStream fis = new FileInputStream(inputFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet userSheet = workbook.getSheet("Users");
            if (userSheet == null) {
                System.out.println("Error: 'Users' sheet not found in input file");
                return graph;
            }

            Sheet relationSheet = workbook.getSheet("UserFollows");
            if (relationSheet == null) {
                System.out.println("Error: 'UserFollows' sheet not found in input file");
                return graph;
            }

            // Load users
            for (Row row : userSheet) {
                if (row.getRowNum() == 0) continue;
                try {
                    int userId = (int) getNumericCellValue(row.getCell(0));
                    String username = row.getCell(1).getStringCellValue();
                    int followerCount = (int) getNumericCellValue(row.getCell(4));
                    int followingCount = (int) getNumericCellValue(row.getCell(5));
                    String linkToProfile = row.getCell(3).getStringCellValue();
                    User user = new User(userId, username, followerCount, followingCount, linkToProfile);
                    graph.addNode(user);
                } catch (Exception e) {
                    System.out.println("Warning: Skipping invalid user row " + (row.getRowNum() + 1));
                }
            }

            // Load relationships
            for (Row row : relationSheet) {
                if (row.getRowNum() == 0) continue;
                try {
                    int sourceId = (int) getNumericCellValue(row.getCell(1));
                    int targetId = (int) getNumericCellValue(row.getCell(2));
                    Node sourceNode = findNodeById(graph, sourceId);
                    Node targetNode = findNodeById(graph, targetId);
                    if (sourceNode != null && targetNode != null) {
                        graph.addEdge(sourceNode, targetNode, EdgeType.FOLLOW, 1.0);
                    }
                } catch (Exception e) {
                    System.out.println("Warning: Skipping invalid relationship row " + (row.getRowNum() + 1));
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading input file: " + e.getMessage());
        }

        return graph;
    }

    private static double getNumericCellValue(Cell cell) {
        if (cell == null) return 0;
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    return Double.parseDouble(cell.getStringCellValue());
                default:
                    return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    private static Node findNodeById(Graph graph, int id) {
        for (Node node : graph.getAllNodes()) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }

    private static void createExcelFile(List<Map.Entry<Node, Double>> sortedScores) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Rankings");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Rank");
            headerRow.createCell(1).setCellValue("User ID");
            headerRow.createCell(2).setCellValue("Username");
            headerRow.createCell(3).setCellValue("Followers Count");
            headerRow.createCell(4).setCellValue("PageRank");

            // Populate data rows
            int rowNum = 1;
            int rank = 1;
            for (Map.Entry<Node, Double> entry : sortedScores) {
                User user = (User) entry.getKey();
                if (user.getFollowerCount() >= MIN_FOLLOWER_COUNT) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rank);
                    row.createCell(1).setCellValue(user.getId());
                    row.createCell(2).setCellValue(user.getUsername());
                    row.createCell(3).setCellValue(user.getFollowerCount());
                    row.createCell(4).setCellValue(entry.getValue());
                }
                rank++;
            }

            // Write the output to a file
            File outputFile = new File(OUTPUT_FILE);
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
                System.out.println("Excel file created successfully");
            }

        } catch (IOException e) {
            System.out.println("Error creating output file: " + e.getMessage());
        }
    }
}