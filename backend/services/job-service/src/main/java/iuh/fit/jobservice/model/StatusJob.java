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
 *   System   : ACTIVE  → EXPIRED (khi hết deadline)
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
