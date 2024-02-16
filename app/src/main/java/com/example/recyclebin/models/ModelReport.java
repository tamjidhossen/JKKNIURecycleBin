package com.example.recyclebin.models;

public class ModelReport {
    String adId, report, reportId, reportersName, reportersProfileImageUrl, reportersUid;

    public ModelReport() {

    }

    public ModelReport(String adId, String report, String reportId, String reportersName, String reportersProfileImageUrl, String reportersUid) {
        this.adId = adId;
        this.report = report;
        this.reportId = reportId;
        this.reportersName = reportersName;
        this.reportersProfileImageUrl = reportersProfileImageUrl;
        this.reportersUid = reportersUid;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportersName() {
        return reportersName;
    }

    public void setReportersName(String reportersName) {
        this.reportersName = reportersName;
    }

    public String getReportersProfileImageUrl() {
        return reportersProfileImageUrl;
    }

    public void setReportersProfileImageUrl(String reportersProfileImageUrl) {
        this.reportersProfileImageUrl = reportersProfileImageUrl;
    }

    public String getReportersUid() {
        return reportersUid;
    }

    public void setReportersUid(String reportersUid) {
        this.reportersUid = reportersUid;
    }
}
