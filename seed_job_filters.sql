\connect "job-service"

TRUNCATE TABLE favourites CASCADE;
TRUNCATE TABLE jobs CASCADE;
TRUNCATE TABLE fields CASCADE;
TRUNCATE TABLE industrys CASCADE;

INSERT INTO industrys (industry_id, name, description) VALUES
('IND001', 'Công nghệ thông tin', 'Phần mềm, mạng, AI'),
('IND002', 'Kinh doanh / Bán hàng', 'Kinh doanh, tư vấn, bán hàng'),
('IND003', 'Marketing / Truyền thông', 'Marketing, PR, truyền thông'),
('IND004', 'Kế toán / Tài chính', 'Kế toán, kiểm toán, tài chính'),
('IND005', 'Hành chính / Nhân sự', 'Hành chính, tuyển dụng, C&B'),
('IND006', 'Kỹ thuật / Cơ khí', 'Kỹ thuật, cơ khí, bảo trì'),
('IND007', 'Xây dựng / Kiến trúc', 'Xây dựng, kiến trúc, nội thất'),
('IND008', 'Giáo dục / Đào tạo', 'Giảng dạy, đào tạo, huấn luyện'),
('IND009', 'Y tế / Dược phẩm', 'Y tế, điều dưỡng, dược'),
('IND010', 'Logistics / Vận tải', 'Vận tải, kho bãi, chuỗi cung ứng'),
('IND011', 'Bất động sản', 'Môi giới, tư vấn, phát triển dự án'),
('IND012', 'Thiết kế / Đồ họa', 'Thiết kế, UI/UX, đồ họa'),
('IND013', 'Điện / Điện tử / Viễn thông', 'Điện, điện tử, viễn thông'),
('IND014', 'Dịch vụ khách hàng', 'CSKH, call center, support'),
('IND015', 'Khác', 'Các ngành nghề khác')
ON CONFLICT (industry_id) DO NOTHING;

INSERT INTO fields (field_id, industry_id, name, description) VALUES
('FLD001', 'IND001', 'Backend Developer', 'Java, Node.js, Python'),
('FLD002', 'IND001', 'Frontend Developer', 'React, Angular, Vue'),
('FLD003', 'IND002', 'Accountant', 'Kế toán tổng hợp'),
('FLD004', 'IND008', 'Teacher', 'Giảng dạy, đào tạo'),
('FLD005', 'IND012', 'UI/UX Designer', 'Thiết kế giao diện, trải nghiệm người dùng')
ON CONFLICT (field_id) DO NOTHING;

