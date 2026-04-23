package iuh.fit.jobservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Tự động cập nhật CHECK constraint `jobs_status_check` sau khi app ready.
 * Chạy sau khi Hibernate đã hoàn tất ddl-auto=update → an toàn.
 */
@Component
public class JobStatusConstraintMigration {

    private static final Logger log = LoggerFactory.getLogger(JobStatusConstraintMigration.class);

    private final JdbcTemplate jdbcTemplate;

    public JobStatusConstraintMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void migrate() {
        try {
            // Xóa constraint cũ (nếu tồn tại)
            jdbcTemplate.execute(
                "ALTER TABLE jobs DROP CONSTRAINT IF EXISTS jobs_status_check"
            );

            // Tạo lại constraint với đủ 7 giá trị
            jdbcTemplate.execute(
                "ALTER TABLE jobs ADD CONSTRAINT jobs_status_check " +
                "CHECK (status IN ('DRAFT','PENDING','ACTIVE','PAUSED','CLOSED','REJECTED','EXPIRED'))"
            );

            log.info("[Migration] jobs_status_check updated: 7 statuses allowed.");
        } catch (Exception e) {
            log.warn("[Migration] Could not update jobs_status_check: {}", e.getMessage());
        }
    }
}
