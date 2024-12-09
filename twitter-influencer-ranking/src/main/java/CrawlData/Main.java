package CrawlData;

public class Main {
    public static void main(String[] args) {
        RunMainCrawl runMainCrawl = new RunMainCrawl();
        runMainCrawl.RunMultiThread("C:\\Users\\Admin\\IdeaProjects\\twitter-influencer-ranking\\twitter-influencer-ranking\\src\\main\\java\\CrawlData\\keyWordSearch.txt", "C:\\Users\\Admin\\IdeaProjects\\twitter-influencer-ranking\\twitter-influencer-ranking\\src\\main\\java\\CrawlData\\keyWordSearch1.txt", "D:\\Documents D\\CrawlResult.xlsx");
    }
}
