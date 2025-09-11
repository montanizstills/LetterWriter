package com.github.nez;

import com.github.nez.utils.CredentialsBuilder;
import com.github.nez.utils.JSONPayload;
import com.adobe.pdfservices.operation.auth.Credentials;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class PDFGenerationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDFGenerationService.class);

    public void generatePDF(String documentPath, String jsonPath, String credentialsPath, String outputPath)
            throws Exception {

        LOGGER.info("Starting PDF generation process...");
        LOGGER.debug("Document path: {}", documentPath);
        LOGGER.debug("JSON path: {}", jsonPath);
        LOGGER.debug("Output path: {}", outputPath);

        try (InputStream inputStream = Files.newInputStream(new File(documentPath).toPath())) {

            // Create credentials
            Credentials credentials = new CredentialsBuilder()
                    .addFilePath(credentialsPath)
                    .createCredentials();

            // Setup input data for the document merge process
            JSONObject jsonDataForMerge = new JSONPayload()
                    .addJSON(jsonPath)
                    .create();

            // Build and generate PDF
            new PDFBuilder()
                    .withCredentials(credentials)
                    .withDocumentStream(inputStream)
                    .withJsonData(jsonDataForMerge)
                    .withOutputPath(outputPath)
                    .generate();

            LOGGER.info("PDF generation completed successfully");
        }
    }

    // Convenience method with default output path
    public void generatePDF(String documentPath, String jsonPath, String credentialsPath)
            throws Exception {
        generatePDF(documentPath, jsonPath, credentialsPath, "output/generatePDFOutput.pdf");
    }
}