package CrawlData;

public class Main {
    public static void main(String[] args) {
        RunMainCrawl runMainCrawl = new RunMainCrawl();
        runMainCrawl.RunSingleThread( "C:\\Users\\AD\\Downloads\\keyword.txt", "C:\\Users\\AD\\Downloads\\Output.xlsx");
    }
}
