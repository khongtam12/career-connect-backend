package iuh.fit.applicationservice.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 4xx
    CANDIDATE_NOT_FOUND(404, "Candidate not found"),
    CV_NOT_FOUND(404, "CV not found"),
    JOB_NOT_FOUND(404, "Job not found"),
    APPLICATION_NOT_FOUND(404, "Application not found"),
    COMPANY_NOT_FOUND(404, "company not found"),
    INDUSTRY_NOT_FOUND(404, "industry not found"),

    JOB_CLOSED(400, "Job is closed or deadline exceeded"),
    DUPLICATE_APPLICATION(409, "You have already applied for this job"),
    INVALID_CV_OWNER(403, "CV does not belong to you"),
    CANDIDATE_BANNED(403, "Your account has been banned"),
    INVALID_FILE(400, "File is empty or invalid"),
    INVALID_STATUS(400, "Invalid status transition"),

    UNAUTHORIZED(401, "Unauthorized access"),
    INVALID_TOKEN(401, "Invalid or expired token"),

    // 5xx
    FILE_UPLOAD_FAILED(500, "Failed to upload file to S3"),
    INTERNAL_ERROR(500, "Internal server error");
    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
