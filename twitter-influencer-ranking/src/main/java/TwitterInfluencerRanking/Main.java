package TwitterInfluencerRanking;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class Main {

    private static final String INPUT_FILE = "transformed_data.xlsx";

    public static void main(String[] args) {
        Graph graph = loadGraphFromExcel(INPUT_FILE);
        PageRank pageRank = new PageRank(graph, 0.85, 100);
        Map<Node, Double> pageRankScores = pageRank.computePageRank();

        List<Map.Entry<Node, Double>> sortedScores = new ArrayList<>(pageRankScores.entrySet());
        sortedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        System.out.println("Ranking of Key Opinion Leaders (KoLs):");
        for (Map.Entry<Node, Double> entry : sortedScores) {
            User user = (User) entry.getKey();
            System.out.println("User ID: " + user.getId() + ", Username: " + user.getUsername() + ", PageRank: " + entry.getValue());
        }
    }

    private static Graph loadGraphFromExcel(String filePath) {
        Graph graph = new Graph();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet userSheet = workbook.getSheet("Users");
            for (Row row : userSheet) {
                if (row.getRowNum() == 0) continue;
                int userId = (int) row.getCell(0).getNumericCellValue();
                String username = row.getCell(1).getStringCellValue();
                int followerCount = (int) row.getCell(4).getNumericCellValue();
                int followingCount = (int) row.getCell(5).getNumericCellValue();
                String linkToProfile = row.getCell(3).getStringCellValue();
                User user = new User(userId, username, followerCount, followingCount, linkToProfile);
                graph.addNode(user);
            }

            Sheet relationSheet = workbook.getSheet("UserFollows");
            for (Row row : relationSheet) {
                if (row.getRowNum() == 0) continue;
                int sourceId = (int) row.getCell(1).getNumericCellValue();
                int targetId = (int) row.getCell(2).getNumericCellValue();
                Node sourceNode = findNodeById(graph, sourceId);
                Node targetNode = findNodeById(graph, targetId);
                if (sourceNode != null && targetNode != null) {
                    graph.addEdge(sourceNode, targetNode, EdgeType.FOLLOW, 1.0);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }

    private static Node findNodeById(Graph graph, int id) {
        for (Node node : graph.getAllNodes()) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }
}