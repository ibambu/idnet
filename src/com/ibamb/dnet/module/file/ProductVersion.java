package com.ibamb.dnet.module.file;

import java.util.List;

public class ProductVersion {
    private String productName;
    private String productVersion;
    private String versionFile;
    private List<HistoryVersion> historyVersions;

    public ProductVersion() {
    }

    public ProductVersion(String productName, String productVersion, String versionFile) {
        this.productName = productName;
        this.productVersion = productVersion;
        this.versionFile = versionFile;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getVersionFile() {
        return versionFile;
    }

    public void setVersionFile(String versionFile) {
        this.versionFile = versionFile;
    }

    public List<HistoryVersion> getHistoryVersions() {
        return historyVersions;
    }

    public void setHistoryVersions(List<HistoryVersion> historyVersions) {
        this.historyVersions = historyVersions;
    }

    public static class HistoryVersion {
        private String version;
        private String fileName;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
