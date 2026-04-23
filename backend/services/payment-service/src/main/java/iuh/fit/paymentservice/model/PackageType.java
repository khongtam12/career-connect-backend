package iuh.fit.paymentservice.model;

public enum PackageType {
    BASIC_JOB_POST,      // Tin cơ bản
    URGENT_JOB_POST,     // Tuyển nhanh
    TRENDING_POST,       // Tuyển gấp / nổi bật trang chủ
    INDUSTRY_PRIORITY,   // Ưu tiên trang ngành
    AUTO_REFRESH_HOURLY, // Làm mới theo giờ
    AUTO_REFRESH_DAILY,  // Làm mới theo ngày
    EFFECT_HOT,          // Hiệu ứng HOT
    EFFECT_BOLD,         // Hiệu ứng đậm
    EFFECT_FRAME,        // Đóng khung
    POINT_SERVICE,       // Gói điểm dịch vụ
    BRANDING_LOGO        // Logo thương hiệu
}
