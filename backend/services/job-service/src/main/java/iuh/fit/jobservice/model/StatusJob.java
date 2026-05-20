package iuh.fit.jobservice.model;

/**
 * Trạng thái vòng đời của một tin tuyển dụng.
 *
 * Luồng chuyển trạng thái:
 *   Employer : DRAFT   → PENDING
 *              ACTIVE  → PAUSED | CLOSED
 *              PAUSED  → ACTIVE
 *              REJECTED → (edit) → PENDING
 *   Admin    : PENDING → ACTIVE | REJECTED
 *   System   : ACTIVE/PENDING/PAUSED → CLOSED (khi gói tin hết hạn)
 *
 *   Deadline chỉ dùng để hiển thị "hết hạn nộp", không đổi StatusJob.
 */
public enum StatusJob {
    DRAFT,
    PENDING,
    ACTIVE,
    PAUSED,
    CLOSED,
    REJECTED,
    EXPIRED
}
