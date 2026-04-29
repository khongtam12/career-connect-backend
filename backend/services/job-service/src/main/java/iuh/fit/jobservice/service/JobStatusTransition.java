package iuh.fit.jobservice.service;

import iuh.fit.jobservice.exception.JobStatusException;
import iuh.fit.jobservice.model.StatusJob;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Engine kiểm tra và thực hiện chuyển trạng thái tin tuyển dụng.
 *
 * <pre>
 * EMPLOYER transitions:
 *   DRAFT    → PENDING
 *   ACTIVE   → PAUSED, CLOSED
 *   PAUSED   → ACTIVE
 *   REJECTED → PENDING   (sau khi chỉnh sửa)
 *
 * ADMIN transitions:
 *   PENDING  → ACTIVE
 *   PENDING  → REJECTED
 *
 * SYSTEM transitions:
 *   ACTIVE   → EXPIRED   (tự động khi hết deadline)
 * </pre>
 */
public class JobStatusTransition {

    public enum Role {
        EMPLOYER, ADMIN, SYSTEM
    }

    // Các chuyển trạng thái hợp lệ theo từng role
    private static final Map<Role, Map<StatusJob, Set<StatusJob>>> ALLOWED;

    static {
        ALLOWED = new EnumMap<>(Role.class);

        // ── EMPLOYER ──
        Map<StatusJob, Set<StatusJob>> employer = new EnumMap<>(StatusJob.class);
        employer.put(StatusJob.DRAFT, EnumSet.of(StatusJob.PENDING));
        employer.put(StatusJob.ACTIVE, EnumSet.of(StatusJob.PAUSED, StatusJob.CLOSED));
        employer.put(StatusJob.PAUSED, EnumSet.of(StatusJob.ACTIVE));
        employer.put(StatusJob.REJECTED, EnumSet.of(StatusJob.PENDING));
        ALLOWED.put(Role.EMPLOYER, employer);

        // ── ADMIN ──
        Map<StatusJob, Set<StatusJob>> admin = new EnumMap<>(StatusJob.class);
        admin.put(StatusJob.PENDING, EnumSet.of(StatusJob.ACTIVE, StatusJob.REJECTED));
        admin.put(StatusJob.ACTIVE, EnumSet.of(StatusJob.REJECTED, StatusJob.CLOSED));
        admin.put(StatusJob.CLOSED, EnumSet.of(StatusJob.ACTIVE));
        ALLOWED.put(Role.ADMIN, admin);

        // ── SYSTEM ──
        Map<StatusJob, Set<StatusJob>> system = new EnumMap<>(StatusJob.class);
        system.put(StatusJob.ACTIVE, EnumSet.of(StatusJob.EXPIRED));
        ALLOWED.put(Role.SYSTEM, system);
    }

    /**
     * Validate và trả về trạng thái mới nếu hợp lệ.
     *
     * @param role      role thực hiện hành động
     * @param current   trạng thái hiện tại của job
     * @param requested trạng thái muốn chuyển sang
     * @return trạng thái mới (chính là {@code requested} nếu hợp lệ)
     * @throws JobStatusException nếu chuyển trạng thái không được phép
     */
    public static StatusJob transition(Role role, StatusJob current, StatusJob requested) {
        Map<StatusJob, Set<StatusJob>> roleMap = ALLOWED.get(role);
        if (roleMap == null) {
            throw JobStatusException.forbidden(role.name(), "change job status");
        }

        Set<StatusJob> allowed = roleMap.get(current);
        if (allowed == null || !allowed.contains(requested)) {
            throw JobStatusException.invalidTransition(current.name(), requested.name());
        }

        return requested;
    }

    /**
     * Kiểm tra xem role có được phép chuyển trạng thái không (không ném exception).
     */
    public static boolean canTransition(Role role, StatusJob current, StatusJob requested) {
        Map<StatusJob, Set<StatusJob>> roleMap = ALLOWED.get(role);
        if (roleMap == null)
            return false;
        Set<StatusJob> allowed = roleMap.get(current);
        return allowed != null && allowed.contains(requested);
    }
}
