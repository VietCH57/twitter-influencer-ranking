package GUI.scene;

import CrawlData.RunMainCrawl;
import GUI.handler.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class CrawlScene extends BaseScene {
    private RunMainCrawl crawler;
    private ComboBox<Integer> threadSelector;
    private Label label_crawl, label_transform, label_clean, label_filter, label_select_top_users, label_recrawl;

    public CrawlScene(VBox buttonContainer) {
        super(buttonContainer);
        this.crawler = new RunMainCrawl();
    }

    @Override
    protected void createContent() {
        VBox crawlContent = new VBox(10);
        crawlContent.setPadding(new Insets(15));

        Label titleLabel = new Label("Crawl Data");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Create thread selector
        threadSelector = new ComboBox<>();
        threadSelector.getItems().addAll(1, 2, 3, 4);
        threadSelector.setValue(1); // Default value

        // Crawl button
        Button crawlButton = new Button("Crawl");
        crawlButton.setOnAction(e -> startCrawling());

        // Status label
        label_crawl = new Label();
        label_crawl.setManaged(false);
        label_transform = new Label();
        label_transform.setManaged(false);
        label_clean = new Label();
        label_clean.setManaged(false);
        label_filter = new Label();
        label_filter.setManaged(false);
        label_select_top_users = new Label();
        label_select_top_users.setManaged(false);
        label_recrawl = new Label();
        label_recrawl.setManaged(false);

        // Transform crawl data file button
        Button transformDataButton = new Button("Transform Data File");
        DataTransformerHandler dataTransformerHandler = new DataTransformerHandler(label_transform, label_clean);
        transformDataButton.setOnAction(e -> {
            try {
                dataTransformerHandler.transformData();
            } catch (Exception ex) {
                label_transform.setText("Error: " + ex.getMessage());
                transformDataButton.setDisable(false);
            }
        });

        // Filter data button
        Button filterDataButton = new Button("Filter Data");
        DataFilterHandler dataFilterHandler = new DataFilterHandler(label_filter);
        filterDataButton.setOnAction(e -> dataFilterHandler.filterData());

        // Select top users button
        Button selectTopUsersButton = new Button("Select Top Users");
        TopUsersSelectHandler topUsersSelectHandler = new TopUsersSelectHandler(label_select_top_users);
        selectTopUsersButton.setOnAction(e -> topUsersSelectHandler.selectTopUsers());

        // ReCrawl button
        Button reCrawlButton = new Button("ReCrawl");
        ReCrawler reCrawler = new ReCrawler(label_recrawl);
        reCrawlButton.setOnAction(e -> reCrawler.reCrawl());

        crawlContent.getChildren().addAll(
                titleLabel,
                new Label("Select number of threads:"),
                threadSelector,
                crawlButton,
                label_crawl,
                transformDataButton,
                label_transform,
                filterDataButton,
                label_filter,
                selectTopUsersButton,
                label_select_top_users,
                reCrawlButton,
                label_recrawl
        );

        layout.setCenter(crawlContent);
    }

    private void startCrawling() {
        int numThreads = threadSelector.getValue();
        label_crawl.setManaged(true);
        label_crawl.setText("Crawling with " + numThreads + " thread(s)...");

        // Select Excel output file
        ExcelFileSelector excelFileSelector = new ExcelFileSelector();
        File selectedExcelFile = excelFileSelector.selectInputFile();
        if (selectedExcelFile == null) {
            label_crawl.setText("No Excel file selected.");
            return;
        }
        String outputFile = selectedExcelFile.toString();

        // Show WaitingScene
        WaitingScene waitingScene = new WaitingScene(this::stopCrawling);
        Stage stage = (Stage) layout.getScene().getWindow();
        stage.setScene(new Scene(waitingScene.getWaitingContent(), 900,600));

        // Start crawling (in a separate thread to avoid blocking the UI)
        new Thread(() -> {
            try {
                switch (numThreads) {
                    case 1:
                        // Select single text input file
                        TxtFileSelector txtFileSelector = new TxtFileSelector();
                        File selectedTxtFile = txtFileSelector.selectInputFile();
                        if (selectedTxtFile == null) {
                            updateLabelOnUIThread(label_crawl, "No text file selected.");
                            return;
                        }
                        String keywordFile = selectedTxtFile.toString();
                        crawler.RunSingleThread(keywordFile, outputFile);
                        break;
                    case 2:
                        // Select two text input files
                        File selectedTxtFile1 = new TxtFileSelector().selectInputFile();
                        File selectedTxtFile2 = new TxtFileSelector().selectInputFile();
                        if (selectedTxtFile1 == null || selectedTxtFile2 == null) {
                            updateLabelOnUIThread(label_crawl, "Two text files must be selected.");
                            return;
                        }
                        crawler.RunMultiThread(selectedTxtFile1.toString(), selectedTxtFile2.toString(), outputFile);
                        break;
                    case 3:
                        // Select three text input files
                        File selectedTxtFile3 = new TxtFileSelector().selectInputFile();
                        File selectedTxtFile4 = new TxtFileSelector().selectInputFile();
                        File selectedTxtFile5 = new TxtFileSelector().selectInputFile();
                        if (selectedTxtFile3 == null || selectedTxtFile4 == null || selectedTxtFile5 == null) {
                            updateLabelOnUIThread(label_crawl, "Three text files must be selected.");
                            return;
                        }
                        crawler.RunMultiThread(selectedTxtFile3.toString(), selectedTxtFile4.toString(), selectedTxtFile5.toString(), outputFile);
                        break;
                    case 4:
                        // Select four text input files
                        File selectedTxtFile6 = new TxtFileSelector().selectInputFile();
                        File selectedTxtFile7 = new TxtFileSelector().selectInputFile();
                        File selectedTxtFile8 = new TxtFileSelector().selectInputFile();
                        File selectedTxtFile9 = new TxtFileSelector().selectInputFile();
                        if (selectedTxtFile6 == null || selectedTxtFile7 == null || selectedTxtFile8 == null || selectedTxtFile9 == null) {
                            updateLabelOnUIThread(label_crawl, "Four text files must be selected.");
                            return;
                        }
                        crawler.RunMultiThread(selectedTxtFile6.toString(), selectedTxtFile7.toString(), selectedTxtFile8.toString(), selectedTxtFile9.toString(), outputFile);
                        break;
                    default:
                        updateLabelOnUIThread(label_crawl, "Invalid number of threads selected.");
                        return;
                }
                updateLabelOnUIThread(label_crawl, "Crawling completed!");
            } catch (Exception e) {
                updateLabelOnUIThread(label_crawl, "Error: " + e.getMessage());
            } finally {
                // Return to CrawlScene after crawling is finished
                javafx.application.Platform.runLater(() -> stage.setScene(new Scene(layout)));
            }
        }).start();
    }

    private void stopCrawling() {
        // Implement logic to stop crawling if possible
        label_crawl.setText("Crawling stopped.");
    }

    private void updateLabelOnUIThread(Label label, String text) {
        javafx.application.Platform.runLater(() -> {
            label.setText(text);
        });
    }
}