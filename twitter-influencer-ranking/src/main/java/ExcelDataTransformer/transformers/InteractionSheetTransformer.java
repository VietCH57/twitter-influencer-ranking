package ExcelDataTransformer.transformers;

import ExcelDataTransformer.core.BaseTransformer;
import ExcelDataTransformer.core.SheetTransformer;
import org.apache.poi.ss.usermodel.*;

public class InteractionSheetTransformer extends BaseTransformer implements SheetTransformer {
    private final String sheetName;

    public InteractionSheetTransformer(String sheetName) {
        this.sheetName = sheetName;
    }

    @Override
    public void transform(Sheet inputSheet, Workbook outputWorkbook) {
        if (inputSheet == null) {
            System.out.println("Warning: " + sheetName + " sheet not found in input file");
            return;
        }

        Sheet outputSheet = outputWorkbook.createSheet(sheetName);
        System.out.println("Processing " + sheetName + " sheet...");

        // Create header row
        Row headerRow = outputSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Source User");
        headerRow.createCell(1).setCellValue("Source User ID");
        headerRow.createCell(2).setCellValue("Target User");
        headerRow.createCell(3).setCellValue("Target User ID");

        // Get user sheet to get usernames
        Sheet userSheet = outputWorkbook.getSheet("User");
        if (userSheet == null) {
            System.out.println("Error: User sheet not found. Processing " + sheetName + " failed.");
            return;
        }

        int outputRow = 1;

        try {
            // Start from row 1 in input sheet (skip header)
            for (int rowNum = 1; rowNum < inputSheet.getLastRowNum() + 1; rowNum++) {
                Row inputRow = inputSheet.getRow(rowNum);
                if (inputRow == null) continue;

                // Get source user info from User sheet
                Row userSheetRow = userSheet.getRow(rowNum);
                if (userSheetRow == null) continue;

                String sourceUsername = getCellValueAsString(userSheetRow.getCell(2));
                Integer sourceUserId = (int)userSheetRow.getCell(0).getNumericCellValue();
                if (sourceUsername == null || sourceUsername.trim().isEmpty()) continue;

                // Process each cell in the interaction row
                for (Cell cell : inputRow) {
                    String targetUsername = getCellValueAsString(cell);
                    if (targetUsername != null && !targetUsername.trim().isEmpty()) {
                        // Find target user's ID from User sheet
                        Integer targetUserId = null;
                        for (Row userRow : userSheet) {
                            if (userRow.getRowNum() == 0) continue;
                            String currentUsername = getCellValueAsString(userRow.getCell(2));
                            if (targetUsername.equals(currentUsername)) {
                                targetUserId = (int)userRow.getCell(0).getNumericCellValue();
                                break;
                            }
                        }

                        // Create new row in output sheet
                        Row newRow = outputSheet.createRow(outputRow++);
                        newRow.createCell(0).setCellValue(sourceUsername);
                        newRow.createCell(1).setCellValue(sourceUserId);
                        newRow.createCell(2).setCellValue(targetUsername);
                        if (targetUserId != null) {
                            newRow.createCell(3).setCellValue(targetUserId);
                        }
                    }
                }
            }

            // Adjust column widths
            for (int i = 0; i < 4; i++) {
                outputSheet.autoSizeColumn(i);
            }

        } catch (Exception e) {
            System.out.println("Error processing " + sheetName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}