package com.github.nez;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneratePDF {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratePDF.class);

    //    static final String CREDITORS_II_JSON = "G:\\My Drive\\dev\\projects\\GenerateLetter\\src\\main\\resources\\document_templates\\Creditors_II.json";
//    static final String CREDITORS_II_DOCX = "G:\\My Drive\\dev\\projects\\GenerateLetter\\src\\main\\resources\\document_templates\\Creditors_Draft_II.docx";
    static final String CREDENTIALS_JSON = "/Users/njmacstudio/Library/CloudStorage/GoogleDrive-montanizstills@gmail.com/My Drive/dev/projects/GenerateLetter/src/main/resources/secrets/adobe.json";


    static final String RPM_MAINT_NO_DOCX = "/Users/njmacstudio/Library/CloudStorage/GoogleDrive-montanizstills@gmail.com/My Drive/dev/projects/GenerateLetter/src/main/resources/document_templates/RPM/MaintenanceNotice_Patriot2_Template.docx";
    static final String RPM_MAINT_NO_JSON = "/Users/njmacstudio/Library/CloudStorage/GoogleDrive-montanizstills@gmail.com/My Drive/dev/projects/GenerateLetter/src/main/resources/document_templates/RPM/RPM_MaintenanceNotice.json";


    public static void main(String[] args) {
        LOGGER.info("Starting PDF generation application...");

        try {
            new PDFGenerationService().generatePDF(RPM_MAINT_NO_DOCX, RPM_MAINT_NO_JSON, CREDENTIALS_JSON, "output/pdf.pdf");

//            // Basic usage
//            new PDFGenerationService().generatePDF(docPath, jsonPath, credPath);
//
//            // With custom output path
//            new PDFGenerationService().generatePDF(docPath, jsonPath, credPath, "custom/output.pdf");


            LOGGER.info("Application completed successfully");

        } catch (Exception e) {
            LOGGER.error("Exception encountered while executing operation", e);
            e.printStackTrace();
        }
    }
}

