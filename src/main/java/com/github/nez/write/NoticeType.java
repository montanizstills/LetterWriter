package com.github.nez.write;

public enum NoticeType {
    MAINTENANCE("Maintenance_Notice_Template.docx"),
    FAILED_EXTERMINATION_SINGLE("LeaseInfraction_Failed_Extermination_Notice_Template.docx"),
    FAILED_EXTERMINATION_MULTI("LeaseInfraction_MultiDate_Template.docx"),
    MISSED_EXTERMINATION("Exterm_Reinpect_Missed_Template.docx"),
    LEASE_INFRACTION_DOGS("LeaseInfraction_Dogs.docx"),
    DCA_PREINSPECT("Pre-DCA_Inspection_Notice.docx"),
    FILTER("Filter_Notice_Template.docx");

    private final String templateFileName;

    NoticeType(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public String getTemplateFileName() {
        return this.templateFileName;
    }
}