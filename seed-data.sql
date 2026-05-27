-- Seed data for Career Connect Microservices
-- Target: PostgreSQL Server

-----------------------------------------------------------
-- 1. DATABASE: company-service
-----------------------------------------------------------
\c "company-service"

INSERT INTO companies (company_id, name, logo, tax_code, website, email, phone, address, description, company_size, founded_year, status_company, created_at, approval_status, approved_by) VALUES
('COMP001', 'FPT Software', 'https://fpt.png', '0101248141', 'https://fpt.com', 'hr@fpt.com', '02437687077', 'Ha Noi', 'IT Services', 30000, 1999, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP002', 'VNG Corp', 'https://vng.png', '0303215392', 'https://vng.com', 'hr@vng.com', '02839623888', 'HCM', 'Internet Services', 5000, 2004, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP003', 'Viettel', 'https://viettel.png', '0100109106', 'https://viettel.vn', 'hr@viettel.vn', '18008098', 'Ha Noi', 'Telecom', 50000, 1989, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP004', 'Momo', 'https://momo.png', '0305289153', 'https://momo.vn', 'hr@momo.vn', '02839151550', 'HCM', 'Fintech', 2000, 2007, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP005', 'Tiki', 'https://tiki.png', '0309535990', 'https://tiki.vn', 'hr@tiki.vn', '19006035', 'HCM', 'E-commerce', 3000, 2010, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP006', 'Shopee', 'https://shopee.png', '0313566133', 'https://shopee.vn', 'hr@shopee.vn', '19001221', 'HCM', 'E-commerce', 4000, 2015, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP007', 'Grab', 'https://grab.png', '0312650437', 'https://grab.com', 'hr@grab.com', '02871087108', 'HCM', 'Ride-hailing', 1500, 2014, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP008', 'Tek Expert', 'https://tek.png', '0107753177', 'https://tek.com', 'hr@tek.com', '02439561234', 'Ha Noi', 'Technical Support', 2000, 2011, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP009', 'NashTech', 'https://nash.png', '0302014728', 'https://nashtech.com', 'hr@nash.com', '02838106200', 'HCM', 'Software', 2500, 2000, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP010', 'Zalo', 'https://zalo.png', '0303215392-1', 'https://zalo.me', 'hr@zalo.me', '02839623888', 'HCM', 'Messaging', 1000, 2012, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP011', 'Lazada', 'https://lazada.png', '0311226743', 'https://lazada.vn', 'hr@lazada.vn', '19001007', 'HCM', 'E-commerce', 2000, 2012, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP012', 'VinFast', 'https://vinfast.png', '0105847076', 'https://vinfast.vn', 'hr@vinfast.vn', '1900232389', 'Hai Phong', 'Automotive', 10000, 2017, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP013', 'Base.vn', 'https://base.png', '0107431268', 'https://base.vn', 'hr@base.vn', '02422466981', 'HCM', 'SaaS', 500, 2016, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP014', 'KMS', 'https://kms.png', '0306325510', 'https://kms.com', 'hr@kms.com', '02838486000', 'HCM', 'Software', 1500, 2009, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP015', 'NAB', 'https://nab.png', '0315754562', 'https://nab.com', 'hr@nab.com', '02836224000', 'HCM', 'Banking IT', 1000, 2019, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP016', 'SmartDev', 'https://sd.png', '0401625341', 'https://smartdev.com', 'hr@sd.com', '02363888321', 'Da Nang', 'Software', 300, 2014, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP017', 'Sun*', 'https://sun.png', '0105813350', 'https://sun.vn', 'hr@sun.vn', '02437955463', 'Ha Noi', 'Software', 2000, 2012, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP018', 'Amanotes', 'https://ama.png', '0313137258', 'https://amanotes.com', 'hr@ama.com', '02862908231', 'HCM', 'Gaming', 200, 2014, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP019', 'Be Group', 'https://be.png', '0315184852', 'https://be.vn', 'hr@be.vn', '1900232345', 'HCM', 'Ride-hailing', 1000, 2018, 'ACTIVE', NOW(), 'APPROVED', 'system'),
('COMP020', 'OneMount', 'https://om.png', '0108906161', 'https://onemount.com', 'hr@om.com', '02432045555', 'Ha Noi', 'Ecosystem', 2000, 2019, 'ACTIVE', NOW(), 'APPROVED', 'system');

-----------------------------------------------------------
-- 2. DATABASE: user-service (Part 1)
-----------------------------------------------------------
\c "user-service"

-- Admins (2)
INSERT INTO admins (admin_id, email, password, full_name, phone, avatar, created_at, updated_at, status) VALUES
('ADM001', 'admin@cc.com', '$2a$10$X87SxD1h8lshh8.92SxD1.Lh.v8.Lh.v8.Lh.v8.Lh.v8.Lh.v8', 'Admin 1', '0123456789', 'https://avatar.png', NOW(), NOW(), 'ACTIVE'),
('ADM002', 'mod@cc.com', '$2a$10$X87SxD1h8lshh8.92SxD1.Lh.v8.Lh.v8.Lh.v8.Lh.v8.Lh.v8', 'Admin 2', '0123456788', 'https://avatar.png', NOW(), NOW(), 'ACTIVE');

-- Candidates (5)
INSERT INTO candidates (candidate_id, email, password, full_name, phone, avatar, created_at, updated_at, status, date_of_birth, address, experience_year, current_job_title, expected_salary) VALUES
('CAND001', 'cand1@gmail.com', '$2a$10$7', 'Nguyen Van A', '0912345671', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', '1995-01-01', 'HN', 3, 'Java Dev', 1500),
('CAND002', 'cand2@gmail.com', '$2a$10$7', 'Le Thi B', '0912345672', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', '1998-01-01', 'HCM', 1, 'Web Dev', 1000),
('CAND003', 'cand3@gmail.com', '$2a$10$7', 'Tran Van C', '0912345673', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', '1992-01-01', 'DN', 7, 'PM', 2500),
('CAND004', 'cand4@gmail.com', '$2a$10$7', 'Pham Thi D', '0912345674', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', '2000-01-01', 'HN', 0, 'Analyst', 500),
('CAND005', 'cand5@gmail.com', '$2a$10$7', 'Hoang Van E', '0912345675', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', '1996-01-01', 'HCM', 4, 'Designer', 1800);

-- Employers (20)
INSERT INTO employers (employer_id, email, password, full_name, phone, avatar, created_at, updated_at, status, position, company_id) VALUES
('EMP001', 'hr1@fpt.com', '$2a$10$7', 'Emp 1', '0987111001', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'HR', 'COMP001'),
('EMP002', 'hr2@vng.com', '$2a$10$7', 'Emp 2', '0987111002', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Lead', 'COMP002'),
('EMP003', 'hr3@viettel.vn', '$2a$10$7', 'Emp 3', '0987111003', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Spec', 'COMP003'),
('EMP004', 'hr4@momo.vn', '$2a$10$7', 'Emp 4', '0987111004', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Rec', 'COMP004'),
('EMP005', 'hr5@tiki.vn', '$2a$10$7', 'Emp 5', '0987111005', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Tech', 'COMP005'),
('EMP006', 'hr6@shopee.vn', '$2a$10$7', 'Emp 6', '0987111006', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Talent', 'COMP006'),
('EMP007', 'hr7@grab.com', '$2a$10$7', 'Emp 7', '0987111007', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Ops', 'COMP007'),
('EMP008', 'hr8@tek.com', '$2a$10$7', 'Emp 8', '0987111008', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Staff', 'COMP008'),
('EMP009', 'hr9@nash.com', '$2a$10$7', 'Emp 9', '0987111009', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Man', 'COMP009'),
('EMP010', 'hr10@zalo.me', '$2a$10$7', 'Emp 10', '0987111010', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'BP', 'COMP010'),
('EMP011', 'hr11@lazada.vn', '$2a$10$7', 'Emp 11', '0987111011', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Rec', 'COMP011'),
('EMP012', 'hr12@vinfast.vn', '$2a$10$7', 'Emp 12', '0987111012', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Dir', 'COMP012'),
('EMP013', 'hr13@base.vn', '$2a$10$7', 'Emp 13', '0987111013', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'CEO', 'COMP013'),
('EMP014', 'hr14@kms.com', '$2a$10$7', 'Emp 14', '0987111014', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Exec', 'COMP014'),
('EMP015', 'hr15@nab.com', '$2a$10$7', 'Emp 15', '0987111015', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Lead', 'COMP015'),
('EMP016', 'hr16@sd.com', '$2a$10$7', 'Emp 16', '0987111016', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Dev', 'COMP016'),
('EMP017', 'hr17@sun.vn', '$2a$10$7', 'Emp 17', '0987111017', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Man', 'COMP017'),
('EMP018', 'hr18@ama.com', '$2a$10$7', 'Emp 18', '0987111018', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Lead', 'COMP018'),
('EMP019', 'hr19@be.vn', '$2a$10$7', 'Emp 19', '0987111019', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Man', 'COMP019'),
('EMP020', 'hr20@om.com', '$2a$10$7', 'Emp 20', '0987111020', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Chair', 'COMP020');

-- Recruiters (Mirror Employers)
INSERT INTO recruiters (id, username, email, company_id, role, status, created_at, updated_at) VALUES
('EMP001', 'emp1', 'hr1@fpt.com', 'COMP001', 'RECRUITER', 'ACTIVE', NOW(), NOW()),
('EMP002', 'emp2', 'hr2@vng.com', 'COMP002', 'RECRUITER', 'ACTIVE', NOW(), NOW()),
('EMP003', 'emp3', 'hr3@viettel.vn', 'COMP003', 'RECRUITER', 'ACTIVE', NOW(), NOW()),
('EMP004', 'emp4', 'hr4@momo.vn', 'COMP004', 'RECRUITER', 'ACTIVE', NOW(), NOW());

-----------------------------------------------------------
-- 3. DATABASE: payment-service
-----------------------------------------------------------
\c "payment-service"

-- JobPackages
INSERT INTO jobpackages (package_id, badge, badge_color, category, description, duration_days, image_url, is_active, job_post_limit, name, old_price, price, show_details, type) VALUES
('BR001', '', '', 'BRANDING', 'Logo trên trang chủ', 28, 'https://via.placeholder.com/100x60?text=Brand', true, 4, 'Logo thương hiệu', NULL, 16000000, false, 'BRANDING_LOGO'),
('EF001', 'HOT', 'red', 'EFFECT', 'Gắn nhãn HOT', 14, 'https://via.placeholder.com/100x60?text=Hot', true, 4, 'Hot Effect', NULL, 0, false, 'EFFECT_HOT'),
('EF002', '', '', 'EFFECT', 'Chữ đậm', 14, 'https://via.placeholder.com/100x60?text=Bold', true, 3, 'Bold Effect', NULL, 500000, false, 'EFFECT_BOLD'),
('EF003', '', '', 'EFFECT', 'Đóng khung', 14, 'https://via.placeholder.com/100x60?text=Frame', true, 5, 'Frame Effect', NULL, 600000, false, 'EFFECT_FRAME'),
('HP001', 'TRENDING', 'yellow', 'HIGHLIGHT', 'Nổi bật trang chủ', 7, 'https://via.placeholder.com/100x60?text=Trending', true, 4, 'Trending Post', NULL, 3260000, false, 'TRENDING_POST'),
('HP002', 'BEST SELLER', 'orange', 'HIGHLIGHT', 'Nổi bật theo ngành', 14, 'https://via.placeholder.com/100x60?text=Best', true, 3, 'Industry Priority', NULL, 2390000, false, 'INDUSTRY_PRIORITY'),
('JP001', '', '', 'JOB_POSTING', 'Tin cơ bản 4 tuần', 28, 'https://via.placeholder.com/100x60?text=Basic', true, 2, 'Basic Job', NULL, 1720000, false, 'BASIC_JOB_POST'),
('JP002', 'MỚI', 'red', 'JOB_POSTING', 'Việc làm nhanh', 7, 'https://via.placeholder.com/100x60?text=Urgent', true, 2, 'Urgent Job', 3690000, 2583000, true, 'URGENT_JOB_POST'),
('PT001', '', '', 'POINTS', 'Gói 100 điểm', 90, 'https://via.placeholder.com/100x60?text=Points', true, 4, 'Point Service', NULL, 2820000, false, 'POINT_SERVICE');

-- jobpackage_box_types
INSERT INTO jobpackage_box_types (package_id, box_type) VALUES
('EF001','TRANG_CHU'),('EF001','TUYEN_GAP'),('EF001','UU_TIEN'),('EF001','NGANH'),
('EF002','TRANG_CHU'),('EF002','UU_TIEN'),('EF003','NGANH'),
('HP001','TRANG_CHU'),('HP002','NGANH');

-----------------------------------------------------------
-- 4. DATABASE: job-service
-----------------------------------------------------------
\c "job-service"

-- Industries
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
('IND015', 'Khác', 'Các ngành nghề khác');

-- Fields
INSERT INTO fields (field_id, industry_id, name, description) VALUES
('FLD001', 'IND001', 'Backend Developer', 'Java, Node.js, Python'),
('FLD002', 'IND001', 'Frontend Developer', 'React, Angular, Vue'),
('FLD003', 'IND002', 'Accountant', 'Kế toán tổng hợp');

-- Jobs
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
('JOB003', 'COMP003', 'Viettel', 'EMP003', 'DevOps Engineer', 'Quan ly CI/CD, Docker, Kubernetes, AWS', 'Docker, Kubernetes, CI/CD, Linux', 'Thuong KPI', 'Bao hiem, dao tao, phu cap onsite', 'Thu 2 - Thu 6', 'Thành phố Hà Nội', 'Nam Từ Liêm', '1 Giang Vo', 30000000, 50000000, false, '4 năm', NOW() + interval '25 days', NOW(), NOW(), 160, 0, true, NULL, 'EF001', 'Hot Effect', NULL, NULL, 'EFFECT', 'EFFECT_HOT', 'HOT', 'Senior', 'Thạc sĩ', 2, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://viettel.png'),
('JOB004', 'COMP004', 'Momo', 'EMP004', 'Product Owner', 'Quan ly backlog, phoi hop team san pham', 'Agile, Scrum, Product Thinking', 'Luong va thuong du an', 'BHXH, budget hoc tap, team building', 'Thu 2 - Thu 6', 'Thành phố Hồ Chí Minh', 'Quận 1', '53 Nguyen Hue', 28000000, 45000000, true, '3 năm', NOW() + interval '28 days', NOW(), NOW(), 110, 0, false, NULL, 'EF002', 'Bold Effect', NULL, NULL, 'EFFECT', 'EFFECT_BOLD', 'Chữ đậm', 'Trưởng nhóm', 'Đại học', 1, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://momo.png'),
('JOB005', 'COMP005', 'Tiki', 'EMP005', 'Business Analyst', 'Phan tich nghiep vu, viet tai lieu yeu cau', 'BA, SQL, UAT, Communication', 'Luong co ban + thuong', 'BHXH, phu cap com', 'Thu 2 - Thu 6', 'Thành phố Hồ Chí Minh', 'Quận 3', 'Lau 2, 52 Le Dai Hanh', 16000000, 28000000, true, '2 năm', NOW() + interval '18 days', NOW(), NOW(), 88, 0, true, NULL, 'HP002', 'Industry Priority', NULL, NULL, 'HIGHLIGHT', 'INDUSTRY_PRIORITY', 'BEST SELLER', 'Middle', 'Đại học', 2, '1-3 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://tiki.png'),
('JOB006', 'COMP012', 'VinFast', 'EMP012', 'Embedded Software Engineer', 'Phat trien phan mem nhung cho he thong xe dien', 'C, C++, Embedded Linux', 'Luong theo nang luc', 'BHXH, xe dua don, moi truong R&D', 'Thu 2 - Thu 6', 'Thành phố Hải Phòng', 'Hải An', 'Dinh Vu Cat Hai', 22000000, 38000000, false, '5 năm', NOW() + interval '24 days', NOW(), NOW(), 130, 0, true, NULL, 'EF003', 'Frame Effect', NULL, NULL, 'EFFECT', 'EFFECT_FRAME', 'Đóng khung', 'Senior', 'Đại học', 4, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://vinfast.png')
ON CONFLICT (job_id) DO NOTHING;


