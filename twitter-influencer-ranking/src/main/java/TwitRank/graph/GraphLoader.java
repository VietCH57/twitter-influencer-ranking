package TwitRank.graph;

import TwitRank.elements.Edge;
import TwitRank.elements.EdgeType;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import TwitRank.elements.KoL;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class GraphLoader {

    public Graph loadGraphFromExcel(File inputFile) {
        Graph graph = new Graph();
        System.out.println("Loading graph from: " + inputFile.getAbsolutePath());

        try (FileInputStream fis = new FileInputStream(inputFile);
             Workbook workbook = WorkbookFactory.create(fis)) {

            loadUsers(workbook.getSheet("User"), graph);
            loadEdges(workbook.getSheet("User Follower"), graph, EdgeType.FOLLOW, true);
            loadEdges(workbook.getSheet("User Following"), graph, EdgeType.FOLLOW, false);
            loadEdges(workbook.getSheet("User Repost"), graph, EdgeType.RETWEET, true);
            loadEdges(workbook.getSheet("User Comment"), graph, EdgeType.REPLY, true);

            return graph;

        } catch (Exception e) {
            System.out.println("Error loading graph: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void loadUsers(Sheet sheet, Graph graph) {
        if (sheet == null) {
            System.out.println("Error: 'User' sheet not found in input file");
            return;
        }

        System.out.println("Loading users...");
        int rowCount = 0;

        for (Row row : sheet) {
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
    }

    private void loadEdges(Sheet sheet, Graph graph, EdgeType edgeType, boolean reverseDirection) {
        if (sheet == null) return;

        System.out.println("Loading " + edgeType + " relationships...");
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
                Edge edge;
                if (reverseDirection) {
                    edge = new Edge(targetNode, targetId, sourceNode, sourceId, edgeType, weight);
                } else {
                    edge = new Edge(sourceNode, sourceId, targetNode, targetId, edgeType, weight);
                }
                graph.addEdge(edge);
                relationCount++;

            } catch (Exception e) {
                skippedCount++;
            }
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

            // Create either a KoL or regular User based on metrics
            if (meetsKoLCriteria(followerCount, reacts, comments, reposts)) {
                return new KoL(id, name, username, followerCount, followingCount,
                        linkToProfile, linkToTweet, views, reacts, comments, reposts);
            } else {
                return new User(id, name, username, followerCount, followingCount,
                        linkToProfile, linkToTweet, views, reacts, comments, reposts);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating user from row " + (row.getRowNum() + 1) +
                    ": " + e.getMessage());
        }
    }

    public static boolean meetsKoLCriteria(int followerCount, int reacts, int comments, int reposts) {
        return followerCount >= KoL.getMinFollowerCount() &&
                reacts >= KoL.getMinReacts() &&
                comments >= KoL.getMinComments() &&
                reposts >= KoL.getMinReposts();
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
        return graph.getNodeById(id);
    }

    private double calculateEdgeWeight(EdgeType edgeType) {
        return switch (edgeType) {
            case FOLLOW -> 1.0;
            case RETWEET -> 2.0;
            case REPLY -> 3.0;
            default -> 1.0;
        };
    }
}