package GUI.handler;

import ExcelDataTransformer.DataTransformer;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.File;

public class DataTransformerHandler {
    private Label label_transform;
    private Label label_clean;

    public DataTransformerHandler(Label statusLabel_transform, Label statusLabel_clean) {
        this.label_transform = statusLabel_transform;
        this.label_clean = statusLabel_clean;
    }

    public void transformData() {
        ExcelFileSelector ExcelFileSelector = new ExcelFileSelector();
        File selectedFile = ExcelFileSelector.selectInputFile();
        String inputFile = selectedFile.toString();

        label_transform.setManaged(true);
        label_transform.setText("Data Transforming");

        if (selectedFile != null) {
            new Thread(() -> {
                DataTransformer dataTransformer = new DataTransformer();
                dataTransformer.transformData(inputFile);

                Platform.runLater(() -> {
                    label_transform.setText("Data transformed");
                    label_clean.setManaged(true);
                    label_clean.setText("Ready to clean data");
                });
            }).start();
        }
    }
}