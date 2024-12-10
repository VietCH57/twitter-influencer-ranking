package CrawlData;

import java.util.concurrent.CountDownLatch;

public class CrawlTask1 implements Runnable{
    private String filePath1;
    private String filePath2;
    private ExcelFileWriter excelFileWriter;
    private CountDownLatch latch;

    public CrawlTask1(String filePath1, String filePath2, ExcelFileWriter excelFileWriter, CountDownLatch latch) {
        this.filePath1 = filePath1;
        this.filePath2 = filePath2;
        this.excelFileWriter = excelFileWriter;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            ReCrawl mainCrawl = new ReCrawl();
            mainCrawl.ControlMainCrawl(filePath1, filePath2, excelFileWriter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }
}
