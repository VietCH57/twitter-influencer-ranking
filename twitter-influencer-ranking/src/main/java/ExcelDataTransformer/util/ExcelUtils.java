package ExcelDataTransformer.util;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtils {
    public static void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public static void copyCellValue(Cell source, Cell target) {
        if (source == null) return;

        switch (source.getCellType()) {
            case STRING -> target.setCellValue(source.getStringCellValue());
            case NUMERIC -> target.setCellValue(source.getNumericCellValue());
            case BOOLEAN -> target.setCellValue(source.getBooleanCellValue());
            case FORMULA -> target.setCellValue(source.getCellFormula());
            default -> target.setCellValue("");
        }
    }

    public static void copyRow(Row source, Row target) {
        if (source == null) return;

        for (int i = 0; i < source.getLastCellNum(); i++) {
            Cell sourceCell = source.getCell(i);
            if (sourceCell != null) {
                Cell targetCell = target.createCell(i);
                copyCellValue(sourceCell, targetCell);
            }
        }
    }
}