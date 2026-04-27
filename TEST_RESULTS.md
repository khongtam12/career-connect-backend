# Báo Cáo Kiểm Thử End-to-End: Tính năng Quản lý Recruiter và Phê duyệt Hồ sơ

## 1. Trạng thái Khởi động Microservices
- **Eureka Server** (cổng 8761): OK
- **API Gateway** (cổng 8080): OK
- **User-service** (cổng 8081): OK
- **Company-service** (cổng 8082): OK
- **Frontend Vite** (cổng 5173): OK

## 2. Kiểm tra API Gateway Routing
| Endpoint | Dịch vụ đích | HTTP Status | Kết quả | Ghi chú |
|----------|-------------|-------------|---------|---------|
| `GET /api/v1/admin/recruiters` | User-service | 200 OK | PASS | Trả về JSON hợp lệ, danh sách nhà tuyển dụng với trường `companyName` đã được phân giải thành tên thật thông qua Feign Client. |
| `GET /api/v1/company/pending-approvals` | Company-service | 200 OK | PASS | Trả về JSON chứa danh sách công ty chờ duyệt. |

## 3. Kiểm tra Feign Fallback (Circuit Breaker)
**Kịch bản:** Dừng đột ngột `company-service` để kiểm tra khả năng phục hồi của `user-service`.
- **Thực thi:** Gọi lại API `GET /api/v1/admin/recruiters` qua API Gateway.
- **Kết quả:**
  - HTTP Status: **200 OK** (Không bị lỗi 500).
  - Phản hồi: Trường `companyName` của tất cả Recruiter tự động chuyển thành `"N/A"`.
  - Log: `user-service` ghi nhận lỗi gọi sang `company-service` và trigger fallback `fallbackGetCompany`.
- **Đánh giá:** PASS. Circuit Breaker hoạt động chính xác.

## 4. Kiểm thử UI End-to-End (React)
1. **Trang Quản lý Recruiter:**
   - [x] Hiển thị đúng tên công ty thực tế.
   - [x] Tạo mới recruiter: Điền thông tin hợp lệ -> Toast "Thêm thành công" -> Bảng cập nhật.
   - [x] Cập nhật: Chỉnh sửa thông tin -> Toast "Cập nhật thành công" -> Bảng cập nhật.
   - [x] Đổi trạng thái: Chuyển ACTIVE/INACTIVE -> Toast xác nhận -> Bảng cập nhật.
   - [x] Xóa: Bấm xác nhận xóa -> Toast "Xóa thành công" -> Recruiter biến mất.

2. **Trang Phê duyệt Hồ sơ (Company):**
   - [x] Hiển thị danh sách công ty trạng thái PENDING.
   - [x] Duyệt hồ sơ: Nhấn Duyệt -> Toast "Đã duyệt" -> Công ty biến mất khỏi danh sách chờ.
   - [x] Từ chối hồ sơ: Nhấn Từ chối, nhập lý do -> Toast "Đã từ chối" -> Công ty biến mất khỏi danh sách chờ.

## 5. CORS & Các Vấn đề Liên Quan
- **CORS Setup:** Config CORS hiện tại của dự án trong `api-gateway/src/main/java/iuh/fit/apigateway/config/SecurityConfig.java` cho phép gọi từ origin `http://localhost:5173`. Các luồng API hoàn toàn trơn tru.
- Không ghi nhận block liên quan đến cấu hình network khi truy cập qua cổng 8080.
