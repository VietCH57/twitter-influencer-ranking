package ExcelDataTransformer.processors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ExcelDataTransformer.util.ExcelUtils;
import java.io.*;
import java.util.*;

public class DataCleaner {
    private static final String INPUT_FILE = "transformed_data.xlsx";
    private static final String OUTPUT_FILE = "cleaned_data.xlsx";

    public void cleanData() {
        try (FileInputStream fis = new FileInputStream(INPUT_FILE);
             Workbook inputWorkbook = new XSSFWorkbook(fis)) {

            Workbook outputWorkbook = new XSSFWorkbook();

            // Clean User sheet first (remove duplicate users)
            Sheet userSheet = inputWorkbook.getSheet("User");
            if (userSheet != null) {
                cleanUserSheet(userSheet, outputWorkbook);
            }

            // Clean and copy interaction sheets
            String[] sheetNames = {"User Follower", "User Following", "User Repost", "User Comment"};
            for (String sheetName : sheetNames) {
                Sheet inputSheet = inputSheet = inputWorkbook.getSheet(sheetName);
                if (inputSheet != null) {
                    cleanInteractionSheet(inputSheet, outputWorkbook);
                }
            }

            // Save cleaned data
            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE)) {
                outputWorkbook.write(fos);
                System.out.println("Data cleaning completed. Output saved to: " + OUTPUT_FILE);
            }

        } catch (IOException e) {
            System.out.println("Error during data cleaning: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cleanUserSheet(Sheet inputSheet, Workbook outputWorkbook) {
        Sheet outputSheet = outputWorkbook.createSheet("User");
        System.out.println("Cleaning User sheet...");

        // Copy header row
        Row headerRow = inputSheet.getRow(0);
        if (headerRow != null) {
            Row outputHeaderRow = outputSheet.createRow(0);
            ExcelUtils.copyRow(headerRow, outputHeaderRow);
        }

        // Use a Set to track unique users by their username (column 2)
        Map<String, Row> uniqueUsers = new LinkedHashMap<>();

        // Process all rows except header
        for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
            Row inputRow = inputSheet.getRow(i);
            if (inputRow != null) {
                Cell usernameCell = inputRow.getCell(2); // Username is in column C
                if (usernameCell != null) {
                    String username = usernameCell.getStringCellValue();
                    // Keep only the first occurrence of each user
                    if (!uniqueUsers.containsKey(username)) {
                        uniqueUsers.put(username, inputRow);
                    }
                }
            }
        }

        // Write unique users to output sheet
        int outputRowNum = 1;
        for (Row uniqueRow : uniqueUsers.values()) {
            Row outputRow = outputSheet.createRow(outputRowNum++);
            ExcelUtils.copyRow(uniqueRow, outputRow);
        }

        ExcelUtils.autoSizeColumns(outputSheet);
        System.out.println("Removed " + (inputSheet.getLastRowNum() - uniqueUsers.size()) + " duplicate users");
    }

    private void cleanInteractionSheet(Sheet inputSheet, Workbook outputWorkbook) {
        Sheet outputSheet = outputWorkbook.createSheet(inputSheet.getSheetName());
        System.out.println("Cleaning " + inputSheet.getSheetName() + "...");

        // Copy header row
        Row headerRow = inputSheet.getRow(0);
        if (headerRow != null) {
            Row outputHeaderRow = outputSheet.createRow(0);
            ExcelUtils.copyRow(headerRow, outputHeaderRow);
        }

        // Map to store unique interactions for each source user
        // Key: sourceUserId, Value: Map of targetUserId to Row
        Map<String, Set<String>> uniqueInteractions = new HashMap<>();
        List<Row> cleanedRows = new ArrayList<>();

        // Process all rows except header
        for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
            Row inputRow = inputSheet.getRow(i);
            if (inputRow != null) {
                String sourceUserId = getValueAsString(inputRow.getCell(1)); // Source User ID
                String targetUserId = getValueAsString(inputRow.getCell(3)); // Target User ID

                if (sourceUserId != null && targetUserId != null) {
                    // Initialize set for this source user if not exists
                    uniqueInteractions.putIfAbsent(sourceUserId, new HashSet<>());

                    // If this is a new interaction for this source user, add it
                    if (uniqueInteractions.get(sourceUserId).add(targetUserId)) {
                        cleanedRows.add(inputRow);
                    }
                }
            }
        }

        // Write unique interactions to output sheet
        int outputRowNum = 1;
        for (Row uniqueRow : cleanedRows) {
            Row outputRow = outputSheet.createRow(outputRowNum++);
            ExcelUtils.copyRow(uniqueRow, outputRow);
        }

        ExcelUtils.autoSizeColumns(outputSheet);
        System.out.println("Removed " + (inputSheet.getLastRowNum() - cleanedRows.size()) +
                " duplicate interactions in " + inputSheet.getSheetName());
    }

    private String getValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long)cell.getNumericCellValue());
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        new DataCleaner().cleanData();
    }
}