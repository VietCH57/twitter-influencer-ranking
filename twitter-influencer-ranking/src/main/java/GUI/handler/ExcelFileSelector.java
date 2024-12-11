package GUI.handler;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ExcelFileSelector {
    public File selectInputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input Excel File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        Stage stage = new Stage(); // Create a new Stage for the FileChooser
        return fileChooser.showOpenDialog(stage);
    }
}
