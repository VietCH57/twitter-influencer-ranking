package CrawlData;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class ExcelFileWriter {
    private Workbook workbook;
    private Sheet sheet1;
    private Sheet sheet2;
    private Sheet sheet3;
    private Sheet sheet4;
    private Sheet sheet5;

    public ExcelFileWriter() {
        this.workbook = new XSSFWorkbook();
        this.sheet1 = workbook.createSheet("User");
        this.sheet2 = workbook.createSheet("User Follower");
        this.sheet3 = workbook.createSheet("User Following");
        this.sheet4 = workbook.createSheet("User Repost");
        this.sheet5 = workbook.createSheet("User Comment");
    }

    public void writePageData(Page page){
        int rowIndex = page.getId();

        //Ghi thong tin co ban cua user
        Row row1 = sheet1.createRow(rowIndex);

        row1.createCell(0).setCellValue(page.getName());
        row1.createCell(1).setCellValue(page.getUserName());
        row1.createCell(2).setCellValue(page.getNumOfFollower());
        row1.createCell(3).setCellValue(page.getNumOfFollowing());
        row1.createCell(4).setCellValue(page.getLinkPage());
        row1.createCell(5).setCellValue(page.getLinkHotTweet());
        row1.createCell(6).setCellValue(page.getNumOfViewInHotTweet());
        row1.createCell(7).setCellValue(page.getNumOfReactInHotTweet());
        row1.createCell(8).setCellValue(page.getNumOfCommentInHotTweet());
        row1.createCell(9).setCellValue(page.getNumOfRepostInHotTweet());

        Row row2 = sheet2.createRow(rowIndex);
        List<String> usersFollower = page.getList("Follower");
        for (String user : usersFollower) {
            int i = 0;
            row2.createCell(i).setCellValue(user);
            i++;
        }

        Row row3 = sheet3.createRow(rowIndex);
        List<String> usersFollowing = page.getList("Following");
        for (String user : usersFollowing) {
            int i = 0;
            row3.createCell(i).setCellValue(user);
            i++;
        }

        Row row4 = sheet4.createRow(rowIndex);
        List<String> usersRepost = page.getList("Repost");
        for (String user : usersRepost) {
            int i = 0;
            row4.createCell(i).setCellValue(user);
            i++;
        }

        Row row5 = sheet5.createRow(rowIndex);
        List<String> usersComment = page.getList("Comment");
        for (String user : usersComment) {
            int i = 0;
            row5.createCell(i).setCellValue(user);
            i++;
        }
    }

    public void saveToFile(String fileName) {
        try (FileOutputStream fileOut = new FileOutputStream(fileName)){
            workbook.write(fileOut);
            System.out.println("File written to: " + fileName);
        } catch (IOException e) {
            System.out.println("Co loi xay ra khi ghi du lieu vào " + fileName + ": " + e.getMessage());
        }
    }

    public void close() {
        try {
            workbook.close();
        } catch (IOException e) {
            System.out.println("Co loi xay ra khi dong workbook: " + e.getMessage());
        }
    }
}
