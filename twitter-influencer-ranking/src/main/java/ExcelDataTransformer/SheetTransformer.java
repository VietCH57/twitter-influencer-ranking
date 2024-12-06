package ExcelDataTransformer;

import org.apache.poi.ss.usermodel.*;

interface SheetTransformer {
    void transform(Sheet inputSheet, Workbook outputWorkbook);
}