package GUI.handler;

import CrawlData.ExcelFileWriter;
import CrawlData.ReCrawl;
import ExcelDataTransformer.processors.TopTargetUsersSelector;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class ReCrawler {
    private ReCrawl reCrawl;
    private Label label_recrawl;

    public ReCrawler(Label statusLabel) {
        this.reCrawl = new ReCrawl();
        this.label_recrawl = statusLabel;
    }

    public void reCrawl() {
        label_recrawl.setManaged(true);
        label_recrawl.setText("ReCrawling...");

        new Thread(() -> {
            ReCrawl reCrawl = new ReCrawl();
            ExcelFileWriter excelWriter = new ExcelFileWriter("ReCrawl.xlsx");
            reCrawl.ControlMainCrawl("KeyWord1", "KeyWord2", excelWriter);

            Platform.runLater(() -> {
                label_recrawl.setText("Data recrawled. Output saved to: " + "ReCrawl.xlsx");
            });
        }).start();
    }
}
