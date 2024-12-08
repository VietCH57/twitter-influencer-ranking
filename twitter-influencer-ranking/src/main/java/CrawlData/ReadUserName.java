package CrawlData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadUserName extends ReadKeyWord {

    public ReadUserName() {}

    @Override
    public void setLinks(){
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("@")) {
                    // Loại bỏ '@' và ghép vào URL
                    links.add("https://x.com/" + line.substring(1));
                }
            }
        } catch (IOException e) {
            System.out.println("Co loi trong qua trinh tao link user" + e.getMessage());
        }
    }

}
