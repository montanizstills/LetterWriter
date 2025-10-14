package com.github.nez;

import com.adobe.pdfservices.operation.auth.Credentials;
import com.github.nez.utils.CSVToJSONProcessor;
import com.github.nez.utils.CredentialsBuilder;
import com.github.nez.utils.JSONPayload;
import com.github.nez.utils.LoggingConfigurer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class GeneratePDF {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratePDF.class);

    public void generate(String docPath, String jsonPath, String credPath, String outputPath, String logPath) {
        LoggingConfigurer.configureFileLogging(logPath);
        LOGGER.info("Starting PDF generation application...");

        try {
            new PDFGenerationService().generatePDF(docPath, jsonPath, credPath, outputPath);
            LOGGER.info("Application completed successfully");
        } catch (Exception e) {
            LOGGER.error("Exception encountered while executing operation", e);
            e.printStackTrace();
        }
    }

    public void generateMultiplePDFsFromJSON(String documentPath, String jsonPath, String credentialsPath,
                                             String outputDir, String logPath) throws Exception {
        LoggingConfigurer.configureFileLogging(logPath);
        LOGGER.info("Starting batch PDF generation process...");

        Credentials credentials = new CredentialsBuilder().addFilePath(credentialsPath).createCredentials();
        JSONObject combinedJson = new JSONPayload().addJSON(jsonPath).create();

        if (combinedJson.has("maintenance_notices")) {
            org.json.JSONArray noticesArray = combinedJson.getJSONArray("maintenance_notices");
            LOGGER.info("Processing {} maintenance notices", noticesArray.length());

            for (int i = 0; i < noticesArray.length(); i++) {
                JSONObject singleNotice = noticesArray.getJSONObject(i);
                String outputPath = String.format("%s/%s_MaintenanceNotice_Unit%s_%s_%s.pdf",
                        outputDir,
                        singleNotice.getString("PROPERTY_CODE"),
                        singleNotice.getString("UNIT_NUMBER"),
                        singleNotice.getString("TENANT_FIRST_NAME"),
                        singleNotice.getString("TENANT_LAST_NAME"));

                try (InputStream inputStream = Files.newInputStream(new File(documentPath).toPath())) {
                    new PDFBuilder()
                            .withCredentials(credentials)
                            .withDocumentStream(inputStream)
                            .withJsonData(singleNotice)
                            .withOutputPath(outputPath)
                            .generate();
                    LOGGER.info("Generated PDF for Unit {}", singleNotice.getString("UNIT_NUMBER"));
                }
            }
        }
        LOGGER.info("Batch PDF generation completed");
    }

    public void generateMultiplePDFsFromCSV(NoticeType noticeType, String csvFilePath,
                                            String credentialsPath, String outputDir, String logPath) throws Exception {
        generateMultiplePDFsFromCSV(noticeType, csvFilePath, credentialsPath, outputDir, logPath, Boolean.FALSE);
    }

    public void generateMultiplePDFsFromCSV(NoticeType noticeType, String csvFilePath,
                                            String credentialsPath, String outputDir,
                                            String logPath, Boolean saveJSON) throws Exception {
        LoggingConfigurer.configureFileLogging(logPath);
        LOGGER.info("Starting CSV-based PDF generation process for notice type: {}", noticeType);

        // Determine the document template path based on notice type
        String documentPath = getDocumentPath(noticeType);

        CSVToJSONProcessor csvProcessor = new CSVToJSONProcessor();
        JSONObject combinedJson = csvProcessor.convertCSVToJSON(csvFilePath, noticeType);

        if (saveJSON) {
            csvProcessor.saveJSONToFile(combinedJson, outputDir + "/converted_data.json");
        }

        Credentials credentials = new CredentialsBuilder().addFilePath(credentialsPath).createCredentials();

        String arrayKey = getNoticeArrayKey(noticeType);

        if (combinedJson.has(arrayKey)) {
            org.json.JSONArray noticesArray = combinedJson.getJSONArray(arrayKey);
            LOGGER.info("Processing {} {} from CSV", noticesArray.length(), arrayKey);

            for (int i = 0; i < noticesArray.length(); i++) {
                JSONObject singleNotice = noticesArray.getJSONObject(i);

                DevelopmentPropertyInfo propertyInfo = DevelopmentPropertyInfo.findByCode(
                        singleNotice.getString("PROPERTY_CODE"));

                JSONObject enrichedNotice = propertyInfo != null ?
                        propertyInfo.enrichNotice(singleNotice) : singleNotice;

                LOGGER.info("Full JSON: {}", enrichedNotice.toString(2));

                String outputPath = buildOutputPath(noticeType, outputDir, enrichedNotice);

                try (InputStream inputStream = Files.newInputStream(new File(documentPath).toPath())) {
                    new PDFBuilder()
                            .withCredentials(credentials)
                            .withDocumentStream(inputStream)
                            .withJsonData(enrichedNotice)
                            .withOutputPath(outputPath)
                            .generate();
                    LOGGER.info("Generated PDF for {}", enrichedNotice.getString("PROPERTY_NAME"));
                }
            }
        }
        LOGGER.info("CSV-based PDF generation completed for notice type: {}", noticeType);
    }

    private String getDocumentPath(NoticeType noticeType) {
        return String.format("src/main/resources/document_templates/RPM/%s",
                noticeType.getTemplateFileName());
    }

    private String getNoticeArrayKey(NoticeType noticeType) {
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

    private String buildOutputPath(NoticeType noticeType, String outputDir, JSONObject notice) {
        String noticeTypeString = noticeType.name().toLowerCase();
        return String.format("%s/%s_%s_%s_%s_%s.pdf",
                outputDir,
                notice.getString("PROPERTY_CODE"),
                notice.getString("UNIT_NUMBER"),
                notice.getString("TENANT_FIRST_NAME"),
                notice.getString("TENANT_LAST_NAME"),
                noticeTypeString); // todo - should say lease infraction for all notice types except maint_no.
    }
}