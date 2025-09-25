package com.github.nez;

public class Main {

    static final String CREDENTIALS_JSON = "src/main/resources/secrets/adobe.json";
    static final String RPM_MAINT_NO_DOCX = "src/main/resources/document_templates/RPM/Maintenance Notice_Template.docx";
    static final String RPM_MAINT_NO_BATCH_JSON = "src/main/resources/document_templates/RPM/todoSingleFile.json";
    static final String RPM_MAINT_NO_BATCH_CSV = "src/main/resources/document_templates/RPM/RPM_Maint_No_Batch.csv";

    // Log file configuration
    static final String LOG_FILE_PATH = "logs/pdf_generation.log";

    public static void main(String[] args) throws Exception {
        GeneratePDF pdf = new GeneratePDF();

//         Option 1: Use JSON input (existing functionality)
//         pdf.generateMultiplePDFsFromJSON(
//                 RPM_MAINT_NO_DOCX,
//                 RPM_MAINT_NO_BATCH_JSON,
//                 CREDENTIALS_JSON,
//                 "output",
//                 LOG_FILE_PATH
//         );

        // Option 2: Use CSV input (new functionality) with logging
        pdf.generateMultiplePDFsFromCSV(
                RPM_MAINT_NO_DOCX,
                RPM_MAINT_NO_BATCH_CSV,
                CREDENTIALS_JSON,
                "output",
                LOG_FILE_PATH
        );
    }
}