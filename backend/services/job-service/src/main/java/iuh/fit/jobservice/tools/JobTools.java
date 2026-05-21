package iuh.fit.jobservice.tools;

import iuh.fit.jobservice.dto.response.JobResponse;
import iuh.fit.jobservice.dto.response.JobCardResponse;
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
    public PageResponse<JobCardResponse> searchJobs(
            @ToolParam(description = "Từ khóa tìm kiếm (ví dụ: Java, React, Manager)") String keyword,
            @ToolParam(description = "Địa điểm làm việc. Bạn (AI) có trách nhiệm sử dụng kiến thức địa lý để tự phân tích câu nói của người dùng (ví dụ: từ các quận/huyện như Quận 1, Cầu Giấy; từ các thành phố trực thuộc như Nha Trang, Đà Lạt, Buôn Ma Thuột; từ các tên gọi khác/viết tắt/không dấu như Sài Gòn, tp hcm, hn, da nang, hue...) và TỰ ĐỘNG CHUYỂN ĐỔI / CHỌN lựa chính xác tên tỉnh/thành tương ứng từ danh sách hỗ trợ dưới đây để điền vào tham số này. Nếu người dùng không chỉ định địa điểm, hãy để trống.\n" +
                                     "Danh sách các tỉnh/thành được hỗ trợ (điền chính xác một trong các chuỗi sau):\n" +
                                     "- 'Ha Noi' (Hà Nội, Thủ đô)\n" +
                                     "- 'HCM' (TP. Hồ Chí Minh, Sài Gòn, các quận/huyện thuộc HCM)\n" +
                                     "- 'Da Nang' (Đà Nẵng)\n" +
                                     "- 'Hue' (Huế, Thừa Thiên Huế)\n" +
                                     "- 'Hai Phong' (Hải Phòng)\n" +
                                     "- 'Can Tho' (Cần Thơ)\n" +
                                     "- 'Khanh Hoa' (Khánh Hòa, Nha Trang)\n" +
                                     "- 'Lam Dong' (Lâm Đồng, Đà Lạt)\n" +
                                     "- 'Dak Lak' (Đắk Lắk, Đắc Lắc, Buôn Ma Thuột)\n" +
                                     "- 'Tuyen Quang', 'Lao Cai', 'Thai Nguyen', 'Phu Tho', 'Bac Ninh', 'Hung Yen', 'Ninh Binh', 'Quang Tri', 'Quang Ngai', 'Gia Lai', 'Dong Nai', 'Tay Ninh', 'Vinh Long', 'Dong Thap', 'Ca Mau', 'An Giang', 'Lai Chau', 'Dien Bien', 'Son La', 'Lang Son', 'Quang Ninh', 'Thanh Hoa', 'Nghe An', 'Ha Tinh', 'Cao Bang'") String location,
            @ToolParam(description = "Mức lương tối thiểu mong muốn. LƯU Ý QUAN TRỌNG: Truyền CHÍNH XÁC con số thô người dùng nhập/yêu cầu (ví dụ: người dùng nói '1000' thì điền 1000.0, nói '1500' thì điền 1500.0, nói '15000000' thì điền 15000000.0). Tuyệt đối KHÔNG được nhân lên, chia nhỏ, quy đổi hay biên dịch ý nghĩa (ví dụ: người dùng nói '1000' thì CẤM tự ý hiểu là '1 triệu' và điền 1000000.0, hãy điền đúng 1000.0).") Double salaryMin
    ) {
        try {
            String normalizedLocation = LocationNormalizer.normalizeLocation(location);
            // Mặc định status "ACTIVE", gọi hàm search public từ JobService
            return jobService.searchJobs(
                    keyword, null, null, null, null,
                    normalizedLocation, "ACTIVE",
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
                        "LƯU Ý QUAN TRỌNG: Bạn (AI) phải tự trích xuất jobId tương ứng từ kết quả của các công cụ tìm kiếm trước đó trong lịch sử trò chuyện để điền vào. Tuyệt đối KHÔNG được hỏi người dùng cung cấp jobId hoặc mã công việc, vì người dùng không thể biết mã này.")
    public String applyToJob(
            @ToolParam(description = "ID của công việc mà người dùng muốn ứng tuyển, tự động trích xuất từ kết quả tìm kiếm trước đó trong lịch sử cuộc trò chuyện (không bao giờ được hỏi người dùng cung cấp mã này)") String jobId,
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
