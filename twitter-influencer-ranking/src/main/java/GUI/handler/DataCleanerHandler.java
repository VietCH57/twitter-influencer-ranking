package GUI.handler;

import ExcelDataTransformer.processors.DataCleaner;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class DataCleanerHandler {
    private final Label label_clean;

    public DataCleanerHandler(Label statusLabel_clean) {
        this.label_clean = statusLabel_clean;
    }

    public void cleanData() {
        label_clean.setManaged(true);
        label_clean.setText("Data Cleaning...");

        new Thread(() -> {
            DataCleaner dataCleaner = new DataCleaner();
            dataCleaner.cleanData();

            Platform.runLater(() -> {
                label_clean.setText("Data cleaned");
            });
        }).start();
    }
}