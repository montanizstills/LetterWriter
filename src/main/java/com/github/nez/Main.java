package com.github.nez;

public class Main {

    static final String CREDENTIALS_JSON = "src/main/resources/secrets/adobe.json";
    static final String LOG_FILE_PATH = "logs/pdf_generation.log";

    public static void main(String[] args) throws Exception {
        GeneratePDF pdf = new GeneratePDF();

//        // Generate maintenance notices
        pdf.generateMultiplePDFsFromCSV(
                NoticeType.MAINTENANCE,
                "src/main/resources/data_files/RPM/Maintenance_Notice.csv",
                CREDENTIALS_JSON,
                "output",
                LOG_FILE_PATH,
                Boolean.FALSE
        );

//         Generate dog infraction notices
//        pdf.generateMultiplePDFsFromCSV(
//                NoticeType.LEASE_INFRACTION_DOGS,
//                "src/main/resources/document_templates/RPM/leaseinfraction_dogs.csv",
//                CREDENTIALS_JSON,
//                "output",
//                LOG_FILE_PATH,
//                Boolean.FALSE
//        );

        // Generate failed extermination notices
//        pdf.generateMultiplePDFsFromCSV(
//                NoticeType.FAILED_EXTERMINATION,
//                "src/main/resources/data_files/RPM/leaseinfraction_exterm_reinspect.csv",
//                CREDENTIALS_JSON,
//                "output",
//                LOG_FILE_PATH,
//                Boolean.FALSE
//        );

        // Generate missed extermination notices
//        pdf.generateMultiplePDFsFromCSV(
//                NoticeType.MISSED_EXTERMINATION,
//                "src/main/resources/data_files/RPM/exterm_reinpect_missed.csv",
//                CREDENTIALS_JSON,
//                "output",
//                LOG_FILE_PATH,
//                Boolean.FALSE
//                );
    }
}