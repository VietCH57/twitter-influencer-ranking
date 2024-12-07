package TwitRank;

import TwitRank.graph.Graph;
import TwitRank.graph.GraphLoader;
import TwitRank.elements.Node;
import TwitRank.rank.PageRankCalculator;
import TwitRank.util.FileManager;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PagerankExecuter {

    private static final String INPUT_FILE = "cleaned_data.xlsx";
    private static final String OUTPUT_FILE = "ranking_output.xlsx";

    public static void main(String[] args) {
        System.out.println("Twitter Influencer Ranking Program");
        System.out.println("Current User: " + System.getProperty("user.name"));

        ZonedDateTime utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Start Time (UTC): " + utcTime.format(formatter));

        String workingDir = System.getProperty("user.dir");
        System.out.println("Working Directory = " + workingDir);

        File inputFile = new File(INPUT_FILE);
        System.out.println("Looking for input file at: " + inputFile.getAbsolutePath());

        if (!inputFile.exists()) {
            System.out.println("Error: Input file not found at: " + inputFile.getAbsolutePath());
            return;
        }

        GraphLoader graphLoader = new GraphLoader();
        Graph graph = graphLoader.loadGraphFromExcel(inputFile);

        if (graph.getAllNodes().isEmpty()) {
            System.out.println("Error: No data was loaded from the input file");
            return;
        }

        System.out.println("Successfully loaded graph data. Computing PageRank scores...");

        PageRankCalculator pageRankCalculator = new PageRankCalculator();
        Map<Node, Double> pageRankScores = pageRankCalculator.computePageRank(graph, 0.85, 100);

        List<Map.Entry<Node, Double>> sortedScores = new ArrayList<>(pageRankScores.entrySet());
        sortedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        System.out.println("Output will be written to: " + OUTPUT_FILE);

        FileManager fileManager = new FileManager();
        fileManager.createExcelFile(sortedScores, OUTPUT_FILE);

        utcTime = ZonedDateTime.now(ZoneOffset.UTC);
        System.out.println("End Time (UTC): " + utcTime.format(formatter));
    }
}