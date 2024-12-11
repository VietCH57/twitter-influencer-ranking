package GUI.handler;

import ExcelDataTransformer.processors.TopTargetUsersSelector;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class TopUsersSelectHandler {
    private TopTargetUsersSelector topTargetUsersSelector;
    private Label label_select_top_users;

    public TopUsersSelectHandler(Label statusLabel) {
        this.topTargetUsersSelector = new TopTargetUsersSelector();
        this.label_select_top_users = statusLabel;
    }

    public void selectTopUsers() {
        label_select_top_users.setManaged(true);
        label_select_top_users.setText("Top Users Selecting...");

        new Thread(() -> {
            TopTargetUsersSelector topTargetUsersSelector = new TopTargetUsersSelector();
            topTargetUsersSelector.selectTopTargetUsers();

            Platform.runLater(() -> {
                label_select_top_users.setText("Top users selection completed. Output saved to: " + "top_300_targets.xlsx");
            });
        }).start();
    }
}
