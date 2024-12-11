package GUI.handler;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class TxtFileSelector {
    public File selectInputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Input Text File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        Stage stage = new Stage(); // Create a new Stage for the FileChooser
        return fileChooser.showOpenDialog(stage);
    }
}
