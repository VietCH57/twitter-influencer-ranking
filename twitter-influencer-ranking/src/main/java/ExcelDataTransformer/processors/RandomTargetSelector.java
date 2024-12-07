package ExcelDataTransformer.processors;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;

public class RandomTargetSelector {
    private static final String FILTERED_DATA_FILE = "filtered_data.xlsx";
    private static final String RANKING_FILE = "ranking_output.xlsx";
    private static final String OUTPUT_FILE = "random_targets.xlsx";
    private static final int TARGETS_PER_USER = 10;
    private static final String[] INTERACTION_SHEETS = {
            "User Follower", "User Following", "User Repost", "User Comment"
    };

    public void selectRandomTargets() {
        System.out.println("Starting random target selection...");
        try (FileInputStream filteredFis = new FileInputStream(FILTERED_DATA_FILE);
             FileInputStream rankingFis = new FileInputStream(RANKING_FILE);
             Workbook filteredWorkbook = new XSSFWorkbook(filteredFis);
             Workbook rankingWorkbook = new XSSFWorkbook(rankingFis)) {

            System.out.println("Reading input files:");
            System.out.println("- Filtered data: " + FILTERED_DATA_FILE);
            System.out.println("- Ranking data: " + RANKING_FILE);

            // Get ranked users
            List<RankedUser> rankedUsers = getRankedUsers(rankingWorkbook);
            System.out.println("Processing " + rankedUsers.size() + " ranked users...");

            // Collect all possible target users first
            Set<String> allPossibleTargets = new HashSet<>();
            Map<RankedUser, Set<String>> userRelations = new HashMap<>();

            for (RankedUser rankedUser : rankedUsers) {
                Set<String> relatedUsers = collectRelatedUsers(filteredWorkbook, rankedUser);
                userRelations.put(rankedUser, relatedUsers);
                allPossibleTargets.addAll(relatedUsers);
            }

            // Calculate required number of targets
            int requiredTargets = rankedUsers.size() * TARGETS_PER_USER;
            Set<String> selectedTargets = selectTargetsWithFallback(userRelations, allPossibleTargets, requiredTargets);

            // Create output file
            createOutputFile(selectedTargets);

            System.out.println("Random target selection completed. Output saved to: " + OUTPUT_FILE);
            System.out.println("Total selected targets: " + selectedTargets.size());

        } catch (IOException e) {
            System.out.println("Error during random target selection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Set<String> selectTargetsWithFallback(
            Map<RankedUser, Set<String>> userRelations,
            Set<String> allPossibleTargets,
            int requiredTargets) {

        Set<String> selectedTargets = new LinkedHashSet<>();
        Random random = new Random();
        List<String> allTargetsList = new ArrayList<>(allPossibleTargets);

        // First pass: select preferred targets from related users
        for (Set<String> relatedUsers : userRelations.values()) {
            List<String> availableUsers = new ArrayList<>(relatedUsers);
            availableUsers.removeAll(selectedTargets); // Remove already selected users

            // Select up to TARGETS_PER_USER from related users
            for (int i = 0; i < TARGETS_PER_USER && !availableUsers.isEmpty(); i++) {
                int randomIndex = random.nextInt(availableUsers.size());
                selectedTargets.add(availableUsers.get(randomIndex));
                availableUsers.remove(randomIndex);
            }
        }

        // Second pass: fill remaining slots with random users from all possible targets
        while (selectedTargets.size() < requiredTargets && !allTargetsList.isEmpty()) {
            int randomIndex = random.nextInt(allTargetsList.size());
            String candidate = allTargetsList.get(randomIndex);
            if (!selectedTargets.contains(candidate)) {
                selectedTargets.add(candidate);
            }
            allTargetsList.remove(randomIndex);
        }

        return selectedTargets;
    }

    private static class RankedUser {
        final String userId;
        final String username;
        final int rank;

        RankedUser(String userId, String username, int rank) {
            this.userId = userId;
            this.username = username;
            this.rank = rank;
        }
    }

    private List<RankedUser> getRankedUsers(Workbook rankingWorkbook) {
        List<RankedUser> rankedUsers = new ArrayList<>();
        Sheet rankingSheet = rankingWorkbook.getSheetAt(0);

        // Skip header row
        for (int i = 1; i <= rankingSheet.getLastRowNum(); i++) {
            Row row = rankingSheet.getRow(i);
            if (row != null) {
                int rank = (int) row.getCell(0).getNumericCellValue();
                String userId = getCellValueAsString(row.getCell(1));
                String username = getCellValueAsString(row.getCell(3));
                if (userId != null && username != null) {
                    rankedUsers.add(new RankedUser(userId, username, rank));
                }
            }
        }
        return rankedUsers;
    }

    private Set<String> collectRelatedUsers(Workbook filteredWorkbook, RankedUser rankedUser) {
        Set<String> relatedUsers = new HashSet<>();

        for (String sheetName : INTERACTION_SHEETS) {
            Sheet sheet = filteredWorkbook.getSheet(sheetName);
            if (sheet == null) continue;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String sourceUserId = getCellValueAsString(row.getCell(1));
                    String targetUsername = getCellValueAsString(row.getCell(2));

                    if (sourceUserId != null && targetUsername != null &&
                            sourceUserId.equals(rankedUser.userId)) {
                        relatedUsers.add(targetUsername);
                    }
                }
            }
        }
        return relatedUsers;
    }

    private void createOutputFile(Set<String> selectedTargets) throws IOException {
        Workbook outputWorkbook = new XSSFWorkbook();
        Sheet outputSheet = outputWorkbook.createSheet("Selected Targets");

        // Create header row
        Row headerRow = outputSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Username");

        // Add all selected targets
        int rowNum = 1;
        for (String username : selectedTargets) {
            Row row = outputSheet.createRow(rowNum++);
            row.createCell(0).setCellValue(username);
        }

        // Adjust column width
        outputSheet.autoSizeColumn(0);

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
        new RandomTargetSelector().selectRandomTargets();
    }
}