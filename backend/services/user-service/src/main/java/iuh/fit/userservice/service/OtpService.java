package iuh.fit.userservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private static final int OTP_EXPIRE_MINUTES = 5;
    private static final int VERIFIED_EXPIRE_MINUTES = 10;

    private final Map<String, OtpData> otpCache = new ConcurrentHashMap<>();
    private final Map<String, Long> verifiedRegistrationCache = new ConcurrentHashMap<>();

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        long expireTime = System.currentTimeMillis() + (long) OTP_EXPIRE_MINUTES * 60 * 1000;

        otpCache.put(email, new OtpData(otp, expireTime));
        verifiedRegistrationCache.remove(email);
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        OtpData data = otpCache.get(email);
        if (data == null || data.isExpired() || !data.otp().equals(otp)) {
            return false;
        }

        otpCache.remove(email);
        verifiedRegistrationCache.put(
                email,
                System.currentTimeMillis() + (long) VERIFIED_EXPIRE_MINUTES * 60 * 1000
        );
        return true;
    }

    public boolean consumeVerifiedRegistration(String email) {
        Long expireTime = verifiedRegistrationCache.get(email);
        if (expireTime == null || System.currentTimeMillis() > expireTime) {
            verifiedRegistrationCache.remove(email);
            return false;
        }

        verifiedRegistrationCache.remove(email);
        return true;
    }

    private record OtpData(String otp, long expireTime) {
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
}
