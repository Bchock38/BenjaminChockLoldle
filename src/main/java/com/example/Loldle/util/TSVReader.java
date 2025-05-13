package com.example.Loldle.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TSVReader {
    public static String[][] readTSV(String filePath) {
        List<String[]> rows = new ArrayList<>(); // To store rows temporarily
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split each line into values
                String[] values = line.split("\t");
                rows.add(values); // Add the row to the list
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the List to a 2D string array
        String[][] data = new String[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            data[i] = rows.get(i);
        }

        return data;
    }
}
