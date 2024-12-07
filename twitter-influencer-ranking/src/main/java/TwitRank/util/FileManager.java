package TwitRank.util;

import TwitRank.elements.Node;
import TwitRank.elements.User;
import TwitRank.elements.KoL;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class FileManager {

    public void createExcelFile(List<Map.Entry<Node, Double>> sortedScores, String outputFile) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Rankings");
            createHeaderRow(sheet);
            int kolCount = populateScores(sheet, sortedScores);
            autoSizeColumns(sheet);
            saveWorkbook(workbook, outputFile, kolCount);
        } catch (IOException e) {
            System.out.println("Error creating output file: " + e.getMessage());
        }
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Rank", "User ID", "Name", "Username", "Followers Count", "PageRank Score"};

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    private int populateScores(Sheet sheet, List<Map.Entry<Node, Double>> sortedScores) {
        int rowNum = 1;
        int rank = 1;
        int kolCount = 0;

        for (Map.Entry<Node, Double> entry : sortedScores) {
            if (entry.getKey() instanceof User user) {
                boolean isKoL = user.getFollowerCount() >= KoL.getMinFollowerCount() &&
                        user.getReacts() >= KoL.getMinReacts() &&
                        user.getComments() >= KoL.getMinComments() &&
                        user.getReposts() >= KoL.getMinReposts();

                if (isKoL) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rank++);
                    row.createCell(1).setCellValue(user.getId());
                    row.createCell(2).setCellValue(user.getName());
                    row.createCell(3).setCellValue(user.getUsername());
                    row.createCell(4).setCellValue(user.getFollowerCount());
                    row.createCell(5).setCellValue(entry.getValue());
                    kolCount++;
                }
            }
        }
        return kolCount;
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void saveWorkbook(Workbook workbook, String outputFile, int kolCount) throws IOException {
        File file = new File(outputFile);
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
            System.out.println("Ranking file created with " + kolCount + " KoLs");
        }
    }
}