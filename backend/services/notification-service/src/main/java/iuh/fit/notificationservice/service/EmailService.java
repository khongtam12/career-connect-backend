package iuh.fit.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
                ? "<div style='background: #fffbeb; border-left: 4px solid #f59e0b; padding: 12px; border-radius: 8px;'><strong style='color: #92400e;'>📝 Ghi chú:</strong><p style='margin: 4px 0 0 0; color: #78350f;'>" + note + "</p></div>"
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
                """.formatted(candidateName, jobName, interviewDate, interviewTime, location, noteHtml);

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
                """.formatted(candidateName, jobName);

        sendHtmlEmail(toEmail, subject, html);
    }

    // Gui email tu choi
    public void sendRejectEmail(String toEmail, String candidateName, String jobName, String reason) throws MessagingException {
        String subject = "Thông báo kết quả ứng tuyển - " + jobName;
        String reasonHtml = (reason != null && !reason.isEmpty())
                ? "<div style='background: #fef3c7; border-left: 4px solid #f59e0b; padding: 12px; border-radius: 8px; margin: 12px 0;'><strong style='color: #92400e;'>💡 Phản hồi từ nhà tuyển dụng:</strong><p style='margin: 4px 0 0 0; color: #78350f;'>" + reason + "</p></div>"
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
                """.formatted(candidateName, jobName, reasonHtml);

        sendHtmlEmail(toEmail, subject, html);
    }

    // Gui email huy phong van
    public void sendCancelInterviewEmail(String toEmail, String candidateName, String jobName) throws MessagingException {
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
                """.formatted(candidateName, jobName);

        sendHtmlEmail(toEmail, subject, html);
    }
}
