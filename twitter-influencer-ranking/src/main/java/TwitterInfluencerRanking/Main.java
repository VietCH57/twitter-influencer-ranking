package TwitterInfluencerRanking;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final String INPUT_FILE = "transformed_data.xlsx";
    private static final String OUTPUT_FILE = "ranking_output.xlsx";

    public static void main(String[] args) {
        // Print program start information
        System.out.println("Twitter Influencer Ranking Program");
        System.out.println("Current User: " + System.getProperty("user.name"));

        // Print current UTC time
        ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Start Time (UTC): " + utcTime.format(formatter));

        // Print working directory and input file path for verification
        String workingDir = System.getProperty("user.dir");
        System.out.println("Working Directory = " + workingDir);

        File inputFile = new File(INPUT_FILE);
        System.out.println("Looking for input file at: " + inputFile.getAbsolutePath());

        if (!inputFile.exists()) {
            System.out.println("Error: Input file not found at: " + inputFile.getAbsolutePath());
            return;
        }

        Graph graph = loadGraphFromExcel(inputFile);
        if (graph.getAllNodes().isEmpty()) {
            System.out.println("Error: No data was loaded from the input file");
            return;
        }

        System.out.println("Successfully loaded graph data. Computing PageRank scores...");

        PageRank pageRank = new PageRank(graph, 0.85, 100);
        Map<Node, Double> pageRankScores = pageRank.computePageRank();

        List<Map.Entry<Node, Double>> sortedScores = new ArrayList<>(pageRankScores.entrySet());
        sortedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Print output file path for verification
        File outputFile = new File(OUTPUT_FILE);
        System.out.println("Output will be written to: " + outputFile.getAbsolutePath());

        // Create and write to Excel file
        createExcelFile(sortedScores);

        // Print program completion time
        utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        System.out.println("End Time (UTC): " + utcTime.format(formatter));
    }

    public static Graph loadGraphFromExcel(File inputFile) {
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

            System.out.println("Loading users from Excel...");
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

            System.out.println("Loading user relationships from Excel...");
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
            return switch (cell.getCellType()) {
                case NUMERIC -> cell.getNumericCellValue();
                case STRING -> Double.parseDouble(cell.getStringCellValue());
                default -> 0;
            };
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
            headerRow.createCell(4).setCellValue("PageRank Score");

            // Populate data rows
            int rowNum = 1;
            int rank = 1;
            int totalUsers = 0;

            System.out.println("Creating ranking output file...");

            for (Map.Entry<Node, Double> entry : sortedScores) {
                User user = (User) entry.getKey();
                if (user.getFollowerCount() >= KoL.getMinFollowerCount()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rank++);  // Increment rank only when creating a row
                    row.createCell(1).setCellValue(user.getId());
                    row.createCell(2).setCellValue(user.getUsername());
                    row.createCell(3).setCellValue(user.getFollowerCount());
                    row.createCell(4).setCellValue(entry.getValue());
                    totalUsers++;
                }
            }

            // Auto-size columns for better readability
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            File outputFile = new File(OUTPUT_FILE);
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
                System.out.println("Ranking file created successfully with " + totalUsers + " users ranked");
            }

        } catch (IOException e) {
            System.out.println("Error creating output file: " + e.getMessage());
        }
    }

    public static List<Map.Entry<Node, Double>> sortAndFilterPageRankScores(Map<Node, Double> pageRankScores) {
        List<Map.Entry<Node, Double>> sortedScores = new ArrayList<>(pageRankScores.entrySet());
        sortedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return sortedScores.stream()
                .filter(entry -> {
                    User user = (User) entry.getKey();
                    return user.getFollowerCount() >= KoL.getMinFollowerCount();
                })
                .collect(Collectors.toList());
    }
}