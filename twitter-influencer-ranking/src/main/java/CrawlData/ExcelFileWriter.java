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
    private String fileName;

    public ExcelFileWriter(String fileName) {
        this.workbook = new XSSFWorkbook();
        this.sheet1 = workbook.createSheet("User");
        this.sheet2 = workbook.createSheet("User Follower");
        this.sheet3 = workbook.createSheet("User Following");
        this.sheet4 = workbook.createSheet("User Repost");
        this.sheet5 = workbook.createSheet("User Comment");
        this.fileName = fileName;
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

        for (int i = 0; i < usersFollower.size(); i++) {
            row2.createCell(i).setCellValue(usersFollower.get(i));
        }

        Row row3 = sheet3.createRow(rowIndex);
        List<String> usersFollowing = page.getList("Following");
        for (int i = 0; i < usersFollowing.size(); i++) {
            row3.createCell(i).setCellValue(usersFollowing.get(i));
        }

        Row row4 = sheet4.createRow(rowIndex);
        List<String> usersRepost = page.getList("Repost");
        for (int i = 0; i < usersRepost.size(); i++) {
            row4.createCell(i).setCellValue(usersRepost.get(i));
        }

        Row row5 = sheet5.createRow(rowIndex);
        List<String> usersComment = page.getList("Comment");
        for (int i = 0; i < usersComment.size(); i++) {
            row5.createCell(i).setCellValue(usersComment.get(i));
        }
    }

    public void saveToFile() {
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
