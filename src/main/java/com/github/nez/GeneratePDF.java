package com.github.nez;

import com.adobe.pdfservices.operation.auth.Credentials;
import com.github.nez.utils.CredentialsBuilder;
import com.github.nez.utils.JSONPayload;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class GeneratePDF {


    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratePDF.class);

    public void generate(String docPath, String jsonPath, String credPath, String outputPath) {
        LOGGER.info("Starting PDF generation application...");

        try {
            new PDFGenerationService().generatePDF(docPath, jsonPath, credPath, outputPath);
            LOGGER.info("Application completed successfully");
        } catch (Exception e) {
            LOGGER.error("Exception encountered while executing operation", e);
            e.printStackTrace();
        }
    }

    // batch pdf processing - existing method preserved
    public void generateMultiplePDFs(String documentPath, String jsonPath, String credentialsPath, String outputDir)
            throws Exception {

        LOGGER.info("Starting batch PDF generation process...");

        // Create credentials once (reuse for all PDFs)
        Credentials credentials = new CredentialsBuilder()
                .addFilePath(credentialsPath)
                .createCredentials();

        // Load the combined JSON
        JSONObject combinedJson = new JSONPayload()
                .addJSON(jsonPath)
                .create();

        // Extract the maintenance_notices array
        if (combinedJson.has("maintenance_notices")) {
            org.json.JSONArray noticesArray = combinedJson.getJSONArray("maintenance_notices");

            // Process each notice
            for (int i = 0; i < noticesArray.length(); i++) {
                JSONObject singleNotice = noticesArray.getJSONObject(i);

                // Generate filename based on tenant info
                String propertyCode = singleNotice.getString("PROPERTY_CODE");
                String unitNumber = singleNotice.getString("UNIT_NUMBER");
                String firstName = singleNotice.getString("TENANT_FIRST_NAME");
                String lastName = singleNotice.getString("TENANT_LAST_NAME");
                String outputPath = String.format(
                        "%s/%s_MaintenanceNotice_Unit%s_%s_%s.pdf",
                        outputDir,
                        propertyCode,
                        unitNumber,
                        firstName,
                        lastName
                );

                try (InputStream inputStream = Files.newInputStream(new File(documentPath).toPath())) {
                    new PDFBuilder()
                            .withCredentials(credentials)
                            .withDocumentStream(inputStream)
                            .withJsonData(singleNotice)  // Pass individual notice
                            .withOutputPath(outputPath)
                            .generate();

                    LOGGER.info("Generated PDF for Unit {} - {}", unitNumber, lastName);
                }
            }
        }

        LOGGER.info("Batch PDF generation completed");
    }

    // NEW: Enhanced method that uses the enum to enrich data
    public void generateMultiplePDFsWithPropertyInfo(String documentPath, String jsonPath,
                                                     String credentialsPath, String outputDir) throws Exception {

        LOGGER.info("Starting enhanced batch PDF generation process...");

        // Create credentials once (reuse for all PDFs)
        Credentials credentials = new CredentialsBuilder()
                .addFilePath(credentialsPath)
                .createCredentials();

        // Load the combined JSON
        JSONObject combinedJson = new JSONPayload()
                .addJSON(jsonPath)
                .create();

        // Extract the maintenance_notices array
        if (combinedJson.has("maintenance_notices")) {
            org.json.JSONArray noticesArray = combinedJson.getJSONArray("maintenance_notices");

            // Process each notice
            for (int i = 0; i < noticesArray.length(); i++) {
                JSONObject singleNotice = noticesArray.getJSONObject(i);

                // Enrich the notice with property information
                JSONObject enrichedNotice = enrichNoticeWithPropertyInfo(singleNotice);

                // Generate filename based on tenant info (keeping your existing format)
                String propertyCode = enrichedNotice.getString("PROPERTY_CODE");
                String unitNumber = enrichedNotice.getString("UNIT_NUMBER");
                String firstName = enrichedNotice.getString("TENANT_FIRST_NAME");
                String lastName = enrichedNotice.getString("TENANT_LAST_NAME");
                String outputPath = String.format(
                        "%s/%s_MaintenanceNotice_Unit%s_%s_%s.pdf",
                        outputDir,
                        propertyCode,
                        unitNumber,
                        firstName,
                        lastName
                );

                try (InputStream inputStream = Files.newInputStream(new File(documentPath).toPath())) {
                    new PDFBuilder()
                            .withCredentials(credentials)
                            .withDocumentStream(inputStream)
                            .withJsonData(enrichedNotice)  // Pass enriched notice
                            .withOutputPath(outputPath)
                            .generate();

                    LOGGER.info("Generated PDF for Unit {} - {} at {}", unitNumber, lastName,
                            enrichedNotice.getString("PROPERTY_NAME"));
                }
            }
        }

        LOGGER.info("Enhanced batch PDF generation completed");
    }

    // Helper method to enrich notice data with property information
    private JSONObject enrichNoticeWithPropertyInfo(JSONObject originalNotice) {
        // Create a copy of the original notice
        JSONObject enrichedNotice = new JSONObject(originalNotice.toString());

        // Get property code from the notice
        String propertyCode = originalNotice.getString("PROPERTY_CODE");

        // Find matching property info
        DevelopmentPropertyInfo propertyInfo = findPropertyByCode(propertyCode);

        if (propertyInfo != null) {
            // Add all property information to the JSON
            enrichedNotice.put("PROPERTY_NAME", propertyInfo.getPropertyName());
            enrichedNotice.put("PROPERTY_ADDRESS_STREET", propertyInfo.getAddressStreet());
            enrichedNotice.put("PROPERTY_ADDRESS_CITY", propertyInfo.getAddressCity());
            enrichedNotice.put("PROPERTY_ADDRESS_STATE", propertyInfo.getAddressState());
            enrichedNotice.put("PROPERTY_ADDRESS_ZIP", propertyInfo.getAddressZip());
            enrichedNotice.put("PROPERTY_WEBSITE", propertyInfo.getPropertyWebsite());

            // Convenience fields for templates
            enrichedNotice.put("PROPERTY_FULL_ADDRESS",
                    String.format("%s, %s, %s %s",
                            propertyInfo.getAddressStreet(),
                            propertyInfo.getAddressCity(),
                            propertyInfo.getAddressState(),
                            propertyInfo.getAddressZip()));

            LOGGER.debug("Enriched notice for property: {}", propertyInfo.getPropertyName());
        } else {
            LOGGER.warn("No property information found for code: {}", propertyCode);
        }

        return enrichedNotice;
    }

    // Helper method to find property by code
    private DevelopmentPropertyInfo findPropertyByCode(String propertyCode) {
        for (DevelopmentPropertyInfo property : DevelopmentPropertyInfo.values()) {
            if (property.getPropertyCode().equalsIgnoreCase(propertyCode)) {
                return property;
            }
        }
        return null;
    }

    // Add this method to your existing GeneratePDF class

    /**
     * Generate PDFs from CSV input with property enrichment
     */
    public void generateMultiplePDFsFromCSV(String documentPath, String csvFilePath,
                                            String credentialsPath, String outputDir) throws Exception {

        LOGGER.info("Starting CSV-based PDF generation process...");

        // Convert CSV to JSON
        com.github.nez.utils.CSVToJSONProcessor csvProcessor = new com.github.nez.utils.CSVToJSONProcessor();
        JSONObject combinedJson = csvProcessor.convertCSVToJSON(csvFilePath);

        // Optional: Save converted JSON for debugging
        // csvProcessor.saveJSONToFile(combinedJson, outputDir + "/converted_data.json");

        // Create credentials once (reuse for all PDFs)
        Credentials credentials = new CredentialsBuilder()
                .addFilePath(credentialsPath)
                .createCredentials();

        // Extract the maintenance_notices array
        if (combinedJson.has("maintenance_notices")) {
            org.json.JSONArray noticesArray = combinedJson.getJSONArray("maintenance_notices");

            // Process each notice
            for (int i = 0; i < noticesArray.length(); i++) {
                JSONObject singleNotice = noticesArray.getJSONObject(i);

                // Enrich the notice with property information
                JSONObject enrichedNotice = enrichNoticeWithPropertyInfo(singleNotice);

                // Generate filename based on tenant info
                String propertyCode = enrichedNotice.getString("PROPERTY_CODE");
                String unitNumber = enrichedNotice.getString("UNIT_NUMBER");
                String firstName = enrichedNotice.getString("TENANT_FIRST_NAME");
                String lastName = enrichedNotice.getString("TENANT_LAST_NAME");
                String outputPath = String.format(
                        "%s/%s_MaintenanceNotice_Unit%s_%s_%s.pdf",
                        outputDir,
                        propertyCode,
                        unitNumber,
                        firstName,
                        lastName
                );

                try (InputStream inputStream = Files.newInputStream(new File(documentPath).toPath())) {
                    new PDFBuilder()
                            .withCredentials(credentials)
                            .withDocumentStream(inputStream)
                            .withJsonData(enrichedNotice)
                            .withOutputPath(outputPath)
                            .generate();

                    LOGGER.info("Generated PDF for Unit {} - {} at {}", unitNumber, lastName,
                            enrichedNotice.getString("PROPERTY_NAME"));
                }
            }
        }

        LOGGER.info("CSV-based PDF generation completed");
    }
}