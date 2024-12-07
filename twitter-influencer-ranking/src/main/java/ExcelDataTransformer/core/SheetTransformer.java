package ExcelDataTransformer.core;

import org.apache.poi.ss.usermodel.*;

public interface SheetTransformer {
    void transform(Sheet inputSheet, Workbook outputWorkbook);
}