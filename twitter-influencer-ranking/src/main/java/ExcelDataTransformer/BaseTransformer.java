package ExcelDataTransformer;

import org.apache.poi.ss.usermodel.*;

public abstract class BaseTransformer {
    protected String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long)cell.getNumericCellValue());
            default -> null;
        };
    }

    protected void copyCellValue(Cell source, Cell target) {
        if (source == null) return;

        switch (source.getCellType()) {
            case STRING -> target.setCellValue(source.getStringCellValue());
            case NUMERIC -> target.setCellValue(source.getNumericCellValue());
            case BOOLEAN -> target.setCellValue(source.getBooleanCellValue());
            case FORMULA -> target.setCellValue(source.getCellFormula());
            default -> target.setCellValue("");
        }
    }
}