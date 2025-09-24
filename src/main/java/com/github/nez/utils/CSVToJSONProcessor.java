package com.github.nez.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CSVToJSONProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVToJSONProcessor.class);

    // CSV headers that match your JSON structure
    private static final String[] EXPECTED_HEADERS = {
            "PROPERTY_CODE",
            "UNIT_NUMBER",
            "TENANT_FIRST_NAME",
            "TENANT_LAST_NAME",
            "SCHEDULED_DATE",
            "SENT_DATE",
            "WORK_TO_BE_COMPLETED"
    };

    /**
     * Converts a CSV file to JSON format matching your maintenance notices structure
     *
     * @param csvFilePath Path to the CSV file
     * @return JSONObject with maintenance_notices array
     * @throws IOException if file reading fails
     */
    public JSONObject convertCSVToJSON(String csvFilePath) throws IOException {
        LOGGER.info("Starting CSV to JSON conversion for file: {}", csvFilePath);

        JSONObject rootObject = new JSONObject();
        JSONArray maintenanceNotices = new JSONArray();

        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {

            // Configure CSV format with headers
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(EXPECTED_HEADERS)
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)
                    .build();

            CSVParser csvParser = csvFormat.parse(reader);

            for (CSVRecord record : csvParser) {
                JSONObject notice = processCSVRecord(record);
                if (notice != null) {
                    maintenanceNotices.put(notice);
                }
            }

            LOGGER.info("Successfully processed {} maintenance notices from CSV", maintenanceNotices.length());
        }

        rootObject.put("maintenance_notices", maintenanceNotices);
        return rootObject;
    }

    /**
     * Processes a single CSV record into a JSON object
     */
    private JSONObject processCSVRecord(CSVRecord record) {
        try {
            JSONObject notice = new JSONObject();

            // Map CSV columns to JSON fields
            notice.put("PROPERTY_CODE", getValueOrEmpty(record, "PROPERTY_CODE"));
            notice.put("UNIT_NUMBER", getValueOrEmpty(record, "UNIT_NUMBER"));
            notice.put("TENANT_FIRST_NAME", getValueOrEmpty(record, "TENANT_FIRST_NAME"));
            notice.put("TENANT_LAST_NAME", getValueOrEmpty(record, "TENANT_LAST_NAME"));
            notice.put("NOTICE_SENT_DATE", getValueOrEmpty(record, "NOTICE_SENT_DATE"));
            notice.put("EXPECTED_DATE", getValueOrEmpty(record, "EXPECTED_DATE"));
            notice.put("WORK_TO_BE_COMPLETED", getValueOrEmpty(record, "WORK_TO_BE_COMPLETED"));

            // Process work items (semi-colon delimited)
            String workItemsText = getValueOrEmpty(record, "WORK_TO_BE_COMPLETED");
            if (!workItemsText.isEmpty()) {
                JSONArray workItems = parseWorkItems(workItemsText);
                if (workItems.length() > 0) {
                    notice.put("WORK_TO_BE_COMPLETED", workItems);
                }
            }

            LOGGER.debug("Processed record for Unit {} - {}",
                    notice.getString("UNIT_NUMBER"),
                    notice.getString("TENANT_LAST_NAME"));

            return notice;

        } catch (Exception e) {
            LOGGER.error("Error processing CSV record at line {}: {}", record.getRecordNumber(), e.getMessage());
            return null;
        }
    }

    /**
     * Safely gets a value from CSV record, returns empty string if null/missing
     */
    private String getValueOrEmpty(CSVRecord record, String columnName) {
        try {
            String value = record.get(columnName);
            return value != null ? value.trim() : "";
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Column '{}' not found in CSV record", columnName);
            return "";
        }
    }

    /**
     * Parses semi-colon delimited work items into JSON array
     */
    private JSONArray parseWorkItems(String workItemsText) {
        JSONArray workItems = new JSONArray();

        if (workItemsText != null && !workItemsText.trim().isEmpty()) {
            String[] items = workItemsText.split(";");

            for (String item : items) {
                String trimmedItem = item.trim();
                if (!trimmedItem.isEmpty()) {
                    JSONObject workItem = new JSONObject();
                    workItem.put("WORK_ITEM", trimmedItem);
                    workItems.put(workItem);
                }
            }
        }

        return workItems;
    }

    /**
     * Saves the JSON object to a file for debugging/verification
     */
    public void saveJSONToFile(JSONObject jsonObject, String outputPath) throws IOException {
        LOGGER.info("Saving converted JSON to: {}", outputPath);

        Files.createDirectories(Paths.get(outputPath).getParent());
        Files.write(Paths.get(outputPath), jsonObject.toString(2).getBytes());

        LOGGER.info("JSON file saved successfully");
    }
}