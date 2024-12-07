package ExcelDataTransformer.processors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ExcelDataTransformer.util.ExcelUtils;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class DataCleaner {
    private static final String INPUT_FILE = "transformed_data.xlsx";
    private static final String OUTPUT_FILE = "cleaned_data.xlsx";

    public void cleanData() {
        System.out.println("Starting data cleaning process...");
        try (FileInputStream fis = new FileInputStream(INPUT_FILE);
             Workbook inputWorkbook = new XSSFWorkbook(fis)) {

            Workbook outputWorkbook = new XSSFWorkbook();

            // First, process User sheet to find duplicates and their ID mappings
            Sheet userSheet = inputWorkbook.getSheet("User");
            Map<String, List<UserEntry>> duplicateUsers = findDuplicateUsers(userSheet);
            Map<String, String> idMappings = createIdMappings(duplicateUsers);

            // Clean User sheet (remove duplicates)
            Set<String> validUserIds = cleanUserSheet(userSheet, outputWorkbook, duplicateUsers);

            // Clean and copy interaction sheets with ID updates and invalid target removal
            String[] sheetNames = {"User Follower", "User Following", "User Repost", "User Comment"};
            for (String sheetName : sheetNames) {
                Sheet inputSheet = inputWorkbook.getSheet(sheetName);
                if (inputSheet != null) {
                    cleanInteractionSheet(inputSheet, outputWorkbook, idMappings, validUserIds);
                }
            }

            // Save cleaned data
            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE)) {
                outputWorkbook.write(fos);
                System.out.println("Data cleaning completed. Output saved to: " + OUTPUT_FILE);
            }

        } catch (IOException e) {
            System.out.println("Error during data cleaning: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class UserEntry {
        final int rowIndex;
        final String userId;
        final String username;

        UserEntry(int rowIndex, String userId, String username) {
            this.rowIndex = rowIndex;
            this.userId = userId;
            this.username = username;
        }
    }

    private Map<String, List<UserEntry>> findDuplicateUsers(Sheet userSheet) {
        Map<String, List<UserEntry>> userMap = new HashMap<>();

        // Skip header row
        for (int i = 1; i <= userSheet.getLastRowNum(); i++) {
            Row row = userSheet.getRow(i);
            if (row != null) {
                String userId = getValueAsString(row.getCell(0));
                String username = getValueAsString(row.getCell(2));

                if (userId != null && username != null) {
                    userMap.computeIfAbsent(username, k -> new ArrayList<>())
                            .add(new UserEntry(i, userId, username));
                }
            }
        }

        return userMap.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, String> createIdMappings(Map<String, List<UserEntry>> duplicateUsers) {
        Map<String, String> idMappings = new HashMap<>();

        for (List<UserEntry> entries : duplicateUsers.values()) {
            String primaryId = entries.get(0).userId; // Keep the first ID as primary
            for (int i = 1; i < entries.size(); i++) {
                idMappings.put(entries.get(i).userId, primaryId);
            }
            System.out.println("Mapping IDs for user @" + entries.get(0).username +
                    ": " + entries.stream().map(e -> e.userId).collect(Collectors.joining(", ")) +
                    " -> " + primaryId);
        }

        return idMappings;
    }

    private Set<String> cleanUserSheet(Sheet inputSheet, Workbook outputWorkbook,
                                       Map<String, List<UserEntry>> duplicateUsers) {
        Sheet outputSheet = outputWorkbook.createSheet("User");
        System.out.println("Cleaning User sheet...");

        // Copy header row
        Row headerRow = inputSheet.getRow(0);
        if (headerRow != null) {
            Row outputHeaderRow = outputSheet.createRow(0);
            ExcelUtils.copyRow(headerRow, outputHeaderRow);
        }

        // Use a Map to track unique users by their username
        Map<String, Row> uniqueUsers = new LinkedHashMap<>();
        Set<String> validUserIds = new HashSet<>();

        // Process all rows except header
        for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
            Row inputRow = inputSheet.getRow(i);
            if (inputRow != null) {
                String username = getValueAsString(inputRow.getCell(2));
                String userId = getValueAsString(inputRow.getCell(0));
                if (username != null && userId != null) {
                    // Keep only the first occurrence of each user
                    if (!uniqueUsers.containsKey(username)) {
                        uniqueUsers.put(username, inputRow);
                        validUserIds.add(userId);
                    }
                }
            }
        }

        // Write unique users to output sheet
        int outputRowNum = 1;
        for (Row uniqueRow : uniqueUsers.values()) {
            Row outputRow = outputSheet.createRow(outputRowNum++);
            ExcelUtils.copyRow(uniqueRow, outputRow);
        }

        ExcelUtils.autoSizeColumns(outputSheet);
        System.out.println("Removed " + (inputSheet.getLastRowNum() - uniqueUsers.size()) + " duplicate users");

        return validUserIds;
    }

    private void cleanInteractionSheet(Sheet inputSheet, Workbook outputWorkbook,
                                       Map<String, String> idMappings, Set<String> validUserIds) {
        Sheet outputSheet = outputWorkbook.createSheet(inputSheet.getSheetName());
        System.out.println("Cleaning " + inputSheet.getSheetName() + "...");

        // Copy header row
        Row headerRow = inputSheet.getRow(0);
        if (headerRow != null) {
            Row outputHeaderRow = outputSheet.createRow(0);
            ExcelUtils.copyRow(headerRow, outputHeaderRow);
        }

        Map<String, Set<String>> uniqueInteractions = new HashMap<>();
        List<Row> cleanedRows = new ArrayList<>();
        int invalidTargetCount = 0;
        int updatedIdCount = 0;

        // Process all rows except header
        for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
            Row inputRow = inputSheet.getRow(i);
            if (inputRow != null) {
                String sourceUserId = getValueAsString(inputRow.getCell(1));
                String targetUserId = getValueAsString(inputRow.getCell(3));

                // Update IDs if they're in the mapping
                if (sourceUserId != null && idMappings.containsKey(sourceUserId)) {
                    sourceUserId = idMappings.get(sourceUserId);
                    inputRow.getCell(1).setCellValue(Double.parseDouble(sourceUserId));
                    updatedIdCount++;
                }
                if (targetUserId != null && idMappings.containsKey(targetUserId)) {
                    targetUserId = idMappings.get(targetUserId);
                    inputRow.getCell(3).setCellValue(Double.parseDouble(targetUserId));
                    updatedIdCount++;
                }

                // Check if target user exists in User sheet
                if (sourceUserId != null && targetUserId != null &&
                        validUserIds.contains(sourceUserId) && validUserIds.contains(targetUserId)) {
                    // Initialize set for this source user if not exists
                    uniqueInteractions.putIfAbsent(sourceUserId, new HashSet<>());

                    // If this is a new interaction for this source user, add it
                    if (uniqueInteractions.get(sourceUserId).add(targetUserId)) {
                        cleanedRows.add(inputRow);
                    }
                } else {
                    invalidTargetCount++;
                }
            }
        }

        // Write unique interactions to output sheet
        int outputRowNum = 1;
        for (Row uniqueRow : cleanedRows) {
            Row outputRow = outputSheet.createRow(outputRowNum++);
            ExcelUtils.copyRow(uniqueRow, outputRow);
        }

        ExcelUtils.autoSizeColumns(outputSheet);
        System.out.println("In " + inputSheet.getSheetName() + ":");
        System.out.println("- Updated " + updatedIdCount + " IDs");
        System.out.println("- Removed " + invalidTargetCount + " rows with invalid users");
        System.out.println("- Removed " + (inputSheet.getLastRowNum() - cleanedRows.size() - invalidTargetCount) +
                " duplicate interactions");
    }

    private String getValueAsString(Cell cell) {
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
        new DataCleaner().cleanData();
    }
}