package iuh.fit.applicationservice.dto.response;

public class FileUploadResponse {
    private String id;
    private String url;
    private String fileName;

    public FileUploadResponse() {
    }

    public FileUploadResponse(String id, String url, String fileName) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
