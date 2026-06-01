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
            "Sử dụng tool này khi người dùng hỏi về: danh sách việc làm, tìm việc theo từ khóa, ngành nghề, mức lương, kinh nghiệm hoặc địa điểm. "
            +
            "LƯU Ý QUAN TRỌNG: Tool này CHỈ dùng để đọc dữ liệu và đưa ra gợi ý, KHÔNG được dùng khi người dùng có ý định nộp đơn (apply) vào một công việc cụ thể.")
    public PageResponse<JobCardResponse> searchJobs(
            @ToolParam(description = "Từ khóa tìm kiếm (ví dụ: Java, React, Manager)") String keyword,
            @ToolParam(description = "Ngành nghề (industry) của công việc. Bạn (AI) có trách nhiệm phân tích câu nói của người dùng " +
                    "và TỰ ĐỘNG CHỌN đúng mã ngành (industryId) từ danh sách dưới đây để điền vào tham số này. " +
                    "Nếu người dùng không chỉ định ngành nghề, hãy để trống (null).\n" +
                    "QUY TẮC NHẬN DIỆN: Người dùng có thể dùng từ đồng nghĩa, viết tắt, tiếng lóng, không dấu, tiếng Anh hoặc mô tả gián tiếp. " +
                    "Bạn phải tự suy luận ngành nghề phù hợp nhất từ ngữ cảnh câu nói.\n" +
                    "Danh sách các ngành nghề được hỗ trợ (điền chính xác mã industryId tương ứng):\n" +
                    "- 'IND001' = Công nghệ thông tin — Từ khóa nhận diện: IT, CNTT, phần mềm, lập trình, code, coder, coding, dev, developer, " +
                    "software, hardware, backend, frontend, fullstack, full-stack, web developer, mobile, app, ứng dụng, " +
                    "AI, trí tuệ nhân tạo, machine learning, ML, deep learning, data science, data engineer, big data, " +
                    "DevOps, cloud, AWS, Azure, GCP, mạng, network, hệ thống, system admin, sysadmin, infra, infrastructure, " +
                    "QA, tester, testing, kiểm thử, bảo mật, cyber security, security, an ninh mạng, " +
                    "database, DBA, ERP, SAP, Java, Python, JavaScript, .NET, C#, PHP, Ruby, Go, Golang, Rust, NodeJS, ReactJS, Angular, Vue, " +
                    "Spring Boot, microservice, API, tech, technology, kỹ sư phần mềm, software engineer, SE, SWE, " +
                    "embedded, IoT, blockchain, Web3, game dev, lập trình viên, programmer\n" +
                    "- 'IND002' = Kinh doanh / Bán hàng — Từ khóa nhận diện: kinh doanh, bán hàng, sales, business, BD, " +
                    "business development, phát triển kinh doanh, account manager, AM, key account, " +
                    "tư vấn, consultant, consulting, thương mại, trade, xuất nhập khẩu, XNK, export, import, " +
                    "đại lý, phân phối, distribution, retail, bán lẻ, bán buôn, wholesale, " +
                    "doanh thu, revenue, đàm phán, negotiation, khách hàng doanh nghiệp, B2B, B2C, " +
                    "telesales, pre-sales, presales, after-sales, account executive, AE, sales executive, " +
                    "sales manager, trưởng phòng kinh doanh, giám đốc kinh doanh, CCO, " +
                    "nhân viên kinh doanh, NVKD, sale, saler\n" +
                    "- 'IND003' = Marketing / Truyền thông — Từ khóa nhận diện: marketing, MKT, PR, " +
                    "public relations, quan hệ công chúng, truyền thông, media, quảng cáo, advertising, ads, " +
                    "content, content creator, content writer, copywriter, copywriting, " +
                    "SEO, SEM, Google Ads, Facebook Ads, digital marketing, online marketing, " +
                    "social media, mạng xã hội, MXH, branding, thương hiệu, xây dựng thương hiệu, " +
                    "event, tổ chức sự kiện, truyền thông nội bộ, communication, " +
                    "influencer, KOL, KOC, affiliate, performance marketing, growth, growth hacking, " +
                    "email marketing, CRM marketing, brand manager, CMO, " +
                    "creative, sáng tạo, video marketing, TikTok, YouTube, livestream\n" +
                    "- 'IND004' = Kế toán / Tài chính — Từ khóa nhận diện: kế toán, kiểm toán, tài chính, " +
                    "accounting, finance, financial, audit, auditor, thuế, tax, " +
                    "ngân hàng, bank, banking, chứng khoán, stock, securities, đầu tư, investment, " +
                    "bảo hiểm, insurance, quỹ, fund, CFO, controller, " +
                    "sổ sách, bookkeeping, hóa đơn, invoice, công nợ, receivable, payable, " +
                    "báo cáo tài chính, financial report, phân tích tài chính, financial analyst, FA, " +
                    "treasury, thủ quỹ, cashier, thu ngân, " +
                    "kế toán trưởng, chief accountant, kế toán tổng hợp, kế toán thuế, kế toán kho, " +
                    "fintech, tín dụng, credit, cho vay, loan, thẩm định, valuation\n" +
                    "- 'IND005' = Hành chính / Nhân sự — Từ khóa nhận diện: hành chính, nhân sự, HR, " +
                    "human resources, tuyển dụng, recruitment, recruiter, headhunter, " +
                    "C&B, compensation, benefits, lương thưởng, phúc lợi, " +
                    "đào tạo nội bộ, L&D, learning, development, " +
                    "quản trị nhân sự, HRM, HRBP, HR business partner, " +
                    "văn phòng, office, admin, administration, thư ký, secretary, " +
                    "lễ tân, receptionist, trợ lý, assistant, PA, personal assistant, " +
                    "quản lý hành chính, office manager, GA, general affairs, tổng vụ, " +
                    "chấm công, payroll, bảng lương, hợp đồng lao động, labor, " +
                    "pháp chế, legal, compliance, CHRO, talent acquisition, TA, " +
                    "onboarding, employer branding, OD, organization development\n" +
                    "- 'IND006' = Kỹ thuật / Cơ khí — Từ khóa nhận diện: kỹ thuật, cơ khí, bảo trì, " +
                    "engineering, mechanical, maintenance, sửa chữa, repair, " +
                    "sản xuất, manufacturing, production, nhà máy, factory, " +
                    "tự động hóa, automation, PLC, SCADA, robot, CNC, " +
                    "ô tô, automotive, xe máy, motor, động cơ, engine, " +
                    "chế tạo, fabrication, gia công, machining, hàn, welding, tiện, phay, " +
                    "QC, quality control, kiểm soát chất lượng, QA/QC, " +
                    "kỹ sư, engineer, technician, kỹ thuật viên, thợ máy, " +
                    "công nghiệp, industrial, R&D, nghiên cứu phát triển, " +
                    "đo lường, measurement, hiệu chuẩn, calibration, ME, " +
                    "lean, six sigma, kaizen, 5S, TPM\n" +
                    "- 'IND007' = Xây dựng / Kiến trúc — Từ khóa nhận diện: xây dựng, kiến trúc, nội thất, " +
                    "construction, architecture, interior, exterior, " +
                    "xây nhà, công trình, building, tòa nhà, cao ốc, " +
                    "giám sát, supervisor, chỉ huy trưởng, site manager, " +
                    "thiết kế công trình, structural, kết cấu, " +
                    "quy hoạch, urban planning, cảnh quan, landscape, " +
                    "thầu, contractor, nhà thầu, thi công, " +
                    "BIM, AutoCAD, CAD, Revit, SketchUp, 3D Max, " +
                    "đo bóc khối lượng, dự toán, estimation, " +
                    "bê tông, concrete, thép, steel, vật liệu xây dựng, VLXD, " +
                    "cấp thoát nước, MEP, HVAC, phòng cháy, PCCC, " +
                    "bất động sản xây dựng, property development\n" +
                    "- 'IND008' = Giáo dục / Đào tạo — Từ khóa nhận diện: giảng dạy, đào tạo, huấn luyện, " +
                    "giáo viên, giáo dục, education, training, teacher, lecturer, " +
                    "giảng viên, sư phạm, pedagogy, " +
                    "trường học, school, đại học, university, college, cao đẳng, " +
                    "gia sư, tutor, tutoring, dạy kèm, " +
                    "trung tâm, center, academy, học viện, " +
                    "IELTS, TOEIC, TOEFL, tiếng Anh, English, ngoại ngữ, language, " +
                    "e-learning, edtech, LMS, " +
                    "nghiên cứu, research, học thuật, academic, " +
                    "mầm non, kindergarten, tiểu học, primary, THCS, THPT, " +
                    "coach, coaching, mentor, mentoring, trainer\n" +
                    "- 'IND009' = Y tế / Dược phẩm — Từ khóa nhận diện: y tế, điều dưỡng, dược, bác sĩ, " +
                    "healthcare, pharmacy, medical, medicine, " +
                    "bệnh viện, hospital, phòng khám, clinic, " +
                    "y sĩ, y tá, nurse, nursing, hộ sinh, midwife, " +
                    "dược sĩ, pharmacist, trình dược viên, medical representative, MR, " +
                    "thiết bị y tế, medical device, vật tư y tế, " +
                    "nha khoa, dental, dentist, răng, " +
                    "thú y, veterinary, vet, " +
                    "chẩn đoán, diagnosis, xét nghiệm, lab, laboratory, " +
                    "dinh dưỡng, nutrition, thể dục, fitness, gym, wellness, spa, " +
                    "tâm lý, psychology, tâm thần, psychiatry, " +
                    "sinh học, biology, biotech, công nghệ sinh học, " +
                    "thẩm mỹ, cosmetic, da liễu, dermatology\n" +
                    "- 'IND010' = Logistics / Vận tải — Từ khóa nhận diện: vận tải, kho bãi, chuỗi cung ứng, " +
                    "logistics, supply chain, SCM, shipping, " +
                    "giao hàng, delivery, chuyển phát, courier, " +
                    "xuất nhập khẩu, XNK, customs, hải quan, " +
                    "kho, warehouse, inventory, tồn kho, " +
                    "vận chuyển, transport, transportation, freight, " +
                    "tàu, ship, cảng, port, container, " +
                    "hàng không, aviation, airline, sân bay, airport, " +
                    "đường bộ, trucking, xe tải, truck, lái xe, driver, " +
                    "forwarder, 3PL, fulfillment, dispatch, " +
                    "mua hàng, procurement, purchasing, thu mua, sourcing\n" +
                    "- 'IND011' = Bất động sản — Từ khóa nhận diện: bất động sản, BĐS, môi giới, " +
                    "real estate, property, nhà đất, đất đai, land, " +
                    "chung cư, apartment, căn hộ, biệt thự, villa, " +
                    "dự án, project, khu đô thị, khu dân cư, " +
                    "cho thuê, lease, rent, rental, " +
                    "sàn giao dịch, broker, agent, " +
                    "phát triển dự án, development, đầu tư BĐS, property investment, " +
                    "quản lý tòa nhà, building management, facility, " +
                    "thẩm định giá, valuation, appraisal, " +
                    "nhà phố, shophouse, townhouse, penthouse\n" +
                    "- 'IND012' = Thiết kế / Đồ họa — Từ khóa nhận diện: thiết kế, UI, UX, UI/UX, " +
                    "đồ họa, design, graphic, graphic design, " +
                    "Photoshop, Illustrator, Figma, Sketch, Adobe, InDesign, Canva, " +
                    "3D, render, animation, motion graphic, " +
                    "video editor, dựng phim, quay phim, filmmaker, " +
                    "UX research, product design, thiết kế sản phẩm, " +
                    "creative designer, art director, visual, " +
                    "brochure, banner, poster, catalog, packaging, bao bì, " +
                    "game art, game design, concept art, illustration, vẽ, minh họa, " +
                    "typography, layout, print, in ấn\n" +
                    "- 'IND013' = Điện / Điện tử / Viễn thông — Từ khóa nhận diện: điện, điện tử, viễn thông, " +
                    "electronics, telecom, telecommunications, " +
                    "điện lực, power, electrical, điện công nghiệp, " +
                    "kỹ sư điện, electrical engineer, EE, " +
                    "mạng viễn thông, 4G, 5G, fiber, cáp quang, " +
                    "Viettel, VNPT, Mobifone, FPT Telecom, " +
                    "lắp đặt, installation, bảo trì điện, " +
                    "vi mạch, chip, semiconductor, bán dẫn, PCB, " +
                    "năng lượng, energy, solar, điện mặt trời, điện gió, wind, " +
                    "tự động điện, automation, PLC, inverter, biến tần, " +
                    "camera, CCTV, an ninh, security system, " +
                    "RF, wireless, antenna, ăng-ten, truyền dẫn, transmission\n" +
                    "- 'IND014' = Dịch vụ khách hàng — Từ khóa nhận diện: CSKH, call center, support, " +
                    "chăm sóc khách hàng, customer service, CS, " +
                    "tổng đài, hotline, helpdesk, help desk, " +
                    "hỗ trợ, technical support, tech support, " +
                    "giải đáp, tư vấn khách hàng, " +
                    "CX, customer experience, trải nghiệm khách hàng, " +
                    "complaint, khiếu nại, phản hồi, feedback, " +
                    "live chat, chatbot, ticket, ticketing, " +
                    "after-sales service, hậu mãi, bảo hành, warranty, " +
                    "customer success, CSM, customer care, " +
                    "contact center, trung tâm liên lạc, operator, điện thoại viên\n" +
                    "- 'IND015' = Khác (các ngành nghề khác không thuộc danh sách trên, " +
                    "ví dụ: nông nghiệp, lâm nghiệp, thủy sản, du lịch, nhà hàng, khách sạn, F&B, " +
                    "nghệ thuật, giải trí, thể thao, luật, pháp lý, môi trường, NGO, phi lợi nhuận)") String industryId,
            @ToolParam(description = "Địa điểm làm việc. Bạn (AI) có trách nhiệm sử dụng kiến thức địa lý để tự phân tích câu nói của người dùng (ví dụ: từ các quận/huyện như Quận 1, Cầu Giấy; từ các thành phố trực thuộc như Nha Trang, Đà Lạt, Buôn Ma Thuột; từ các tên gọi khác/viết tắt/không dấu như Sài Gòn, tp hcm, hn, da nang, hue...) và TỰ ĐỘNG CHUYỂN ĐỔI / CHỌN lựa chính xác tên tỉnh/thành tương ứng từ danh sách hỗ trợ dưới đây để điền vào tham số này. Nếu người dùng không chỉ định địa điểm, hãy để trống.\n"
                    +
                    "Danh sách các tỉnh/thành được hỗ trợ (điền chính xác một trong các chuỗi sau):\n" +
                    "- 'Thành phố Hà Nội' (Hà Nội, Thủ đô)\n" +
                    "- 'Thành phố Hồ Chí Minh' (TP. Hồ Chí Minh, Sài Gòn, các quận/huyện thuộc HCM)\n" +
                    "- 'Thành phố Đà Nẵng' (Đà Nẵng)\n" +
                    "- 'Thành phố Huế' (Huế, Thừa Thiên Huế)\n" +
                    "- 'Thành phố Hải Phòng' (Hải Phòng)\n" +
                    "- 'Thành phố Cần Thơ' (Cần Thơ)\n" +
                    "- 'Tỉnh Khánh Hòa' (Khánh Hòa, Nha Trang)\n" +
                    "- 'Tỉnh Lâm Đồng' (Lâm Đồng, Đà Lạt)\n" +
                    "- 'Tỉnh Đắk Lắk' (Đắk Lắk, Đắc Lắc, Buôn Ma Thuột)\n" +
                    "- 'Tỉnh Cao Bằng', 'Tỉnh Tuyên Quang', 'Tỉnh Điện Biên', 'Tỉnh Lai Châu', 'Tỉnh Sơn La', 'Tỉnh Lào Cai', 'Tỉnh Thái Nguyên', 'Tỉnh Lạng Sơn', 'Tỉnh Quảng Ninh', 'Tỉnh Bắc Ninh', 'Tỉnh Phú Thọ', 'Tỉnh Hưng Yên', 'Tỉnh Ninh Bình', 'Tỉnh Thanh Hóa', 'Tỉnh Nghệ An', 'Tỉnh Hà Tĩnh', 'Tỉnh Quảng Trị', 'Tỉnh Quảng Ngãi', 'Tỉnh Gia Lai', 'Tỉnh Đồng Nai', 'Tỉnh Tây Ninh', 'Tỉnh Đồng Tháp', 'Tỉnh Vĩnh Long', 'Tỉnh An Giang', 'Tỉnh Cà Mau'") String location,
            @ToolParam(description = "Mức lương tối thiểu mong muốn. LƯU Ý QUAN TRỌNG: Truyền CHÍNH XÁC con số thô người dùng nhập/yêu cầu (ví dụ: người dùng nói '1000' thì điền 1000.0, nói '1500' thì điền 1500.0, nói '15000000' thì điền 15000000.0). Tuyệt đối KHÔNG được nhân lên, chia nhỏ, quy đổi hay biên dịch ý nghĩa (ví dụ: người dùng nói '1000' thì CẤM tự ý hiểu là '1 triệu' và điền 1000000.0, hãy điền đúng 1000.0).") Double salaryMin) {
        try {
            String normalizedLocation = LocationNormalizer.normalizeLocation(location);
            // Mặc định status "ACTIVE", gọi hàm search public từ JobService
            return jobService.searchJobs(
                    keyword, industryId, null, null, null,
                    normalizedLocation, "ACTIVE",
                    null, null, salaryMin, null,
                    null, null, null,
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
            "CHỈ sử dụng tool này khi người dùng ĐÃ XÁC NHẬN muốn nộp đơn vào một công việc cụ thể hoặc yêu cầu bạn giúp họ ứng tuyển. "
            +
            "LƯU Ý QUAN TRỌNG: Bạn (AI) phải tự trích xuất jobId tương ứng từ kết quả của các công cụ tìm kiếm trước đó trong lịch sử trò chuyện để điền vào. Tuyệt đối KHÔNG được hỏi người dùng cung cấp jobId hoặc mã công việc, vì người dùng không thể biết mã này.")
    public String applyToJob(
            @ToolParam(description = "ID của công việc mà người dùng muốn ứng tuyển, tự động trích xuất từ kết quả tìm kiếm trước đó trong lịch sử cuộc trò chuyện (không bao giờ được hỏi người dùng cung cấp mã này)") String jobId,
            ToolContext toolContext) {
        try {
            if (toolContext == null || toolContext.getContext() == null) {
                return "Hãy từ chối yêu cầu và trả lời chính xác câu sau: 'Bạn cần đăng nhập để nộp đơn ứng tuyển vào công việc này. Vui lòng đăng nhập tại đây: [Đăng nhập](/login) [REQUIRE_LOGIN]'";
            }
            String userId = (String) toolContext.getContext().get("userId");
            if (userId == null || userId.isBlank()) {
                return "Hãy từ chối yêu cầu và trả lời chính xác câu sau: 'Bạn cần đăng nhập để nộp đơn ứng tuyển vào công việc này. Vui lòng đăng nhập tại đây: [Đăng nhập](/login) [REQUIRE_LOGIN]'";
            }
            // Trả về câu thông báo yêu cầu thực hiện hành động tiếp theo bằng thẻ Markdown
            // link
            return "Hãy trả lời người dùng bằng câu sau: 'Vui lòng nhấn vào đường link này để tới trang nộp CV ứng tuyển: [Đi đến trang Ứng tuyển](/job/"
                    + jobId + "?apply=true)'";
        } catch (Exception e) {
            System.err.println("Error running applyToJob tool: " + e.getMessage());
            return "Đã xảy ra lỗi khi chuẩn bị đường dẫn ứng tuyển. Vui lòng đăng nhập và thử lại hoặc bấm trực tiếp vào trang chi tiết công việc.";
        }
    }
}
