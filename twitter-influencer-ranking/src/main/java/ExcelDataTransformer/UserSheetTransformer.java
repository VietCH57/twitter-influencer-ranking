package ExcelDataTransformer;

import org.apache.poi.ss.usermodel.*;

public class UserSheetTransformer extends BaseTransformer implements SheetTransformer {
    @Override
    public void transform(Sheet inputSheet, Workbook outputWorkbook) {
        if (inputSheet == null) {
            System.out.println("Warning: User sheet not found in input file");
            return;
        }

        Sheet outputSheet = outputWorkbook.createSheet("User");
        System.out.println("Processing User sheet...");

        // Create header with ID column
        Row header = outputSheet.createRow(0);
        String[] headers = {"ID", "Name", "UserName", "Followers", "Following",
                "LinkToProfile", "LinkToTweet", "Views", "Reacts",
                "Comments", "Reposts"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        // Copy data and add ID
        int rowNum = 1;
        for (Row row : inputSheet) {
            if (row.getRowNum() == 0) continue;

            Row outputRow = outputSheet.createRow(rowNum);
            // Add ID in first column
            outputRow.createCell(0).setCellValue(rowNum);

            // Copy other data with offset of 1 column
            for (int i = 0; i < 10; i++) {
                Cell inputCell = row.getCell(i);
                if (inputCell != null) {
                    Cell outputCell = outputRow.createCell(i + 1);
                    if (i == 2 || i == 3 || i == 6 || i == 7 || i == 8 || i == 9) {
                        outputCell.setCellValue(convertShorthandValue(getCellValueAsString(inputCell)));
                    } else {
                        copyCellValue(inputCell, outputCell);
                    }
                }
            }
            rowNum++;
        }

        // Adjust column widths
        for (int i = 0; i < headers.length; i++) {
            outputSheet.autoSizeColumn(i);
        }

        System.out.println("User sheet processed successfully with " + (rowNum - 1) + " users");
    }

    @Override
    protected String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }

    private double convertShorthandValue(String shorthand) {
        if (shorthand == null || shorthand.isEmpty()) {
            return 0;
        }
        shorthand = shorthand.toUpperCase().replace(",", "");
        if (shorthand.endsWith("K")) {
            return Double.parseDouble(shorthand.substring(0, shorthand.length() - 1)) * 1_000;
        } else if (shorthand.endsWith("M")) {
            return Double.parseDouble(shorthand.substring(0, shorthand.length() - 1)) * 1_000_000;
        } else {
            return Double.parseDouble(shorthand);
        }
    }
}