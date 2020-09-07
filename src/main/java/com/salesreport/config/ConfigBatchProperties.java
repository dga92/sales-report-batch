package com.salesreport.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.batch.jobs")
public class ConfigBatchProperties {

    private JobProperties salesReport = new JobProperties();

    public JobProperties getSalesReport() {
        return salesReport;
    }

    public void setSalesReport(JobProperties salesReport) {
        this.salesReport = salesReport;
    }

    public static class JobProperties {

        private Boolean enabled;
        private String name;
        private String description;
        private String fileType;
        private Long chunkSize;

        public Long getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(Long chunkSize) {
            this.chunkSize = chunkSize;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

    }
}
