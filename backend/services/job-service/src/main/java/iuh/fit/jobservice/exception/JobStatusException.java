package iuh.fit.jobservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ném ra khi chuyển trạng thái không hợp lệ hoặc không đúng role.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JobStatusException extends RuntimeException {

    public JobStatusException(String message) {
        super(message);
    }

    public static JobStatusException invalidTransition(String from, String to) {
        return new JobStatusException(
                String.format("Cannot transition from %s to %s", from, to));
    }

    public static JobStatusException forbidden(String role, String action) {
        return new JobStatusException(
                String.format("Role %s is not allowed to %s", role, action));
    }
}
