package CrawlData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadKeyWord {

    List<String> links = new ArrayList<>();
    String filePath;

    public ReadKeyWord() {}

    public void setfilePath (String filePath) {
        this.filePath = filePath;
    }

    public void setLinks(){
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                links.add("https://x.com/search?q=" + line + "&src=typed_query");
            }
        } catch (IOException e) {
            System.out.println("Co loi trong qua trinh tao link search" + e.getMessage());
        }
    }

    public void getLinksSize(){
        System.out.println("So link tim kiem: " + links.size());
    }

    public List<String> getLinks(){
        return links;
    }
}