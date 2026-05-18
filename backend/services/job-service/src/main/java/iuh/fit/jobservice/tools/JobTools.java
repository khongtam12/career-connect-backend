package iuh.fit.jobservice.tools;

import iuh.fit.jobservice.dto.response.JobResponse;
import iuh.fit.jobservice.dto.response.PageResponse;
import iuh.fit.jobservice.service.JobService;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class JobTools {

    private final JobService jobService;

    public JobTools(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Tool 1: Dành riêng cho việc TÌM KIẾM / TRA CỨU (Read-only)
     */
    @Tool(description = "Dùng để TÌM KIẾM, GỢI Ý hoặc TRA CỨU thông tin các công việc hiện đang tuyển dụng. " +
                        "Sử dụng tool này khi người dùng hỏi về: danh sách việc làm, tìm việc theo từ khóa, ngành nghề, mức lương, kinh nghiệm hoặc địa điểm. " +
                        "LƯU Ý QUAN TRỌNG: Tool này CHỈ dùng để đọc dữ liệu và đưa ra gợi ý, KHÔNG được dùng khi người dùng có ý định nộp đơn (apply) vào một công việc cụ thể.")
    public PageResponse<JobResponse> searchJobs(
            @ToolParam(description = "Từ khóa tìm kiếm (ví dụ: Java, React, Manager)") String keyword,
            @ToolParam(description = "Địa điểm làm việc (ví dụ: Ho Chi Minh, Ha Noi)") String location,
            @ToolParam(description = "Mức lương tối thiểu mong muốn") Double salaryMin
    ) {
        try {
            // Mặc định status "ACTIVE", gọi hàm search public từ JobService
            return jobService.searchJobs(
                    keyword, null, null, location, "ACTIVE", 
                    null, null, salaryMin, null, 
                    "createdAt", "desc", 1, 10
            );
        } catch (Exception e) {
            System.err.println("Error running searchJobs tool: " + e.getMessage());
            // Trả về PageResponse trống để AI tự thông báo không tìm thấy kết quả
            return new PageResponse<>();
        }
    }

    /**
     * Tool 2: Dành riêng cho việc ỨNG TUYỂN / NỘP ĐƠN (Action)
     */
    @Tool(description = "Dùng để THỰC HIỆN HÀNH ĐỘNG ỨNG TUYỂN (Apply / Nộp CV) vào một công việc cụ thể. " +
                        "CHỈ sử dụng tool này khi người dùng ĐÃ XÁC NHẬN muốn nộp đơn vào một công việc cụ thể hoặc yêu cầu bạn giúp họ ứng tuyển. " +
                        "LƯU Ý QUAN TRỌNG: Bạn bắt buộc phải hỏi và có được jobId (Mã công việc) từ các kết quả tìm kiếm trước đó thì mới được gọi tool này. Không tự bịa ra jobId.")
    public String applyToJob(
            @ToolParam(description = "ID của công việc mà người dùng muốn ứng tuyển, lấy từ kết quả của tool tìm kiếm") String jobId,
            ToolContext toolContext
    ) {
        try {
            if (toolContext == null || toolContext.getContext() == null) {
                return "Hãy từ chối yêu cầu và trả lời chính xác câu sau: 'Bạn cần đăng nhập để nộp đơn ứng tuyển vào công việc này. Vui lòng đăng nhập tại đây: [Đăng nhập](/login) [REQUIRE_LOGIN]'";
            }
            String userId = (String) toolContext.getContext().get("userId");
            if (userId == null || userId.isBlank()) {
                return "Hãy từ chối yêu cầu và trả lời chính xác câu sau: 'Bạn cần đăng nhập để nộp đơn ứng tuyển vào công việc này. Vui lòng đăng nhập tại đây: [Đăng nhập](/login) [REQUIRE_LOGIN]'";
            }
            // Trả về câu thông báo yêu cầu thực hiện hành động tiếp theo bằng thẻ Markdown link
            return "Hãy trả lời người dùng bằng câu sau: 'Vui lòng nhấn vào đường link này để tới trang nộp CV ứng tuyển: [Đi đến trang Ứng tuyển](/job/" + jobId + "?apply=true)'";
        } catch (Exception e) {
            System.err.println("Error running applyToJob tool: " + e.getMessage());
            return "Đã xảy ra lỗi khi chuẩn bị đường dẫn ứng tuyển. Vui lòng đăng nhập và thử lại hoặc bấm trực tiếp vào trang chi tiết công việc.";
        }
    }
}
