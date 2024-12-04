package CrawlData;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelFileWriter {
    private static Workbook workbook = new XSSFWorkbook();
    private static Sheet sheet1 = workbook.createSheet("User");
    private static Sheet sheet2 = workbook.createSheet("User Follower");
    private static Sheet sheet3 = workbook.createSheet("User Following");
    private static Sheet sheet4 = workbook.createSheet("User Repost");
    private static Sheet sheet5 = workbook.createSheet("User Comment");
    private static int currentRow = 0;

    public ExcelFileWriter() {}

    public void writeRowData(Page page){
        currentRow = page.getID();
        Row row = sheet1.createRow(currentRow);

        row.createCell(0).setCellValue(page.getName());
        row.createCell(1).setCellValue(page.getUserName());
        row.createCell(2).setCellValue(page.getNumOfFollower());
        row.createCell(3).setCellValue(page.getNumOfFollowing());
        row.createCell(4).setCellValue(page.getLinkPage());
        row.createCell(5).setCellValue(page.getLinkHotTweet());
        row.createCell(6).setCellValue(page.getNumOfViewInHotTweet());
        row.createCell(7).setCellValue(page.getNumOfReactInHotTweet());
        row.createCell(8).setCellValue(page.getNumOfCommentInHotTweet());
        row.createCell(9).setCellValue(page.getNumOfRepostInHotTweet());

        Row row2 = sheet2.createRow(currentRow);

        for (int i = 0; i < page.listFollower.size(); i++) {
            row2.createCell(i).setCellValue(page.listFollower.get(i));
        }

        Row row3 = sheet3.createRow(currentRow);

        for (int i = 0; i < page.listFollowing.size(); i++) {
            row3.createCell(i).setCellValue(page.listFollowing.get(i));
        }

        Row row4 = sheet4.createRow(currentRow);

        for (int i = 0; i < page.listUserNameRepost.size(); i++) {
            row4.createCell(i).setCellValue(page.listUserNameRepost.get(i));
        }

        Row row5 = sheet5.createRow(currentRow);

        for (int i = 0; i < page.listUserNameComment.size(); i++) {
            row5.createCell(i).setCellValue(page.listUserNameComment.get(i));
        }

        currentRow++;

    }

    public static void saveToFile(){
        String fileName = "D:\\Documents D\\CrawlResult.xlsx";
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            System.out.println("File written to: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeWorkbook(){
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
