package com.github.nez;

import com.adobe.pdfservices.operation.PDFServices;
import com.adobe.pdfservices.operation.PDFServicesMediaType;
import com.adobe.pdfservices.operation.PDFServicesResponse;
import com.adobe.pdfservices.operation.auth.Credentials;
import com.adobe.pdfservices.operation.io.Asset;
import com.adobe.pdfservices.operation.io.StreamAsset;
import com.adobe.pdfservices.operation.pdfjobs.jobs.DocumentMergeJob;
import com.adobe.pdfservices.operation.pdfjobs.params.documentmerge.DocumentMergeParams;
import com.adobe.pdfservices.operation.pdfjobs.params.documentmerge.OutputFormat;
import com.adobe.pdfservices.operation.pdfjobs.result.DocumentMergeResult;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PDFBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDFBuilder.class);

    private Credentials credentials;
    private InputStream documentInputStream;
    private JSONObject jsonDataForMerge;
    private String outputPath = "output/generatePDFOutput.pdf";

    public PDFBuilder withCredentials(Credentials credentials) {
        this.credentials = credentials;
        return this;
    }

    public PDFBuilder withDocumentStream(InputStream inputStream) {
        this.documentInputStream = inputStream;
        return this;
    }

    public PDFBuilder withJsonData(JSONObject jsonData) {
        this.jsonDataForMerge = jsonData;
        return this;
    }

    public PDFBuilder withOutputPath(String outputPath) {
        this.outputPath = outputPath;
        return this;
    }

    public void generate() throws Exception {
        validateInputs();

        LOGGER.debug("Creating PDF Services instance...");
        // Creates a PDF Services instance
        PDFServices pdfServices = new PDFServices(credentials);

        LOGGER.debug("Uploading document asset...");
        // Creates an asset from source file and upload
        Asset asset = pdfServices.upload(documentInputStream, PDFServicesMediaType.DOCX.getMediaType());

        LOGGER.debug("Creating document merge parameters...");
        // Create parameters for the job
        DocumentMergeParams documentMergeParams = DocumentMergeParams.documentMergeParamsBuilder()
                .withJsonDataForMerge(jsonDataForMerge)
                .withOutputFormat(OutputFormat.PDF)
                .build();

        LOGGER.debug("Creating and submitting document merge job...");
        // Creates a new job instance
        DocumentMergeJob documentMergeJob = new DocumentMergeJob(asset, documentMergeParams);

        // Submit the job and gets the job result
        String location = pdfServices.submit(documentMergeJob);
        PDFServicesResponse<DocumentMergeResult> pdfServicesResponse = pdfServices.getJobResult(location, DocumentMergeResult.class);

        LOGGER.debug("Retrieving generated content...");
        // Get content from the resulting asset
        Asset resultAsset = pdfServicesResponse.getResult().getAsset();
        StreamAsset streamAsset = pdfServices.getContent(resultAsset);

        // Create output directory and save file
        saveToFile(streamAsset);

        LOGGER.info("PDF generated successfully at: {}", outputPath);
    }

    private void validateInputs() {
        if (credentials == null) {
            throw new IllegalStateException("Credentials must be provided");
        }
        if (documentInputStream == null) {
            throw new IllegalStateException("Document InputStream must be provided");
        }
        if (jsonDataForMerge == null) {
            throw new IllegalStateException("JSON data for merge must be provided");
        }
    }

    private void saveToFile(StreamAsset streamAsset) throws IOException {
        LOGGER.debug("Creating output directory and saving file...");
        Files.createDirectories(Paths.get(outputPath).getParent());
        try (OutputStream outputStream = Files.newOutputStream(new File(outputPath).toPath())) {
            IOUtils.copy(streamAsset.getInputStream(), outputStream);
        }
    }
}