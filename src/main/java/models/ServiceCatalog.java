package models;

public class ServiceCatalog {
    private int serviceId;
    private String pdfUrl;

    public ServiceCatalog(int serviceId, String pdfUrl) {
        this.serviceId = serviceId;
        this.pdfUrl = pdfUrl;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
    
}