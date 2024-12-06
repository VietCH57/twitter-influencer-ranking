package ExcelDataTransformer;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class ExcelDataTransformer {
    private static final String INPUT_FILE = "input.xlsx";
    private static final String OUTPUT_FILE = "transformed_data.xlsx";
    // Sử dụng LinkedHashMap để duy trì thứ tự của các sheet
    private final LinkedHashMap<String, SheetTransformer> transformers;

    public ExcelDataTransformer() {
        transformers = new LinkedHashMap<>();
        // Thêm các transformer theo đúng thứ tự sheet
        transformers.put("User", new UserSheetTransformer());
        transformers.put("User Follower", new InteractionSheetTransformer("User Follower"));
        transformers.put("User Following", new InteractionSheetTransformer("User Following"));
        transformers.put("User Repost", new InteractionSheetTransformer("User Repost"));
        transformers.put("User Comment", new InteractionSheetTransformer("User Comment"));
    }

    public void transformData() {
        System.out.println("Starting data transformation...");
        File inputFile = new File(INPUT_FILE);

        if (!inputFile.exists()) {
            System.out.println("Error: Input file not found at: " + inputFile.getAbsolutePath());
            return;
        }

        try (FileInputStream fis = new FileInputStream(inputFile);
             Workbook inputWorkbook = new XSSFWorkbook(fis)) {

            Workbook outputWorkbook = new XSSFWorkbook();

            // Transform sheets in specified order
            for (Map.Entry<String, SheetTransformer> entry : transformers.entrySet()) {
                String sheetName = entry.getKey();
                Sheet inputSheet = inputWorkbook.getSheet(sheetName);

                if (inputSheet == null) {
                    System.out.println("Warning: Sheet '" + sheetName + "' not found in input file");
                    continue;
                }

                System.out.println("Processing sheet: " + sheetName);
                System.out.println("Number of rows in input sheet: " + inputSheet.getLastRowNum());

                entry.getValue().transform(inputSheet, outputWorkbook);

                // Verify output sheet
                Sheet outputSheet = outputWorkbook.getSheet(sheetName);
                if (outputSheet != null) {
                    System.out.println("Number of rows in output sheet: " + outputSheet.getLastRowNum());
                }
            }

            // Save output
            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE)) {
                outputWorkbook.write(fos);
                System.out.println("Transformation completed. Output file saved at: " + OUTPUT_FILE);

                // Print final sheet order
                System.out.println("\nFinal sheet order in output file:");
                for (int i = 0; i < outputWorkbook.getNumberOfSheets(); i++) {
                    System.out.println((i + 1) + ". " + outputWorkbook.getSheetName(i));
                }
            }

        } catch (IOException e) {
            System.out.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ExcelDataTransformer().transformData();
    }
}