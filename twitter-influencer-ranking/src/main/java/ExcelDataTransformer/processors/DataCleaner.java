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
    private static final String[] INTERACTION_SHEETS = {
            "User Follower", "User Following", "User Repost", "User Comment"
    };

    public void cleanData() {
        System.out.println("Starting data cleaning process...");
        try (FileInputStream fis = new FileInputStream(INPUT_FILE);
             Workbook inputWorkbook = new XSSFWorkbook(fis);
             Workbook outputWorkbook = new XSSFWorkbook()) {

            // STEP 1: Clean User sheet - remove duplicates
            System.out.println("\nSTEP 1: Removing duplicate users");
            Sheet userSheet = inputWorkbook.getSheet("User");
            Map<String, List<UserEntry>> duplicateUsers = findDuplicateUsers(userSheet);
            Map<String, String> idMappings = createIdMappings(duplicateUsers);
            Set<String> validUserIds = cleanUserSheetDuplicates(userSheet, outputWorkbook, duplicateUsers);

            // STEP 2: Clean interaction sheets - remove invalid users and update IDs
            System.out.println("\nSTEP 2: Cleaning interaction sheets");
            for (String sheetName : INTERACTION_SHEETS) {
                Sheet inputSheet = inputWorkbook.getSheet(sheetName);
                if (inputSheet != null) {
                    cleanInteractionSheet(inputSheet, outputWorkbook, idMappings, validUserIds);
                }
            }

            // STEP 3: Find connected users after cleaning interactions
            System.out.println("\nSTEP 3: Identifying connected users");
            Set<String> connectedUserIds = findConnectedUsers(outputWorkbook);
            System.out.println("Found " + connectedUserIds.size() + " connected users");

            // STEP 4: Remove isolated users
            System.out.println("\nSTEP 4: Removing isolated users");
            cleanUserSheetIsolated(outputWorkbook.getSheet("User"), connectedUserIds);

            // Save the final cleaned data
            try (FileOutputStream fos = new FileOutputStream(OUTPUT_FILE)) {
                outputWorkbook.write(fos);
                System.out.println("\nData cleaning completed successfully");
                System.out.println("Output saved to: " + OUTPUT_FILE);
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
            String primaryId = entries.get(0).userId;
            for (int i = 1; i < entries.size(); i++) {
                idMappings.put(entries.get(i).userId, primaryId);
            }
            System.out.println("Mapping IDs for user @" + entries.get(0).username +
                    ": " + entries.stream().map(e -> e.userId).collect(Collectors.joining(", ")) +
                    " -> " + primaryId);
        }

        return idMappings;
    }

    private Set<String> cleanUserSheetDuplicates(Sheet inputSheet, Workbook outputWorkbook,
                                                 Map<String, List<UserEntry>> duplicateUsers) {
        Sheet outputSheet = outputWorkbook.createSheet("User");
        System.out.println("Processing User sheet...");

        // Copy header row
        Row headerRow = inputSheet.getRow(0);
        if (headerRow != null) {
            Row outputHeaderRow = outputSheet.createRow(0);
            ExcelUtils.copyRow(headerRow, outputHeaderRow);
        }

        Map<String, Row> uniqueUsers = new LinkedHashMap<>();
        Set<String> validUserIds = new HashSet<>();

        for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
            Row inputRow = inputSheet.getRow(i);
            if (inputRow != null) {
                String username = getValueAsString(inputRow.getCell(2));
                String userId = getValueAsString(inputRow.getCell(0));

                if (username != null && userId != null && !uniqueUsers.containsKey(username)) {
                    uniqueUsers.put(username, inputRow);
                    validUserIds.add(userId);
                }
            }
        }

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
        System.out.println("Processing " + inputSheet.getSheetName() + "...");

        // Copy header row
        Row headerRow = inputSheet.getRow(0);
        if (headerRow != null) {
            Row outputHeaderRow = outputSheet.createRow(0);
            ExcelUtils.copyRow(headerRow, outputHeaderRow);
        }

        Map<String, Set<String>> uniqueInteractions = new HashMap<>();
        List<Row> cleanedRows = new ArrayList<>();
        int invalidUserCount = 0;
        int updatedIdCount = 0;

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

                // Check if both users are valid
                if (sourceUserId != null && targetUserId != null &&
                        validUserIds.contains(sourceUserId) && validUserIds.contains(targetUserId)) {
                    uniqueInteractions.putIfAbsent(sourceUserId, new HashSet<>());
                    if (uniqueInteractions.get(sourceUserId).add(targetUserId)) {
                        cleanedRows.add(inputRow);
                    }
                } else {
                    invalidUserCount++;
                }
            }
        }

        // Write cleaned rows
        int outputRowNum = 1;
        for (Row cleanedRow : cleanedRows) {
            Row outputRow = outputSheet.createRow(outputRowNum++);
            ExcelUtils.copyRow(cleanedRow, outputRow);
        }

        ExcelUtils.autoSizeColumns(outputSheet);
        System.out.println("- Updated " + updatedIdCount + " IDs");
        System.out.println("- Removed " + invalidUserCount + " rows with invalid users");
        System.out.println("- Removed " + (inputSheet.getLastRowNum() - cleanedRows.size() - invalidUserCount) +
                " duplicate interactions");
    }

    private Set<String> findConnectedUsers(Workbook workbook) {
        Set<String> connectedUsers = new HashSet<>();

        for (String sheetName : INTERACTION_SHEETS) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) continue;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String sourceUserId = getValueAsString(row.getCell(1));
                    String targetUserId = getValueAsString(row.getCell(3));

                    if (sourceUserId != null) connectedUsers.add(sourceUserId);
                    if (targetUserId != null) connectedUsers.add(targetUserId);
                }
            }
        }

        return connectedUsers;
    }

    private void cleanUserSheetIsolated(Sheet userSheet, Set<String> connectedUserIds) {
        int isolatedUsersCount = 0;
        List<Integer> rowsToRemove = new ArrayList<>();

        for (int i = 1; i <= userSheet.getLastRowNum(); i++) {
            Row row = userSheet.getRow(i);
            if (row != null) {
                String userId = getValueAsString(row.getCell(0));
                if (userId != null && !connectedUserIds.contains(userId)) {
                    rowsToRemove.add(i);
                    isolatedUsersCount++;
                }
            }
        }

        // Remove rows from bottom to top
        for (int i = rowsToRemove.size() - 1; i >= 0; i--) {
            int rowIndex = rowsToRemove.get(i);
            if (rowIndex < userSheet.getLastRowNum()) {
                userSheet.shiftRows(rowIndex + 1, userSheet.getLastRowNum(), -1);
            } else {
                Row removingRow = userSheet.getRow(rowIndex);
                if (removingRow != null) {
                    userSheet.removeRow(removingRow);
                }
            }
        }

        System.out.println("Removed " + isolatedUsersCount + " isolated users");
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