package CrawlData;

public class CrawlTask implements Runnable{
    private String filePath1;
    private String filePath2;
    private ExcelFileWriter excelFileWriter;

    public CrawlTask(String filePath1, String filePath2, ExcelFileWriter excelFileWriter) {
        this.filePath1 = filePath1;
        this.filePath2 = filePath2;
        this.excelFileWriter = excelFileWriter;
    }

    @Override
    public void run() {
            MainCrawl1 mainCrawl = new MainCrawl1();
            mainCrawl.ControlMainCrawl(filePath1, filePath2, excelFileWriter);
    }
}
