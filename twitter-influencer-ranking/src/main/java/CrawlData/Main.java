package CrawlData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        String[] filePaths1 = {"D:\\Documents D\\userName1.txt"};
        String filePaths2 = "D:\\Documents D\\CrawlResult.xlsx";
        for (int i = 0; i < filePaths1.length; i++) {
            try {
                CrawlTask task = new CrawlTask(filePaths1[i], filePaths2);
                executor.execute(task);
                Thread.sleep(35000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }
}
