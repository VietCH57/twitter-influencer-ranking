package CrawlData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(10);
        String[] filePaths1 = {"C:\\Users\\Administrator\\Documents\\userName1.txt", "C:\\Users\\Administrator\\Documents\\userName2.txt", "C:\\Users\\Administrator\\Documents\\userName3.txt", "C:\\Users\\Administrator\\Documents\\userName4.txt", "C:\\Users\\Administrator\\Documents\\userName5.txt", "C:\\Users\\Administrator\\Documents\\userName6.txt", "C:\\Users\\Administrator\\Documents\\userName7.txt", "C:\\Users\\Administrator\\Documents\\userName8.txt", "C:\\Users\\Administrator\\Documents\\userName9.txt", "C:\\Users\\Administrator\\Documents\\userName10.txt"};
        String filePaths2 = "C:\\Users\\Administrator\\Documents\\CrawlResult.xlsx";
        ExcelFileWriter ew = new ExcelFileWriter(filePaths2);
        for (int i = 0; i < filePaths1.length; i++) {
            try {
                CrawlTask task = new CrawlTask(filePaths1[i], filePaths2, ew);
                executor.execute(task);
                Thread.sleep(35000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }
}
