package iuh.fit.userservice.service;

import iuh.fit.userservice.repository.InvalidatedTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TokenCleanupService {

    InvalidatedTokenRepository invalidatedTokenRepository;

    // Chạy mỗi 1 giờ (3600000 milliseconds) để dọn dẹp các token đã hết hạn
    @Scheduled(fixedDelay = 3600000)
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired tokens...");
        invalidatedTokenRepository.deleteAllExpiredSince(new Date());
        log.info("Finished cleanup of expired tokens.");
    }
}
