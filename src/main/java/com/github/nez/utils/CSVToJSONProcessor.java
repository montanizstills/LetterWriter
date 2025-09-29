package com.github.nez.utils;

import com.github.nez.NoticeType;
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

    public JSONObject convertCSVToJSON(String csvFilePath, NoticeType noticeType) throws IOException {
        LOGGER.info("Starting CSV to JSON conversion for file: {} with notice type: {}", csvFilePath, noticeType);

        JSONObject rootObject = new JSONObject();
        JSONArray noticesArray = new JSONArray();

        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath))) {
            String[] headers = getHeadersForNoticeType(noticeType);

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(headers)
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)
                    .setIgnoreHeaderCase(true)
                    .build();

            CSVParser csvParser = csvFormat.parse(reader);

            for (CSVRecord record : csvParser) {
                JSONObject notice = processCSVRecord(record, noticeType);
                if (notice != null) {
                    noticesArray.put(notice);
                }
            }

            LOGGER.info("Successfully processed {} notices from CSV", noticesArray.length());
        }

        String arrayKey = getArrayKeyForNoticeType(noticeType);
        rootObject.put(arrayKey, noticesArray);
        return rootObject;
    }

    private String[] getHeadersForNoticeType(NoticeType noticeType) {
        switch (noticeType) {
            case MAINTENANCE:
                return new String[]{
                        "PROPERTY_CODE", "UNIT_NUMBER",
                        "TENANT_FIRST_NAME", "TENANT_LAST_NAME",
                        "NOTICE_SENT_DATE", "WORK_EXPECTED_DATE", "WORK_TO_BE_COMPLETED"
                };
            case LEASE_INFRACTION_DOGS:
                return new String[]{
                        "PROPERTY_CODE", "UNIT_NUMBER", "NOTICE_SENT_DATE",
                        "TENANT_FIRST_NAME", "TENANT_LAST_NAME"
                };
            case MISSED_EXTERMINATION:
                return new String[]{
                        "PROPERTY_CODE", "UNIT_NUMBER", "NOTICE_SENT_DATE",
                        "PREV_WORK_SCHEDULE_DATE",
                        "TENANT_FIRST_NAME", "TENANT_LAST_NAME"
                };
            case FAILED_EXTERMINATION:
                return new String[]{
                        "PROPERTY_CODE", "UNIT_NUMBER", "NOTICE_SENT_DATE",
                        "EXPECTED_WORK_DATE",
                        "TENANT_FIRST_NAME", "TENANT_LAST_NAME",
                        "FAILURE_REASONS"
                };
            default:
                throw new IllegalArgumentException("Unknown notice type: " + noticeType);
        }
    }

    private String getArrayKeyForNoticeType(NoticeType noticeType) {
        switch (noticeType) {
            case MAINTENANCE:
                return "maintenance_notices";
            case FAILED_EXTERMINATION:
                return "failed_extermination_notices";
            case MISSED_EXTERMINATION:
                return "missed_extermination_notices";
            case LEASE_INFRACTION_DOGS:
                return "lease_infraction_notices";
            default:
                throw new IllegalArgumentException("Unknown notice type: " + noticeType);
        }
    }

    private JSONObject processCSVRecord(CSVRecord record, NoticeType noticeType) {
        try {
            JSONObject notice = new JSONObject();

            switch (noticeType) {
                case MAINTENANCE:
                    processMaintenanceRecord(record, notice);
                    break;
                case LEASE_INFRACTION_DOGS:
                    processLeaseInfractionDogsRecord(record, notice);
                    break;
                case MISSED_EXTERMINATION:
                    processMissedExterminationRecord(record, notice);
                    break;
                case FAILED_EXTERMINATION:
                    processFailedExterminationRecord(record, notice);
                    break;
            }

            return notice;

        } catch (Exception e) {
            LOGGER.error("Error processing CSV record at line {}: {}", record.getRecordNumber(), e.getMessage());
            return null;
        }
    }

    private void processMaintenanceRecord(CSVRecord record, JSONObject notice) {
        notice.put("PROPERTY_CODE", getValueOrEmpty(record, "PROPERTY_CODE"));
        notice.put("UNIT_NUMBER", getValueOrEmpty(record, "UNIT_NUMBER"));
        notice.put("TENANT_FIRST_NAME", getValueOrEmpty(record, "TENANT_FIRST_NAME"));
        notice.put("TENANT_LAST_NAME", getValueOrEmpty(record, "TENANT_LAST_NAME"));
        notice.put("WORK_EXPECTED_DATE", getValueOrEmpty(record, "WORK_EXPECTED_DATE"));
        notice.put("NOTICE_SENT_DATE", getValueOrEmpty(record, "NOTICE_SENT_DATE"));

        String workItemsText = getValueOrEmpty(record, "WORK_TO_BE_COMPLETED");
        if (!workItemsText.isEmpty()) {
            JSONArray workItems = parseWorkItems(workItemsText);
            notice.put("WORK_TO_BE_COMPLETED", workItems);
        } else {
            notice.put("WORK_TO_BE_COMPLETED", new JSONArray());
        }
    }

    private void processLeaseInfractionDogsRecord(CSVRecord record, JSONObject notice) {
        notice.put("PROPERTY_CODE", getValueOrEmpty(record, "PROPERTY_CODE"));
        notice.put("UNIT_NUMBER", getValueOrEmpty(record, "UNIT_NUMBER"));
        notice.put("NOTICE_SENT_DATE", getValueOrEmpty(record, "NOTICE_SENT_DATE"));
        notice.put("TENANT_FIRST_NAME", getValueOrEmpty(record, "TENANT_FIRST_NAME"));
        notice.put("TENANT_LAST_NAME", getValueOrEmpty(record, "TENANT_LAST_NAME"));
    }

    private void processMissedExterminationRecord(CSVRecord record, JSONObject notice) {
        notice.put("PROPERTY_CODE", getValueOrEmpty(record, "PROPERTY_CODE"));
        notice.put("UNIT_NUMBER", getValueOrEmpty(record, "UNIT_NUMBER"));
        notice.put("PREV_WORK_SCHEDULE_DATE", getValueOrEmpty(record, "PREV_WORK_SCHEDULE_DATE"));
        notice.put("NOTICE_SENT_DATE", getValueOrEmpty(record, "PREV_WORK_SCHEDULE_DATE"));
        notice.put("TENANT_FIRST_NAME", getValueOrEmpty(record, "TENANT_FIRST_NAME"));
        notice.put("TENANT_LAST_NAME", getValueOrEmpty(record, "TENANT_LAST_NAME"));
    }

    private void processFailedExterminationRecord(CSVRecord record, JSONObject notice) {
        notice.put("PROPERTY_CODE", getValueOrEmpty(record, "PROPERTY_CODE"));
        notice.put("UNIT_NUMBER", getValueOrEmpty(record, "UNIT_NUMBER"));
        notice.put("NOTICE_SENT_DATE", getValueOrEmpty(record, "NOTICE_SENT_DATE"));
        notice.put("EXPECTED_WORK_DATE", getValueOrEmpty(record, "EXPECTED_WORK_DATE"));
        notice.put("TENANT_FIRST_NAME", getValueOrEmpty(record, "TENANT_FIRST_NAME"));
        notice.put("TENANT_LAST_NAME", getValueOrEmpty(record, "TENANT_LAST_NAME"));

        String failureReasons = getValueOrEmpty(record, "FAILURE_REASONS");
        if (!failureReasons.isEmpty()) {
            JSONArray reasons = parseFailureReasons(failureReasons);
            notice.put("FAILURE_REASONS", reasons);
        }
    }

    private JSONArray parseWorkItems(String workItemsText) {
        JSONArray workItems = new JSONArray();
        if (workItemsText != null && !workItemsText.trim().isEmpty()) {
            String[] items = workItemsText.split(";");
            for (String item : items) {
                String trimmedItem = item.trim();
                if (!trimmedItem.isEmpty()) {
                    JSONObject workItemObj = new JSONObject();
                    workItemObj.put("WORK_ITEM", trimmedItem);
                    workItems.put(workItemObj);
                }
            }
        }
        return workItems;
    }

    private JSONArray parseFailureReasons(String failureReasonsText) {
        JSONArray reasons = new JSONArray();
        if (failureReasonsText != null && !failureReasonsText.trim().isEmpty()) {
            String[] items = failureReasonsText.split(";");
            for (String item : items) {
                String trimmedItem = item.trim();
                if (!trimmedItem.isEmpty()) {
                    reasons.put(trimmedItem);
                }
            }
        }
        return reasons;
    }

    private String getValueOrEmpty(CSVRecord record, String columnName) {
        try {
            String value = record.get(columnName);
            return value != null ? value.trim() : "";
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Column '{}' not found in CSV record", columnName);
            return "";
        }
    }

    public void saveJSONToFile(JSONObject jsonObject, String outputPath) throws IOException {
        LOGGER.info("Saving converted JSON to: {}", outputPath);
        Files.createDirectories(Paths.get(outputPath).getParent());
        Files.write(Paths.get(outputPath), jsonObject.toString(2).getBytes());
        LOGGER.info("JSON file saved successfully");
    }
}