package CrawlData;

public class CrawlTask implements Runnable{
    private String filePath1;
    private String filePath2;

    public CrawlTask(String filePath1, String filePath2) {
        this.filePath1 = filePath1;
        this.filePath2 = filePath2;
    }

    @Override
    public void run() {
            MainCrawl1 mainCrawl = new MainCrawl1();
            mainCrawl.ControlMainCrawl(filePath1, filePath2);
    }
}
