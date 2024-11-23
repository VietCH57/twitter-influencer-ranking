package ExcelDataTransformer;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class ExcelDataTransformer {
    // Change this to your actual file path
    private static final String INPUT_FILE = "D:\\OOP Project\\twitter-influencer-ranking\\twitter-influencer-ranking\\input.xlsx"; // Change this
    private static final String OUTPUT_FILE = "transformed_data.xlsx";

    public void transformData() {
        // Print working directory for debugging
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        File inputFile = new File(INPUT_FILE);
        System.out.println("Looking for file at: " + inputFile.getAbsolutePath());

        if (!inputFile.exists()) {
            System.out.println("Error: Input file not found!");
            return;
        }

        try (FileInputStream fis = new FileInputStream(inputFile);
             Workbook inputWorkbook = new XSSFWorkbook(fis)) {

            Workbook outputWorkbook = new XSSFWorkbook();

            // Transform Sheet1 into separate Users and Tweets sheets
            transformUserAndTweetSheets(inputWorkbook, outputWorkbook);

            // Transform Follower/Following relationships
            transformRelationshipSheets(inputWorkbook, outputWorkbook);

            // Transform Repost data
            transformRepostSheet(inputWorkbook, outputWorkbook);

            // Write the output file
            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE)) {
                outputWorkbook.write(fos);
            }

        } catch (IOException e) {
            System.out.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void transformUserAndTweetSheets(Workbook input, Workbook output) {
        Sheet inputSheet = input.getSheetAt(0); // Sheet1-User
        Sheet userSheet = output.createSheet("Users");
        Sheet tweetSheet = output.createSheet("Tweets");

        // Create User sheet header
        Row userHeader = userSheet.createRow(0);
        String[] userHeaders = {"userId", "userName", "name", "linkUser", "numOfFollower",
                "numOfFollowing", "numOfView"};
        for (int i = 0; i < userHeaders.length; i++) {
            userHeader.createCell(i).setCellValue(userHeaders[i]);
        }

        // Create Tweet sheet header
        Row tweetHeader = tweetSheet.createRow(0);
        String[] tweetHeaders = {"tweetId", "userId", "numOfReact", "numOfRepost",
                "numOfComment", "linkTweet"};
        for (int i = 0; i < tweetHeaders.length; i++) {
            tweetHeader.createCell(i).setCellValue(tweetHeaders[i]);
        }

        // Process data rows
        int userRowNum = 1;
        int tweetRowNum = 1;
        int userId = 1; // Auto-increment ID for users
        int tweetId = 1; // Auto-increment ID for tweets
        Map<String, Integer> userIdMap = new HashMap<>(); // To track userName -> userId mapping

        for (Row row : inputSheet) {
            if (row.getRowNum() == 0) continue; // Skip header

            String userName = getCellValueAsString(row.getCell(0));
            if (userName == null || userName.trim().isEmpty()) continue;

            // Create User row
            Row userRow = userSheet.createRow(userRowNum++);
            userRow.createCell(0).setCellValue(userId); // userId
            userRow.createCell(1).setCellValue(userName); // userName
            userRow.createCell(2).setCellValue(getCellValueAsString(row.getCell(1))); // name
            userRow.createCell(3).setCellValue(getCellValueAsString(row.getCell(2))); // linkUser
            userRow.createCell(4).setCellValue(getNumericCellValue(row.getCell(3))); // numOfFollower
            userRow.createCell(5).setCellValue(getNumericCellValue(row.getCell(4))); // numOfFollowing
            userRow.createCell(6).setCellValue(getNumericCellValue(row.getCell(5))); // numOfView

            // Store userId mapping
            userIdMap.put(userName, userId);

            // Create Tweet row
            Row tweetRow = tweetSheet.createRow(tweetRowNum++);
            tweetRow.createCell(0).setCellValue(tweetId++); // tweetId
            tweetRow.createCell(1).setCellValue(userId); // userId (foreign key)
            tweetRow.createCell(2).setCellValue(getNumericCellValue(row.getCell(6))); // numOfReact
            tweetRow.createCell(3).setCellValue(getNumericCellValue(row.getCell(7))); // numOfRepost
            tweetRow.createCell(4).setCellValue(getNumericCellValue(row.getCell(8))); // numOfComment
            tweetRow.createCell(5).setCellValue(getCellValueAsString(row.getCell(9))); // linkTweet

            userId++;
        }
    }

    private void transformRelationshipSheets(Workbook input, Workbook output) {
        Sheet outputSheet = output.createSheet("UserFollows");

        // Create header row
        Row headerRow = outputSheet.createRow(0);
        headerRow.createCell(0).setCellValue("id");
        headerRow.createCell(1).setCellValue("targetUserId");
        headerRow.createCell(2).setCellValue("relatedUserId");
        headerRow.createCell(3).setCellValue("relationshipType");

        int outputRow = 1;
        int relationshipId = 1;

        // Process followers sheet
        processRelationships(input.getSheetAt(1), outputSheet, "FOLLOWER", outputRow, relationshipId);

        // Process following sheet
        outputRow = outputSheet.getLastRowNum() + 1;
        relationshipId = outputRow;
        processRelationships(input.getSheetAt(2), outputSheet, "FOLLOWING", outputRow, relationshipId);
    }

    private void processRelationships(Sheet inputSheet, Sheet outputSheet,
                                      String relationshipType, int startingRow, int startingId) {
        int currentRow = startingRow;
        int currentId = startingId;

        for (Row row : inputSheet) {
            if (row.getRowNum() == 0) continue; // Skip header

            String targetUser = getCellValueAsString(row.getCell(0));
            if (targetUser == null || targetUser.trim().isEmpty()) continue;

            // Process all related users (starting from column B)
            for (int i = 1; i < row.getLastCellNum(); i += 3 ){ // Skip link columns
                String relatedUser = getCellValueAsString(row.getCell(i));
                if (relatedUser == null || relatedUser.trim().isEmpty()) continue;

                Row outputRow = outputSheet.createRow(currentRow++);
                outputRow.createCell(0).setCellValue(currentId++); // id
                outputRow.createCell(1).setCellValue(targetUser);
                outputRow.createCell(2).setCellValue(relatedUser);
                outputRow.createCell(3).setCellValue(relationshipType);
            }
        }
    }

    private void transformRepostSheet(Workbook input, Workbook output) {
        Sheet inputSheet = input.getSheetAt(3); // Sheet4-User repost
        Sheet outputSheet = output.createSheet("TweetInteractions");

        // Create header row
        Row headerRow = outputSheet.createRow(0);
        headerRow.createCell(0).setCellValue("id");
        headerRow.createCell(1).setCellValue("tweetId");
        headerRow.createCell(2).setCellValue("userId");
        headerRow.createCell(3).setCellValue("interactionType");

        int outputRow = 1;
        int interactionId = 1;

        for (Row row : inputSheet) {
            if (row.getRowNum() == 0) continue; // Skip header

            String originalUser = getCellValueAsString(row.getCell(0));
            if (originalUser == null || originalUser.trim().isEmpty()) continue;

            // Process all reposting users
            for (int i = 1; i < row.getLastCellNum(); i += 3) {
                String repostUser = getCellValueAsString(row.getCell(i));
                if (repostUser == null || repostUser.trim().isEmpty()) continue;

                Row newRow = outputSheet.createRow(outputRow++);
                newRow.createCell(0).setCellValue(interactionId++);
                newRow.createCell(1).setCellValue(originalUser); // Will need to be mapped to actual tweetId
                newRow.createCell(2).setCellValue(repostUser);
                newRow.createCell(3).setCellValue("RETWEET");
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((long)cell.getNumericCellValue());
            default: return null;
        }
    }

    private double getNumericCellValue(Cell cell) {
        if (cell == null) return 0;
        switch (cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default: return 0;
        }
    }

    public static void main(String[] args) {
        new ExcelDataTransformer().transformData();
    }
}