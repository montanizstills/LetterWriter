package com.github.nez;

public enum NoticeType {
    MAINTENANCE("Maintenance_Notice_Template.docx"),
    FAILED_EXTERMINATION("LeaseInfraction_Failed_Extermination_Notice_Template.docx"),
    MISSED_EXTERMINATION("Exterm_Reinpect_Missed_Template.docx"),
    LEASE_INFRACTION_DOGS("LeaseInfraction_Dogs.docx");

    private final String templateFileName;

    NoticeType(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public String getTemplateFileName() {
        return templateFileName;
    }
}