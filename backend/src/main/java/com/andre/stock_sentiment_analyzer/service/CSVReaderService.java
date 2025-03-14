package com.andre.stock_sentiment_analyzer.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CSVReaderService {
    private final String filePath;

    public CSVReaderService(String filePath) {
        this.filePath = filePath;
    }

    public boolean findValueFromCSV(String value, int column) {
        try (InputStream is = CSVReaderService.class.getClassLoader().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(",");
                if (record[column].compareToIgnoreCase(value) == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
