package com.github.nez.write;

import com.adobe.pdfservices.operation.PDFServices;
import com.adobe.pdfservices.operation.PDFServicesMediaType;
import com.adobe.pdfservices.operation.PDFServicesResponse;
import com.adobe.pdfservices.operation.auth.Credentials;
import com.adobe.pdfservices.operation.auth.ServicePrincipalCredentials;
import com.adobe.pdfservices.operation.exception.SDKException;
import com.adobe.pdfservices.operation.exception.ServiceApiException;
import com.adobe.pdfservices.operation.exception.ServiceUsageException;
import com.adobe.pdfservices.operation.io.Asset;
import com.adobe.pdfservices.operation.io.StreamAsset;
import com.adobe.pdfservices.operation.pdfjobs.jobs.CombinePDFJob;
import com.adobe.pdfservices.operation.pdfjobs.params.combinepdf.CombinePDFParams;
import com.adobe.pdfservices.operation.pdfjobs.result.CombinePDFResult;
import com.github.nez.utils.CredentialsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CombinePDF {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombinePDF.class);
    static final String CREDENTIALS_JSON = "src/main/resources/secrets/adobe.json";
    static final String LOG_FILE_PATH = "logs/pdf_generation.log";

    public static void main(String[] args) {

        try (InputStream inputStream1 = Files.newInputStream(new File("C:\\Users\\Mstills\\Desktop\\MontanizRPM\\Programs\\LetterWriter\\output\\900her_112_Jonathan_Simon_failed_extermination_single.pdf").toPath());
             InputStream inputStream2 = Files.newInputStream(new File("C:\\Users\\Mstills\\Desktop\\MontanizRPM\\Programs\\LetterWriter\\output\\concord_405_Yajomi_Guareno-Deleon_failed_extermination_single.pdf").toPath())) {

            Credentials credentials = new CredentialsBuilder().addFilePath(CREDENTIALS_JSON).createCredentials();

            // 2. Creates a PDF Services instance
            PDFServices pdfServices = new PDFServices(credentials);

            // 3. Creates asset(s) from source file(s) and uploads them
            List<StreamAsset> streamAssets = new ArrayList<>();
            streamAssets.add(new StreamAsset(inputStream1, PDFServicesMediaType.PDF.getMediaType()));
            streamAssets.add(new StreamAsset(inputStream2, PDFServicesMediaType.PDF.getMediaType()));
            List<Asset> assets = pdfServices.uploadAssets(streamAssets);

            // 4. Create parameters for the job
            CombinePDFParams combinePDFParams = CombinePDFParams.combinePDFParamsBuilder()
                    .addAsset(assets.get(0))
                    .addAsset(assets.get(1))
                    .build();

            // 5. Creates a new job instance
            CombinePDFJob combinePDFJob = new CombinePDFJob(combinePDFParams);

            // 6. Submit the job and poll for the job result
            String location = pdfServices.submit(combinePDFJob);
            PDFServicesResponse<CombinePDFResult> pdfServicesResponse =
                    pdfServices.getJobResult(location, CombinePDFResult.class);

            // 7. Get content from the resulting asset and save it
            Asset resultAsset = pdfServicesResponse.getResult().getAsset();
            StreamAsset streamAsset = pdfServices.getContent(resultAsset);

            Files.createDirectories(Paths.get("output/"));
            try (InputStream resultStream = streamAsset.getInputStream();
                 OutputStream outputStream = Files.newOutputStream(new File("output/combined_output.pdf").toPath())) {
                LOGGER.info("Saving asset at output/combined_output.pdf");
                resultStream.transferTo(outputStream);
            }

            LOGGER.info("PDFs combined successfully using Adobe SDK V4!");

        } catch (IOException | ServiceApiException | SDKException | ServiceUsageException e) {
            LOGGER.error("Exception encountered while executing operation", e);
        }
    }
}