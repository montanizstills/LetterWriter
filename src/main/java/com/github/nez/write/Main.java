package com.github.nez.write;

public class Main {

    static final String CREDENTIALS_JSON = "src/main/resources/secrets/adobe.json";
    static final String LOG_FILE_PATH = "logs/pdf_generation.log";

    public static void main(String[] args) throws Exception {
        GeneratePDF pdf = new GeneratePDF();

////         Generate maintenance notices
        pdf.generateMultiplePDFsFromCSV(
                NoticeType.MAINTENANCE,
                "src/main/resources/data_files/RPM/Maintenance_Notice.csv",
                CREDENTIALS_JSON,
                "output",
                LOG_FILE_PATH,
                Boolean.FALSE
        );

//        Generate Filter Notices
//        pdf.generateMultiplePDFsFromCSV(NoticeType.FILTER,
//                "src/main/resources/data_files/RPM/Filter_Notice.csv",
//                CREDENTIALS_JSON,
//                "output",
//                LOG_FILE_PATH,
//                Boolean.TRUE
//                );

////        Generate DCA notices
//        pdf.generateMultiplePDFsFromCSV(
//                NoticeType.DCA_PREINSPECT,
//                "src/main/resources/data_files/RPM/PreDCA_inspections.csv",
//                CREDENTIALS_JSON,
//                "output",
//                LOG_FILE_PATH,
//                Boolean.TRUE
//        );

//         Generate dog infraction notices
//        pdf.generateMultiplePDFsFromCSV(
//                NoticeType.LEASE_INFRACTION_DOGS,
//                "src/main/resources/document_templates/RPM/leaseinfraction_dogs.csv",
//                CREDENTIALS_JSON,
//                "output",
//                LOG_FILE_PATH,
//                Boolean.FALSE
//        );

////         Generate failed extermination notices - Single Occurrence
//        pdf.generateMultiplePDFsFromCSV(
//                NoticeType.FAILED_EXTERMINATION_SINGLE,
//                "src/main/resources/data_files/RPM/singles_one_occurrence.csv",
//                CREDENTIALS_JSON,
//                "output",
//                LOG_FILE_PATH,
//                Boolean.FALSE
//        );
////        Generate failed extermination notices - MultiOccurrence
//        pdf.generateMultiplePDFsFromCSV(
//                NoticeType.FAILED_EXTERMINATION_MULTI,
//                "src/main/resources/data_files/RPM/extermination_multiple_offences.csv",
//                CREDENTIALS_JSON,
//                "output",
//                LOG_FILE_PATH,
//                Boolean.TRUE
//        );

//         Generate missed extermination notices
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