package TwitRank.util;

import TwitRank.elements.KoL;
import TwitRank.elements.Node;
import TwitRank.elements.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class FileManager {

    public void createExcelFile(List<Map.Entry<Node, Double>> sortedScores, String outputFile) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Rankings");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Rank");
            headerRow.createCell(1).setCellValue("User ID");
            headerRow.createCell(2).setCellValue("Name");
            headerRow.createCell(3).setCellValue("Username");
            headerRow.createCell(4).setCellValue("Followers Count");
            headerRow.createCell(5).setCellValue("PageRank Score");

            int rowNum = 1;
            int rank = 1;
            int totalUsers = 0;

            System.out.println("Creating ranking output file...");

            for (Map.Entry<Node, Double> entry : sortedScores) {
                User user = (User) entry.getKey();
                if (user.getFollowerCount() >= KoL.getMinFollowerCount()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rank++);  // Increment rank only when creating a row
                    row.createCell(1).setCellValue(user.getId());
                    row.createCell(2).setCellValue(user.getName());
                    row.createCell(3).setCellValue(user.getUsername());
                    row.createCell(4).setCellValue(user.getFollowerCount());
                    row.createCell(5).setCellValue(entry.getValue());
                    totalUsers++;
                }
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            File file = new File(outputFile);
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                System.out.println("Ranking file created successfully with " + totalUsers + " users ranked");
            }

        } catch (IOException e) {
            System.out.println("Error creating output file: " + e.getMessage());
        }
    }
}