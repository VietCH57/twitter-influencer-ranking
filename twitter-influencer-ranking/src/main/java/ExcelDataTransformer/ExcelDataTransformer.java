package ExcelDataTransformer;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class ExcelDataTransformer {
    private static final String INPUT_FILE = "D:\\OOP Project\\twitter-influencer-ranking\\twitter-influencer-ranking\\input.xlsx";
    private static final String OUTPUT_FILE = "transformed_data.xlsx";

    public void transformData() {
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

            transformUserAndTweetSheets(inputWorkbook, outputWorkbook);
            transformRelationshipSheets(inputWorkbook, outputWorkbook);
            transformRepostSheet(inputWorkbook, outputWorkbook);

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

        int userRowNum = 1;
        int tweetRowNum = 1;
        int userId = 1;
        int tweetId = 1;
        Map<String, Integer> userIdMap = new HashMap<>();

        for (Row row : inputSheet) {
            if (row.getRowNum() == 0) continue;

            String userName = getCellValueAsString(row.getCell(0));
            if (userName == null || userName.trim().isEmpty()) continue;

            Row userRow = userSheet.createRow(userRowNum++);
            userRow.createCell(0).setCellValue(userId);
            userRow.createCell(1).setCellValue(userName);
            userRow.createCell(2).setCellValue(getCellValueAsString(row.getCell(1)));
            userRow.createCell(3).setCellValue(getCellValueAsString(row.getCell(2)));
            userRow.createCell(4).setCellValue(getNumericCellValue(row.getCell(3)));
            userRow.createCell(5).setCellValue(getNumericCellValue(row.getCell(4)));
            userRow.createCell(6).setCellValue(getNumericCellValue(row.getCell(5)));

            userIdMap.put(userName, userId);

            Row tweetRow = tweetSheet.createRow(tweetRowNum++);
            tweetRow.createCell(0).setCellValue(tweetId++);
            tweetRow.createCell(1).setCellValue(userId);
            tweetRow.createCell(2).setCellValue(getNumericCellValue(row.getCell(6)));
            tweetRow.createCell(3).setCellValue(getNumericCellValue(row.getCell(7)));
            tweetRow.createCell(4).setCellValue(getNumericCellValue(row.getCell(8)));
            tweetRow.createCell(5).setCellValue(getCellValueAsString(row.getCell(9)));

            userId++;
        }
    }

    private void transformRelationshipSheets(Workbook input, Workbook output) {
        Sheet outputSheet = output.createSheet("UserFollows");

        Row headerRow = outputSheet.createRow(0);
        headerRow.createCell(0).setCellValue("id");
        headerRow.createCell(1).setCellValue("targetUserId");
        headerRow.createCell(2).setCellValue("relatedUserId");
        headerRow.createCell(3).setCellValue("relationshipType");

        int outputRow = 1;
        int relationshipId = 1;

        processRelationships(input.getSheetAt(1), outputSheet, "FOLLOWER", outputRow, relationshipId);

        outputRow = outputSheet.getLastRowNum() + 1;
        relationshipId = outputRow;
        processRelationships(input.getSheetAt(2), outputSheet, "FOLLOWING", outputRow, relationshipId);
    }

    private void processRelationships(Sheet inputSheet, Sheet outputSheet,
                                      String relationshipType, int startingRow, int startingId) {
        int currentRow = startingRow;
        int currentId = startingId;

        for (Row row : inputSheet) {
            if (row.getRowNum() == 0) continue;

            String targetUser = getCellValueAsString(row.getCell(0));
            if (targetUser == null || targetUser.trim().isEmpty()) continue;

            int targetUserId;
            try {
                targetUserId = Integer.parseInt(targetUser.replace("User", ""));
            } catch (NumberFormatException e) {
                continue;
            }

            for (int i = 1; i < row.getLastCellNum(); i += 3) {
                String relatedUser = getCellValueAsString(row.getCell(i));
                if (relatedUser == null || relatedUser.trim().isEmpty()) continue;

                int relatedUserId;
                try {
                    relatedUserId = Integer.parseInt(relatedUser.replace("User", ""));
                } catch (NumberFormatException e) {
                    continue;
                }

                Row outputRow = outputSheet.createRow(currentRow++);
                outputRow.createCell(0).setCellValue(currentId++);
                outputRow.createCell(1).setCellValue(targetUserId);
                outputRow.createCell(2).setCellValue(relatedUserId);
                outputRow.createCell(3).setCellValue(relationshipType);
            }
        }
    }

    private void transformRepostSheet(Workbook input, Workbook output) {
        Sheet inputSheet = input.getSheetAt(3);
        Sheet outputSheet = output.createSheet("TweetInteractions");

        Row headerRow = outputSheet.createRow(0);
        headerRow.createCell(0).setCellValue("id");
        headerRow.createCell(1).setCellValue("tweetId");
        headerRow.createCell(2).setCellValue("userId");
        headerRow.createCell(3).setCellValue("interactionType");

        int outputRow = 1;
        int interactionId = 1;

        for (Row row : inputSheet) {
            if (row.getRowNum() == 0) continue;

            String originalUser = getCellValueAsString(row.getCell(0));
            if (originalUser == null || originalUser.trim().isEmpty()) continue;

            int originalUserId;
            try {
                originalUserId = Integer.parseInt(originalUser.replace("User", ""));
            } catch (NumberFormatException e) {
                continue;
            }

            for (int i = 1; i < row.getLastCellNum(); i += 3) {
                String repostUser = getCellValueAsString(row.getCell(i));
                if (repostUser == null || repostUser.trim().isEmpty()) continue;

                int repostUserId;
                try {
                    repostUserId = Integer.parseInt(repostUser.replace("User", ""));
                } catch (NumberFormatException e) {
                    continue;
                }

                Row newRow = outputSheet.createRow(outputRow++);
                newRow.createCell(0).setCellValue(interactionId++);
                newRow.createCell(1).setCellValue(originalUserId);
                newRow.createCell(2).setCellValue(repostUserId);
                newRow.createCell(3).setCellValue("RETWEET");
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long)cell.getNumericCellValue());
            default -> null;
        };
    }

    private double getNumericCellValue(Cell cell) {
        if (cell == null) return 0;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    yield 0;
                }
            }
            default -> 0;
        };
    }

    public static void main(String[] args) {
        new ExcelDataTransformer().transformData();
    }
}