INSERT INTO jobs (
    job_id,
    company_id,
    company_name,
    employer_id,
    title,
    description,
    candidate_requirements,
    salary_detail,
    benefits_detail,
    work_schedule,
    province,
    ward,
    address_detail,
    salary_min,
    salary_max,
    salary_negotiable,
    experience_required,
    deadline,
    created_at,
    updated_at,
    views,
    number_of_applications,
    is_top,
    company_subscription_id,
    package_id,
    package_label,
    marketing_assignment_id,
    marketing_entitlement_id,
    marketing_package_category,
    marketing_package_type,
    marketing_package_label,
    rank,
    education,
    quantity,
    age_range,
    industry_id,
    status,
    job_type,
    requirement_tags,
    benefit_tags,
    specialties,
    related_categories,
    skills,
    company_logo_url
) VALUES
('JOB001', 'COMP001', 'FPT Software', 'EMP001', 'Senior Java Backend Developer', 'Phat trien backend Java, Spring Boot, Microservices', 'Java, Spring Boot, REST API, SQL, Docker', 'Luong theo nang luc', 'BHXH day du, laptop, review luong', 'Thu 2 - Thu 6', 'Thành phố Hà Nội', 'Cầu Giấy', '88 Duy Tan', 25000000, 40000000, false, '5 năm', NOW() + interval '30 days', NOW(), NOW(), 120, 0, true, NULL, 'HP001', 'Trending Post', NULL, NULL, 'HIGHLIGHT', 'TRENDING_POST', 'TRENDING', 'Senior', 'Đại học', 5, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://fpt.png'),
('JOB002', 'COMP002', 'VNG Corp', 'EMP002', 'Frontend React Developer', 'Xay dung giao dien ReactJS va TypeScript', 'React, TypeScript, HTML, CSS', 'Luong canh tranh', 'Remote linh hoat, phu cap an trua', 'Thu 2 - Thu 6', 'Thành phố Hồ Chí Minh', 'Quận 7', '123 Nguyen Van Linh', 18000000, 30000000, true, '2 năm', NOW() + interval '20 days', NOW(), NOW(), 95, 0, false, NULL, 'JP001', 'Basic Job', NULL, NULL, NULL, NULL, NULL, 'Middle', 'Đại học', 3, '1-3 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://vng.png'),
('JOB003', 'COMP003', 'Viettel', 'EMP003', 'DevOps Engineer', 'Quan ly CI/CD, Docker, Kubernetes, AWS', 'Docker, Kubernetes, CI/CD, Linux', 'Thuong KPI', 'Bao hiem, dao tao, phu cap onsite', 'Thu 2 - Thu 6', 'Thành phố Hà Nội', 'Nam Tu Liem', '1 Giang Vo', 30000000, 50000000, false, '4 năm', NOW() + interval '25 days', NOW(), NOW(), 160, 0, true, NULL, 'EF001', 'Hot Effect', NULL, NULL, 'EFFECT', 'EFFECT_HOT', 'HOT', 'Senior', 'Thạc sĩ', 2, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://viettel.png'),
('JOB004', 'COMP004', 'Momo', 'EMP004', 'Product Owner', 'Quan ly backlog, phoi hop team san pham', 'Agile, Scrum, Product Thinking', 'Luong va thuong du an', 'BHXH, budget hoc tap, team building', 'Thu 2 - Thu 6', 'Thành phố Hồ Chí Minh', 'Quận 1', '53 Nguyen Hue', 28000000, 45000000, true, '3 năm', NOW() + interval '28 days', NOW(), NOW(), 110, 0, false, NULL, 'EF002', 'Bold Effect', NULL, NULL, 'EFFECT', 'EFFECT_BOLD', 'Chữ đậm', 'Trưởng nhóm', 'Đại học', 1, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://momo.png'),
('JOB005', 'COMP005', 'Tiki', 'EMP005', 'Business Analyst', 'Phan tich nghiep vu, viet tai lieu yeu cau', 'BA, SQL, UAT, Communication', 'Luong co ban + thuong', 'BHXH, phu cap com', 'Thu 2 - Thu 6', 'Thành phố Hồ Chí Minh', 'Quận 3', 'Lau 2, 52 Le Dai Hanh', 16000000, 28000000, true, '2 năm', NOW() + interval '18 days', NOW(), NOW(), 88, 0, true, NULL, 'HP002', 'Industry Priority', NULL, NULL, 'HIGHLIGHT', 'INDUSTRY_PRIORITY', 'BEST SELLER', 'Middle', 'Đại học', 2, '1-3 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://tiki.png'),
('JOB006', 'COMP006', 'Shopee', 'EMP006', 'Sales Executive', 'Tim kiem va cham soc khach hang, tu van san pham', 'Giao tiep, dam phan, chiu ap luc', 'Luong co ban + hoa hong', 'Hoa hong, du lich, dao tao', 'Thu 2 - Thu 7', 'Thành phố Cần Thơ', 'Ninh Kieu', '01 Hoa Binh', 9000000, 15000000, true, '0 năm', NOW() + interval '15 days', NOW(), NOW(), 72, 0, false, NULL, 'JP002', 'Urgent Job', NULL, NULL, NULL, NULL, NULL, 'Nhân viên', 'Trung cấp', 8, '0-1 năm', 'IND002', 'ACTIVE', 'PART_TIME', '[]', '[]', '[]', '[]', '[]', 'https://shopee.png'),
('JOB007', 'COMP012', 'VinFast', 'EMP012', 'Embedded Software Engineer', 'Phat trien phan mem nhung cho he thong xe dien', 'C, C++, Embedded Linux', 'Luong theo nang luc', 'BHXH, xe dua don, moi truong R&D', 'Thu 2 - Thu 6', 'Thành phố Hải Phòng', 'Hai An', 'Dinh Vu Cat Hai', 22000000, 38000000, false, '5 năm', NOW() + interval '24 days', NOW(), NOW(), 130, 0, true, NULL, 'EF003', 'Frame Effect', NULL, NULL, 'EFFECT', 'EFFECT_FRAME', 'Đóng khung', 'Senior', 'Đại học', 4, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://vinfast.png'),
('JOB008', 'COMP016', 'SmartDev', 'EMP016', 'English Teacher', 'Giang day tieng Anh va xay dung chuong trinh hoc', 'TOEIC, IELTS, giao tiep tot', 'Luong co ban + phu cap', 'BHXH, dao tao, hoc phi uu dai', 'Thu 2 - Thu 6', 'Thành phố Đà Nẵng', 'Hai Chau', '25 Le Duan', 14000000, 22000000, true, '1 năm', NOW() + interval '14 days', NOW(), NOW(), 55, 0, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Nhân viên', 'Đại học', 3, '1-2 năm', 'IND008', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://sd.png'),
('JOB009', 'COMP008', 'Tek Expert', 'EMP008', 'Customer Support Specialist', 'Ho tro khach hang qua email va chat', 'Giao tiep, xu ly tinh huong, Excel co ban', 'Luong co ban + phu cap ca dem', 'BHXH, phu cap, nha o', 'Ca linh hoat', 'Thành phố Hà Nội', 'Long Bien', 'Toa nha Tech', 10000000, 16000000, false, '1 năm', NOW() + interval '12 days', NOW(), NOW(), 40, 0, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Nhân viên', 'Cao Đẳng trở lên', 6, '0-2 năm', 'IND014', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://tek.png'),
('JOB010', 'COMP013', 'Base.vn', 'EMP013', 'UI/UX Designer', 'Thiet ke giao dien SaaS, prototype, design system', 'Figma, UI Design, UX Research', 'Luong canh tranh', 'BHXH, remote hybrid, phu cap an trua', 'Thu 2 - Thu 6', 'Thành phố Hồ Chí Minh', 'Quận 10', '1233 Su Van Hanh', 20000000, 35000000, true, '2 năm', NOW() + interval '21 days', NOW(), NOW(), 98, 0, true, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Middle', 'Đại học', 2, '2-4 năm', 'IND012', 'ACTIVE', 'FREELANCE', '[]', '[]', '[]', '[]', '[]', 'https://base.png'),
('JOB011', 'COMP007', 'Grab', 'EMP007', 'Logistics Coordinator', 'Dieu phoi don hang, tuyen va theo doi van hanh', 'Excel, sap xep ke hoach, giao tiep', 'Luong co ban + phu cap', 'BHXH, thuong hieu, team building', 'Thu 2 - Thu 7', 'Tỉnh Hưng Yên', 'Van Giang', 'KCN Thang Long II', 15000000, 26000000, false, '2 năm', NOW() + interval '19 days', NOW(), NOW(), 61, 0, false, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Nhân viên', 'Đại học', 4, '1-3 năm', 'IND010', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://grab.png'),
('JOB012', 'COMP017', 'Sun*', 'EMP017', 'QA Automation Engineer', 'Xay dung test automation cho he thong web', 'Java, Selenium, API Testing', 'Luong co ban + thuong', 'BHXH, training, review luong', 'Thu 2 - Thu 6', 'Thành phố Hà Nội', 'Cau Giay', 'Sun* Tower', 20000000, 36000000, false, '3 năm', NOW() + interval '22 days', NOW(), NOW(), 140, 0, true, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Trưởng nhóm', 'Đại học', 2, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://sun.png')
ON CONFLICT (job_id) DO NOTHING;

UPDATE jobs
SET marketing_package_category = 'HIGHLIGHT',
    marketing_package_type = 'TRENDING_POST',
    marketing_package_label = 'TRENDING',
    updated_at = NOW()
WHERE job_id IN ('JOB001', 'JOB003', 'JOB005', 'JOB007', 'JOB012');

UPDATE jobs
SET marketing_package_category = 'EFFECT',
    marketing_package_type = 'EFFECT_HOT',
    marketing_package_label = 'HOT',
    updated_at = NOW()
WHERE job_id = 'JOB003';

UPDATE jobs
SET marketing_package_category = 'BRANDING',
    marketing_package_type = 'BRANDING_LOGO',
    marketing_package_label = 'Logo thương hiệu',
    updated_at = NOW()
WHERE job_id = 'JOB007';