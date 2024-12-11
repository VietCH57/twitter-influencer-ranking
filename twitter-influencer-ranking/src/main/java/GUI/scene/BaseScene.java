package GUI.scene;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public abstract class BaseScene extends Scene {
    protected BorderPane layout;
    protected VBox buttonContainer;

    public BaseScene(VBox buttonContainer) {
        super(new BorderPane(), 1024, 768);
        this.buttonContainer = buttonContainer;
        this.layout = (BorderPane) getRoot();
        this.layout.setLeft(buttonContainer);
        createContent();
    }

    protected abstract void createContent();
}
