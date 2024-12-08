package ExcelDataTransformer.processors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;

public class DataFilter {
    private static final String INPUT_FILE = "transformed_data.xlsx";
    private static final String OUTPUT_FILE = "filtered_data.xlsx";

    public void filterRows() {
        try (FileInputStream fis = new FileInputStream(INPUT_FILE);
             Workbook inputWorkbook = new XSSFWorkbook(fis)) {

            Workbook outputWorkbook = new XSSFWorkbook();

            // Copy User sheet as is
            Sheet userSheet = inputWorkbook.getSheet("User");
            if (userSheet != null) {
                Sheet outputUserSheet = outputWorkbook.createSheet("User");
                copySheet(userSheet, outputUserSheet);
            }

            // Filter and copy interaction sheets
            String[] sheetNames = {"User Follower", "User Following", "User Repost", "User Comment"};
            for (String sheetName : sheetNames) {
                Sheet inputSheet = inputWorkbook.getSheet(sheetName);
                if (inputSheet != null) {
                    filterInteractionSheet(inputSheet, outputWorkbook);
                }
            }

            // Save filtered data
            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE)) {
                outputWorkbook.write(fos);
                System.out.println("Data filtering completed. Output saved to: " + OUTPUT_FILE);
            }

        } catch (IOException e) {
            System.out.println("Error during data filtering: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterInteractionSheet(Sheet inputSheet, Workbook outputWorkbook) {
        Sheet outputSheet = outputWorkbook.createSheet(inputSheet.getSheetName());
        System.out.println("Filtering " + inputSheet.getSheetName() + "...");

        // Copy header row
        Row headerRow = inputSheet.getRow(0);
        if (headerRow != null) {
            Row outputHeaderRow = outputSheet.createRow(0);
            copyRow(headerRow, outputHeaderRow);
        }

        // Copy only rows with target user ID empty
        int outputRowNum = 1;
        for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
            Row inputRow = inputSheet.getRow(i);
            if (inputRow != null) {
                Cell targetUserIdCell = inputRow.getCell(3); // Target User ID is in column D
                if (targetUserIdCell == null || targetUserIdCell.getCellType() != CellType.NUMERIC) {
                    Row outputRow = outputSheet.createRow(outputRowNum++);
                    copyRow(inputRow, outputRow);
                }
            }
        }

        // Adjust column widths
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            outputSheet.autoSizeColumn(i);
        }
    }

    private void copySheet(Sheet source, Sheet target) {
        for (int i = 0; i <= source.getLastRowNum(); i++) {
            Row sourceRow = source.getRow(i);
            if (sourceRow != null) {
                Row targetRow = target.createRow(i);
                copyRow(sourceRow, targetRow);
            }
        }

        // Adjust column widths
        for (int i = 0; i < source.getRow(0).getLastCellNum(); i++) {
            target.autoSizeColumn(i);
        }
    }

    private void copyRow(Row source, Row target) {
        for (int i = 0; i < source.getLastCellNum(); i++) {
            Cell sourceCell = source.getCell(i);
            if (sourceCell != null) {
                Cell targetCell = target.createCell(i);
                copyCellValue(sourceCell, targetCell);
            }
        }
    }

    private void copyCellValue(Cell source, Cell target) {
        switch (source.getCellType()) {
            case STRING:
                target.setCellValue(source.getStringCellValue());
                break;
            case NUMERIC:
                target.setCellValue(source.getNumericCellValue());
                break;
            case BOOLEAN:
                target.setCellValue(source.getBooleanCellValue());
                break;
            case FORMULA:
                target.setCellFormula(source.getCellFormula());
                break;
            default:
                target.setCellValue("");
        }
    }

    public static void main(String[] args) {
        new DataFilter().filterRows();
    }
}