package iuh.fit.notificationservice.service;

import iuh.fit.notificationservice.event.OrderPaymentSuccessEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    // Gui email len lich phong van
    public void sendInterviewScheduleEmail(String toEmail, String candidateName, String jobName,
            String interviewDate, String interviewTime,
            String location, String note) throws MessagingException {
        String subject = " Lịch phỏng vấn - " + jobName;
        String noteHtml = (note != null && !note.isEmpty())
                ? "<div style='background: #fffbeb; border-left: 4px solid #f59e0b; padding: 12px; border-radius: 8px;'><strong style='color: #92400e;'>📝 Ghi chú:</strong><p style='margin: 4px 0 0 0; color: #78350f;'>"
                        + note + "</p></div>"
                : "";

        String html = """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #059669, #10b981); padding: 24px; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 22px;"> Thông báo Phỏng vấn</h1>
                    </div>
                    <div style="padding: 24px;">
                        <p style="font-size: 16px; color: #374151;">Xin chào <strong>%s</strong>,</p>
                        <p style="font-size: 15px; color: #4b5563;">Chúng tôi vui mừng thông báo bạn đã được mời phỏng vấn cho vị trí:</p>
                        <div style="background: #f0fdf4; border-left: 4px solid #10b981; padding: 16px; margin: 16px 0; border-radius: 8px;">
                            <h3 style="margin: 0 0 8px 0; color: #065f46;">%s</h3>
                        </div>
                        <table style="width: 100%%; border-collapse: collapse; margin: 16px 0;">
                            <tr><td style="padding: 8px 0; color: #6b7280; width: 120px;"> Ngày:</td><td style="padding: 8px 0; font-weight: bold; color: #111827;">%s</td></tr>
                            <tr><td style="padding: 8px 0; color: #6b7280;"> Giờ:</td><td style="padding: 8px 0; font-weight: bold; color: #111827;">%s</td></tr>
                            <tr><td style="padding: 8px 0; color: #6b7280;"> Địa điểm:</td><td style="padding: 8px 0; font-weight: bold; color: #111827;">%s</td></tr>
                        </table>
                        %s
                        <p style="font-size: 14px; color: #6b7280; margin-top: 20px;">Vui lòng xác nhận tham gia và chuẩn bị đúng giờ. Chúc bạn thành công!</p>
                    </div>
                    <div style="background: #f9fafb; padding: 16px; text-align: center; font-size: 12px; color: #9ca3af;">
                        Career Connect Platform — Kết nối cơ hội nghề nghiệp
                    </div>
                </div>
                """
                .formatted(candidateName, jobName, interviewDate, interviewTime, location, noteHtml);

        sendHtmlEmail(toEmail, subject, html);
    }

    // Gui email chap nhan
    public void sendAcceptEmail(String toEmail, String candidateName, String jobName) throws MessagingException {
        String subject = " Chúc mừng! Bạn đã được chấp nhận - " + jobName;
        String html = """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #059669, #34d399); padding: 24px; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 22px;"> Chúc mừng!</h1>
                    </div>
                    <div style="padding: 24px;">
                        <p style="font-size: 16px; color: #374151;">Xin chào <strong>%s</strong>,</p>
                        <p style="font-size: 15px; color: #4b5563;">Chúng tôi vui mừng thông báo hồ sơ ứng tuyển của bạn cho vị trí dưới đây đã được <strong style="color: #059669;">CHẤP NHẬN</strong>:</p>
                        <div style="background: #f0fdf4; border-left: 4px solid #10b981; padding: 16px; margin: 16px 0; border-radius: 8px;">
                            <h3 style="margin: 0; color: #065f46;">%s</h3>
                        </div>
                        <p style="font-size: 15px; color: #4b5563;">Nhà tuyển dụng sẽ sớm liên hệ với bạn để hoàn tất các bước tiếp theo. Hãy theo dõi email thường xuyên!</p>
                        <p style="font-size: 14px; color: #6b7280; margin-top: 20px;">Chúc mừng và chúc bạn thành công trong công việc mới! 🚀</p>
                    </div>
                    <div style="background: #f9fafb; padding: 16px; text-align: center; font-size: 12px; color: #9ca3af;">
                        Career Connect Platform — Kết nối cơ hội nghề nghiệp
                    </div>
                </div>
                """
                .formatted(candidateName, jobName);

        sendHtmlEmail(toEmail, subject, html);
    }

    // Gui email tu choi
    public void sendRejectEmail(String toEmail, String candidateName, String jobName, String reason)
            throws MessagingException {
        String subject = "Thông báo kết quả ứng tuyển - " + jobName;
        String reasonHtml = (reason != null && !reason.isEmpty())
                ? "<div style='background: #fef3c7; border-left: 4px solid #f59e0b; padding: 12px; border-radius: 8px; margin: 12px 0;'><strong style='color: #92400e;'>💡 Phản hồi từ nhà tuyển dụng:</strong><p style='margin: 4px 0 0 0; color: #78350f;'>"
                        + reason + "</p></div>"
                : "";

        String html = """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #6b7280, #9ca3af); padding: 24px; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 22px;">Thông báo kết quả ứng tuyển</h1>
                    </div>
                    <div style="padding: 24px;">
                        <p style="font-size: 16px; color: #374151;">Xin chào <strong>%s</strong>,</p>
                        <p style="font-size: 15px; color: #4b5563;">Cảm ơn bạn đã quan tâm và nộp hồ sơ ứng tuyển cho vị trí:</p>
                        <div style="background: #fef2f2; border-left: 4px solid #ef4444; padding: 16px; margin: 16px 0; border-radius: 8px;">
                            <h3 style="margin: 0; color: #991b1b;">%s</h3>
                        </div>
                        <p style="font-size: 15px; color: #4b5563;">Sau khi xem xét kỹ lưỡng, chúng tôi rất tiếc phải thông báo rằng hồ sơ của bạn chưa phù hợp với yêu cầu của vị trí này trong thời điểm hiện tại.</p>
                        %s
                        <p style="font-size: 15px; color: #4b5563; margin-top: 16px;">Chúng tôi đánh giá cao sự quan tâm của bạn và khuyến khích bạn tiếp tục theo dõi các cơ hội khác trên nền tảng.</p>
                        <p style="font-size: 14px; color: #6b7280;">Chúc bạn may mắn trong hành trình nghề nghiệp! 💪</p>
                    </div>
                    <div style="background: #f9fafb; padding: 16px; text-align: center; font-size: 12px; color: #9ca3af;">
                        Career Connect Platform — Kết nối cơ hội nghề nghiệp
                    </div>
                </div>
                """
                .formatted(candidateName, jobName, reasonHtml);

        sendHtmlEmail(toEmail, subject, html);
    }

    // Gui email huy phong van
    public void sendCancelInterviewEmail(String toEmail, String candidateName, String jobName)
            throws MessagingException {
        String subject = "Thông báo hủy phỏng vấn - " + jobName;
        String html = """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #f97316, #fb923c); padding: 24px; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 22px;">Thông báo hủy phỏng vấn</h1>
                    </div>
                    <div style="padding: 24px;">
                        <p style="font-size: 16px; color: #374151;">Xin chào <strong>%s</strong>,</p>
                        <p style="font-size: 15px; color: #4b5563;">Chúng tôi xin thông báo rằng lịch phỏng vấn cho vị trí sau đã bị <strong style="color: #ea580c;">HỦY</strong>:</p>
                        <div style="background: #fff7ed; border-left: 4px solid #f97316; padding: 16px; margin: 16px 0; border-radius: 8px;">
                            <h3 style="margin: 0; color: #9a3412;">%s</h3>
                        </div>
                        <p style="font-size: 15px; color: #4b5563;">Nhà tuyển dụng có thể liên hệ lại với bạn để sắp xếp lịch khác. Vui lòng theo dõi email thường xuyên.</p>
                        <p style="font-size: 14px; color: #6b7280;">Xin lỗi vì sự bất tiện này!</p>
                    </div>
                    <div style="background: #f9fafb; padding: 16px; text-align: center; font-size: 12px; color: #9ca3af;">
                        Career Connect Platform — Kết nối cơ hội nghề nghiệp
                    </div>
                </div>
                """
                .formatted(candidateName, jobName);

        sendHtmlEmail(toEmail, subject, html);
    }

    // Gửi email xác nhận thanh toán gói dịch vụ thành công
    public void sendPaymentSuccessEmail(String toEmail, String packageName, Double amount, int durationDays)
            throws MessagingException {
        String subject = "💳 Xác nhận thanh toán thành công - " + packageName;
        String html = """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #4f46e5, #6366f1); padding: 24px; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 22px;">Thanh toán thành công</h1>
                    </div>
                    <div style="padding: 24px;">
                        <p style="font-size: 16px; color: #374151;">Xin chào,</p>
                        <p style="font-size: 15px; color: #4b5563;">Giao dịch nâng cấp gói dịch vụ của bạn đã được xử lý thành công.</p>
                        <div style="background: #f5f3ff; border-left: 4px solid #6366f1; padding: 16px; margin: 16px 0; border-radius: 8px;">
                            <table style="width: 100%%; border-collapse: collapse;">
                                <tr><td style="padding: 4px 0; color: #6b7280;">Gói dịch vụ:</td><td style="font-weight: bold; color: #111827;">%s</td></tr>
                                <tr><td style="padding: 4px 0; color: #6b7280;">Số tiền:</td><td style="font-weight: bold; color: #059669;">%,.0f VNĐ</td></tr>
                                <tr><td style="padding: 4px 0; color: #6b7280;">Thời hạn:</td><td style="font-weight: bold; color: #111827;">%d ngày</td></tr>
                            </table>
                        </div>
                        <p style="font-size: 15px; color: #4b5563;">Bạn hiện đã có thể sử dụng các tính năng ưu việt của gói dịch vụ này để tối ưu hóa việc tuyển dụng.</p>
                        <div style="text-align: center; margin-top: 24px;">
                            <a href="http://localhost:5173/employer/jobs" style="background: #4f46e5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;">Đăng tin ngay</a>
                        </div>
                        <p style="font-size: 14px; color: #6b7280; margin-top: 20px;">Cảm ơn bạn đã tin dùng Career Connect Platform!</p>
                    </div>
                    <div style="background: #f9fafb; padding: 16px; text-align: center; font-size: 12px; color: #9ca3af;">
                        Career Connect Platform — Nâng tầm thương hiệu tuyển dụng
                    </div>
                </div>
                """
                .formatted(packageName, amount, durationDays);

        sendHtmlEmail(toEmail, subject, html);
    }

    public void sendPaymentSuccessEmail(OrderPaymentSuccessEvent event) throws MessagingException {
        List<OrderPaymentSuccessEvent.ItemEvent> items = event.getItems() == null ? List.of() : event.getItems();
        String rowsHtml = items.stream()
                .map(item -> """
                        <tr>
                            <td style="padding: 10px 8px; border-bottom: 1px solid #e5e7eb; color: #111827;">%s</td>
                            <td style="padding: 10px 8px; border-bottom: 1px solid #e5e7eb; color: #374151; text-align: center;">%d</td>
                            <td style="padding: 10px 8px; border-bottom: 1px solid #e5e7eb; color: #374151; text-align: center;">%d ngày</td>
                            <td style="padding: 10px 8px; border-bottom: 1px solid #e5e7eb; color: #059669; text-align: right; font-weight: bold;">%,.0f VNĐ</td>
                        </tr>
                        """.formatted(
                        item.getPackageName(),
                        item.getQuantity() == null ? 0 : item.getQuantity(),
                        item.getDurationDays() == null ? 0 : item.getDurationDays(),
                        item.getAmount() == null ? 0D : item.getAmount()
                ))
                .reduce("", String::concat);

        String html = """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #4f46e5, #6366f1); padding: 24px; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 22px;">Thanh toán thành công</h1>
                    </div>
                    <div style="padding: 24px;">
                        <p style="font-size: 16px; color: #374151;">Xin chào,</p>
                        <p style="font-size: 15px; color: #4b5563;">Đơn hàng mua gói của bạn đã được xử lý thành công.</p>
                        <div style="background: #f5f3ff; border-left: 4px solid #6366f1; padding: 16px; margin: 16px 0; border-radius: 8px;">
                            <table style="width: 100%%; border-collapse: collapse;">
                                <tr><td style="padding: 4px 0; color: #6b7280;">Mã thanh toán:</td><td style="font-weight: bold; color: #111827;">%s</td></tr>
                                <tr><td style="padding: 4px 0; color: #6b7280;">Tổng thanh toán:</td><td style="font-weight: bold; color: #059669;">%,.0f VNĐ</td></tr>
                                <tr><td style="padding: 4px 0; color: #6b7280;">Số gói trong đơn:</td><td style="font-weight: bold; color: #111827;">%d</td></tr>
                            </table>
                        </div>
                        <table style="width: 100%%; border-collapse: collapse; margin: 16px 0; border: 1px solid #e5e7eb;">
                            <thead>
                                <tr style="background: #eef2ff;">
                                    <th style="padding: 10px 8px; text-align: left; color: #4338ca;">Gói</th>
                                    <th style="padding: 10px 8px; text-align: center; color: #4338ca;">SL</th>
                                    <th style="padding: 10px 8px; text-align: center; color: #4338ca;">Thời hạn</th>
                                    <th style="padding: 10px 8px; text-align: right; color: #4338ca;">Thành tiền</th>
                                </tr>
                            </thead>
                            <tbody>%s</tbody>
                        </table>
                        <p style="font-size: 15px; color: #4b5563;">Bạn đã có thể sử dụng các quyền lợi từ những gói này để tối ưu hóa việc tuyển dụng.</p>
                        <div style="text-align: center; margin-top: 24px;">
                            <a href="http://localhost:5173/employer/jobs" style="background: #4f46e5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;">Đăng tin ngay</a>
                        </div>
                    </div>
                    <div style="background: #f9fafb; padding: 16px; text-align: center; font-size: 12px; color: #9ca3af;">
                        Career Connect Platform
                    </div>
                </div>
                """
                .formatted(
                        event.getPaymentId(),
                        event.getTotalAmount() == null ? 0D : event.getTotalAmount(),
                        items.size(),
                        rowsHtml
                );

        sendHtmlEmail(
                event.getEmployerEmail(),
                "Xác nhận thanh toán thành công - đơn hàng " + event.getPaymentId(),
                html
        );
    }

    // Gửi email mã OTP xác thực
    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        String subject = "🔑 Mã xác thực đăng ký tài khoản - Career Connect";
        String html = """
                <div style="font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #059669, #10b981); padding: 24px; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 22px;">Xác thực tài khoản</h1>
                    </div>
                    <div style="padding: 32px; text-align: center;">
                        <p style="font-size: 16px; color: #374151; margin-bottom: 24px;">Chào bạn, mã xác thực (OTP) để hoàn tất đăng ký tài khoản của bạn là:</p>
                        <div style="background: #f0fdf4; border: 2px dashed #10b981; padding: 20px; border-radius: 8px; display: inline-block;">
                            <span style="font-size: 32px; font-weight: bold; color: #059669; letter-spacing: 8px;">%s</span>
                        </div>
                        <p style="font-size: 14px; color: #6b7280; margin-top: 24px;">Mã này có hiệu lực trong <strong>5 phút</strong>. Vui lòng không chia sẻ mã này với bất kỳ ai để bảo vệ tài khoản của bạn.</p>
                    </div>
                    <div style="background: #f9fafb; padding: 16px; text-align: center; font-size: 12px; color: #9ca3af;">
                        Career Connect Platform — Hệ sinh thái nhân sự tiên phong
                    </div>
                </div>
                """
                .formatted(otp);

        sendHtmlEmail(toEmail, subject, html);
    }
}
