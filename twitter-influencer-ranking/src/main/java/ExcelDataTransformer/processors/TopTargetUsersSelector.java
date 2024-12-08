package ExcelDataTransformer.processors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class TopTargetUsersSelector {
    private static final String INPUT_FILE = "filtered_data.xlsx";
    private static final String OUTPUT_FILE = "top_300_targets.xlsx";
    private static final int TOP_USERS_COUNT = 300;
    private static final String[] INTERACTION_SHEETS = {
            "User Follower", "User Following", "User Repost", "User Comment"
    };

    public void selectTopTargetUsers() {
        System.out.println("Starting top target users selection...");
        try (FileInputStream fis = new FileInputStream(INPUT_FILE);
             Workbook inputWorkbook = new XSSFWorkbook(fis)) {

            // Map to store username and their frequency count
            Map<String, Integer> userFrequencyMap = new HashMap<>();

            // Process each interaction sheet
            for (String sheetName : INTERACTION_SHEETS) {
                processInteractionSheet(inputWorkbook.getSheet(sheetName), userFrequencyMap);
            }

            // Sort users by frequency and get top 300
            List<Map.Entry<String, Integer>> sortedUsers = sortByFrequency(userFrequencyMap);
            List<Map.Entry<String, Integer>> topUsers = getTopUsers(sortedUsers);

            // Create output file
            createOutputFile(topUsers);

            System.out.println("Top target users selection completed. Output saved to: " + OUTPUT_FILE);
            System.out.println("Total selected users: " + topUsers.size());

        } catch (IOException e) {
            System.out.println("Error during top target users selection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processInteractionSheet(Sheet sheet, Map<String, Integer> userFrequencyMap) {
        if (sheet == null) return;

        System.out.println("Processing " + sheet.getSheetName() + "...");

        // Skip header row
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            // Get target username from column 2 (Target User)
            Cell targetUserCell = row.getCell(2);
            if (targetUserCell != null) {
                String targetUsername = getCellValueAsString(targetUserCell);
                if (targetUsername != null && !targetUsername.trim().isEmpty()) {
                    // Increment frequency count
                    userFrequencyMap.merge(targetUsername, 1, Integer::sum);
                }
            }
        }
    }

    private List<Map.Entry<String, Integer>> sortByFrequency(Map<String, Integer> userFrequencyMap) {
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(userFrequencyMap.entrySet());

        // Sort by frequency (descending) and then by username (ascending) for ties
        sortedEntries.sort((e1, e2) -> {
            int freqCompare = e2.getValue().compareTo(e1.getValue());
            return freqCompare != 0 ? freqCompare : e1.getKey().compareTo(e2.getKey());
        });

        return sortedEntries;
    }

    private List<Map.Entry<String, Integer>> getTopUsers(List<Map.Entry<String, Integer>> sortedUsers) {
        List<Map.Entry<String, Integer>> topUsers = new ArrayList<>();
        int count = Math.min(TOP_USERS_COUNT, sortedUsers.size());

        for (int i = 0; i < count; i++) {
            topUsers.add(sortedUsers.get(i));
        }

        return topUsers;
    }

    private void createOutputFile(List<Map.Entry<String, Integer>> topUsers) throws IOException {
        Workbook outputWorkbook = new XSSFWorkbook();
        Sheet outputSheet = outputWorkbook.createSheet("Top Target Users");

        // Create header row
        Row headerRow = outputSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Rank");
        headerRow.createCell(1).setCellValue("Username");
        headerRow.createCell(2).setCellValue("Frequency");

        // Create cell style for numbers
        CellStyle numberStyle = outputWorkbook.createCellStyle();
        numberStyle.setDataFormat(outputWorkbook.createDataFormat().getFormat("#,##0"));

        // Add users with their ranks and frequencies
        for (int i = 0; i < topUsers.size(); i++) {
            Row row = outputSheet.createRow(i + 1);
            Map.Entry<String, Integer> entry = topUsers.get(i);

            // Rank
            Cell rankCell = row.createCell(0);
            rankCell.setCellValue(i + 1);

            // Username
            row.createCell(1).setCellValue(entry.getKey());

            // Frequency with number formatting
            Cell freqCell = row.createCell(2);
            freqCell.setCellValue(entry.getValue());
            freqCell.setCellStyle(numberStyle);
        }

        // Adjust column widths
        outputSheet.autoSizeColumn(0);
        outputSheet.autoSizeColumn(1);
        outputSheet.autoSizeColumn(2);

        // Save the file
        try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE)) {
            outputWorkbook.write(fos);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long)cell.getNumericCellValue());
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        new TopTargetUsersSelector().selectTopTargetUsers();
    }
}