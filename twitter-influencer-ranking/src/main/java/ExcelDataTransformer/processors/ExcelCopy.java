package ExcelDataTransformer.processors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelCopy {
    public static void main(String[] args) {
        String sourceFileName = "TestCrawlData.xlsx";
        String destinationFileName = "input.xlsx";

        String currentDir = System.getProperty("user.dir");
        String sourceFilePath = currentDir + "/" + sourceFileName;
        String destinationFilePath = currentDir + "/" + destinationFileName;

        int startLine = 1074; // Set the starting line number here

        try (FileInputStream sourceFile = new FileInputStream(sourceFilePath);
             Workbook sourceWorkbook = new XSSFWorkbook(sourceFile);
             FileInputStream destinationFileInput = new FileInputStream(destinationFilePath);
             Workbook destinationWorkbook = new XSSFWorkbook(destinationFileInput);
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

        } catch (IOException e) {
            System.err.println("Error copying Excel file: " + e.getMessage());
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