package ExcelDataTransformer.processors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelCopy {
    public static void main(String[] args) {
        String sourceFileName = "ReCrawl4.xlsx";
        String destinationFileName = "input.xlsx";

        String currentDir = System.getProperty("user.dir");
        String sourceFilePath = currentDir + "/" + sourceFileName;
        String destinationFilePath = currentDir + "/" + destinationFileName;

        try (FileInputStream destinationFileInput = new FileInputStream(destinationFilePath);
             Workbook destinationWorkbook = new XSSFWorkbook(destinationFileInput)) {

            // Find the starting line by scanning the destination workbook
            int startLine = findStartingLine(destinationWorkbook);
            System.out.println("Found starting line: " + startLine);

            // Reopen the files for the copying process
            try (FileInputStream sourceFile = new FileInputStream(sourceFilePath);
                 Workbook sourceWorkbook = new XSSFWorkbook(sourceFile);
                 FileOutputStream destinationFileOutput = new FileOutputStream(destinationFilePath)) {

                for (int i = 0; i < sourceWorkbook.getNumberOfSheets(); i++) {
                    Sheet sourceSheet = sourceWorkbook.getSheetAt(i);
                    Sheet destinationSheet = destinationWorkbook.getSheetAt(i);

                    if (destinationSheet == null) {
                        destinationSheet = destinationWorkbook.createSheet(sourceSheet.getSheetName());
                    }

                    int destinationRowNum = startLine;
                    for (int j = 0; j <= sourceSheet.getLastRowNum(); j++) {
                        Row sourceRow = sourceSheet.getRow(j);
                        Row destinationRow = destinationSheet.getRow(destinationRowNum);

                        if (destinationRow == null) {
                            destinationRow = destinationSheet.createRow(destinationRowNum);
                        }

                        if (sourceRow != null) {
                            for (int k = 0; k < sourceRow.getLastCellNum(); k++) {
                                Cell sourceCell = sourceRow.getCell(k);
                                Cell destinationCell = destinationRow.createCell(k);

                                if (sourceCell != null) {
                                    copyCellValue(sourceCell, destinationCell);
                                }
                            }
                        }
                        destinationRowNum++;
                    }
                }

                destinationWorkbook.write(destinationFileOutput);
                System.out.println("Excel file copied successfully!");
            }
        } catch (IOException e) {
            System.err.println("Error copying Excel file: " + e.getMessage());
        }
    }

    private static int findStartingLine(Workbook workbook) {
        int maxRowToCheck = 5000; // Maximum row number to check

        Sheet sheet = workbook.getSheetAt(0); // Check first sheet

        // Start from the maximum row and go backwards
        for (int rowNum = maxRowToCheck; rowNum >= 0; rowNum--) {
            Row row = sheet.getRow(rowNum);
            if (row != null) {
                boolean hasContent = false;
                for (Cell cell : row) {
                    if (cell != null && !isCellEmpty(cell)) {
                        hasContent = true;
                        break;
                    }
                }
                if (hasContent) {
                    // Return the next row after the last non-empty row
                    return rowNum + 1;
                }
            }
        }

        // If no content is found, start from row 0
        return 0;
    }

    private static boolean isCellEmpty(Cell cell) {
        if (cell == null) return true;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim().isEmpty();
            case BLANK:
                return true;
            case NUMERIC:
                return false;
            case BOOLEAN:
                return false;
            case FORMULA:
                return false;
            default:
                return true;
        }
    }

    private static void copyCellValue(Cell sourceCell, Cell destinationCell) {
        switch (sourceCell.getCellType()) {
            case STRING:
                destinationCell.setCellValue(sourceCell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(sourceCell)) {
                    destinationCell.setCellValue(sourceCell.getDateCellValue());
                } else {
                    destinationCell.setCellValue(sourceCell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                destinationCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case FORMULA:
                destinationCell.setCellFormula(sourceCell.getCellFormula());
                break;
            case BLANK:
                destinationCell.setBlank();
                break;
            default:
                break;
        }
    }
}