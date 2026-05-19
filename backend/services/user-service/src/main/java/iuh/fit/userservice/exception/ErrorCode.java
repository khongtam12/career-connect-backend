package iuh.fit.userservice.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    INVALID_KEY(1001, "Invalid key"),
    USER_EXISTED(1002, "User existed"),
    USERNAME_INVALID(1003, "Username must be at least 3 characters"),
    INVALID_PASSWORD(1004, "Password must be at least 8 characters"),
    USER_NOT_EXISTED(1005, "User not existed"),
    UNAUTHENTICATED(1006, "Unauthenticate"),
    USER_EMAIL_EXISTED(1007, "Email đã tồn tại"),
    USERNAME_EXISTED(1008, "Tên đăng nhập đã tồn tại"),
    INVALID_OTP(1009, "Mã OTP không hợp lệ hoặc đã hết hạn"),
    USEREMAIL_EXISTED(1010, "Email đã tồn tại"),
    OTP_NOT_VERIFIED(1011, "Email chưa được xác thực OTP"),
    PASSWORD_INCORRECT(1012, "Mật khẩu hiện tại không chính xác"),
    INVALID_REQUEST(400, "Invalid request");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
