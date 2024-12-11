package GUI.handler;

import ExcelDataTransformer.processors.DataFilter;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class DataFilterHandler {
    private DataFilter dataFilter;
    private Label label_filter;

    public DataFilterHandler(Label statusLabel) {
        this.dataFilter = new DataFilter();
        this.label_filter = statusLabel;
    }

    public void filterData() {
        label_filter.setManaged(true);
        label_filter.setText("Data Filtering...");

        new Thread(() -> {
            DataFilter dataFilter = new DataFilter();
            dataFilter.filterRows();

            Platform.runLater(() -> {
                label_filter.setText("Data filtering completed. Output saved to: " + "filtered_data.xlsx");
            });
        }).start();
    }
}
