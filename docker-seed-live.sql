-- Live seed for running Docker containers.
-- Use after docker compose up when the Postgres volume already exists.

\c "company-service"

INSERT INTO companies (
  company_id, name, logo, tax_code, website, email, phone, address,
  description, company_size, founded_year, status_company, created_at
) VALUES
('COMP001', 'FPT Software', 'https://fpt.png', '0101248141', 'https://fpt.com', 'hr@fpt.com', '02437687077', 'Ha Noi', 'IT Services', 30000, 1999, 'VERIFIED', NOW()),
('COMP002', 'VNG Corp', 'https://vng.png', '0303215392', 'https://vng.com', 'hr@vng.com', '02839623888', 'HCM', 'Internet Services', 5000, 2004, 'VERIFIED', NOW()),
('COMP003', 'Viettel', 'https://viettel.png', '0100109106', 'https://viettel.vn', 'hr@viettel.vn', '18008098', 'Ha Noi', 'Telecom', 50000, 1989, 'VERIFIED', NOW()),
('COMP004', 'Momo', 'https://momo.png', '0305289153', 'https://momo.vn', 'hr@momo.vn', '02839151550', 'HCM', 'Fintech', 2000, 2007, 'VERIFIED', NOW()),
('COMP005', 'Tiki', 'https://tiki.png', '0309535990', 'https://tiki.vn', 'hr@tiki.vn', '19006035', 'HCM', 'E-commerce', 3000, 2010, 'VERIFIED', NOW()),
('COMP006', 'Shopee', 'https://shopee.png', '0106773786', 'https://shopee.vn', 'hr@shopee.vn', '19001221', 'HCM', 'E-commerce', 4000, 2015, 'VERIFIED', NOW()),
('COMP009', 'NashTech', 'https://nash.png', '0302014728', 'https://nashtech.com', 'hr@nash.com', '02838106200', 'HCM', 'Software', 2500, 2000, 'VERIFIED', NOW()),
('COMP012', 'VinFast', 'https://vinfast.png', '0105847076', 'https://vinfast.vn', 'hr@vinfast.vn', '1900232389', 'Hai Phong', 'Automotive', 10000, 2017, 'VERIFIED', NOW()),
('COMP014', 'KMS', 'https://kms.png', '0306325510', 'https://kms.com', 'hr@kms.com', '02838486000', 'HCM', 'Software', 1500, 2009, 'VERIFIED', NOW()),
('COMP015', 'NAB', 'https://nab.png', '0315754562', 'https://nab.com', 'hr@nab.com', '02836224000', 'HCM', 'Banking IT', 1000, 2019, 'VERIFIED', NOW())
ON CONFLICT (company_id) DO NOTHING;

INSERT INTO company_verifications (
  verification_id, company_id, status, submitted_tax_code, business_license, verified_by,
  submitted_at, verified_at, note
) VALUES
('VER001', 'COMP001', 'APPROVED', '0101248141', 'license_fpt.pdf', 'system', NOW(), NOW(), 'Seed data'),
('VER002', 'COMP003', 'APPROVED', '0100109106', 'license_viettel.pdf', 'system', NOW(), NOW(), 'Seed data'),
('VER003', 'COMP005', 'APPROVED', '0309535990', 'license_tiki.pdf', 'system', NOW(), NOW(), 'Seed data')
ON CONFLICT (verification_id) DO NOTHING;

INSERT INTO company_marketing_entitlements (
  id, company_id, payment_id, package_id, package_label, package_category,
  package_type, target_scope, usage_limit, used_count, quantity, duration_days,
  start_date, end_date, status
) VALUES
('ENTBR001', 'COMP001', 'PAYBR001', 'BR001', 'Logo thương hiệu', 'BRANDING', 'BRANDING_LOGO', 'COMPANY', 1, 0, 1, 28, NOW(), NOW() + interval '28 days', 'ACTIVE'),
('ENTBR002', 'COMP003', 'PAYBR002', 'BR001', 'Logo thương hiệu', 'BRANDING', 'BRANDING_LOGO', 'COMPANY', 1, 0, 1, 28, NOW(), NOW() + interval '28 days', 'ACTIVE'),
('ENTBR003', 'COMP005', 'PAYBR003', 'BR001', 'Logo thương hiệu', 'BRANDING', 'BRANDING_LOGO', 'COMPANY', 1, 0, 1, 28, NOW(), NOW() + interval '28 days', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

\c "payment-service"

INSERT INTO jobpackages (
  package_id, badge, badge_color, category, description, duration_days,
  image_url, is_active, job_post_limit, name, old_price, price, show_details, type
) VALUES
('BR001', '', '', 'BRANDING', 'Logo trên trang chủ', 28, 'https://via.placeholder.com/100x60?text=Brand', true, 4, 'Logo thương hiệu', NULL, 16000000, false, 'BRANDING_LOGO'),
('EF001', 'HOT', 'red', 'EFFECT', 'Gắn nhãn HOT', 14, 'https://via.placeholder.com/100x60?text=Hot', true, 4, 'Hot Effect', NULL, 0, false, 'EFFECT_HOT'),
('EF002', '', '', 'EFFECT', 'Chữ đậm', 14, 'https://via.placeholder.com/100x60?text=Bold', true, 3, 'Bold Effect', NULL, 500000, false, 'EFFECT_BOLD'),
('EF003', '', '', 'EFFECT', 'Đóng khung', 14, 'https://via.placeholder.com/100x60?text=Frame', true, 5, 'Frame Effect', NULL, 600000, false, 'EFFECT_FRAME'),
('HP001', 'TRENDING', 'yellow', 'HIGHLIGHT', 'Nổi bật trang chủ', 7, 'https://via.placeholder.com/100x60?text=Trending', true, 4, 'Trending Post', NULL, 3260000, false, 'TRENDING_POST'),
('HP002', 'BEST SELLER', 'orange', 'HIGHLIGHT', 'Nổi bật theo ngành', 14, 'https://via.placeholder.com/100x60?text=Best', true, 3, 'Industry Priority', NULL, 2390000, false, 'INDUSTRY_PRIORITY'),
('JP001', '', '', 'JOB_POSTING', 'Tin cơ bản 4 tuần', 28, 'https://via.placeholder.com/100x60?text=Basic', true, 2, 'Basic Job', NULL, 1720000, false, 'BASIC_JOB_POST'),
('JP002', 'MỚI', 'red', 'JOB_POSTING', 'Việc làm nhanh', 7, 'https://via.placeholder.com/100x60?text=Urgent', true, 2, 'Urgent Job', 3690000, 2583000, true, 'URGENT_JOB_POST'),
('PT001', '', '', 'POINTS', 'Gói 100 điểm', 90, 'https://via.placeholder.com/100x60?text=Points', true, 4, 'Point Service', NULL, 2820000, false, 'POINT_SERVICE')
ON CONFLICT (package_id) DO NOTHING;

INSERT INTO jobpackage_box_types (package_id, box_type) VALUES
('EF001','TRANG_CHU'),('EF001','TUYEN_GAP'),('EF001','UU_TIEN'),('EF001','NGANH'),
('EF002','TRANG_CHU'),('EF002','UU_TIEN'),('EF003','NGANH'),
('HP001','TRANG_CHU'),('HP002','NGANH')
ON CONFLICT DO NOTHING;

\c "job-service"

INSERT INTO industrys (industry_id, name, description) VALUES
('IND001', 'Công nghệ thông tin', 'Phần mềm, mạng, AI'),
('IND002', 'Kinh doanh / Bán hàng', 'Kinh doanh, tư vấn, bán hàng'),
('IND003', 'Marketing / Truyền thông', 'Marketing, PR, truyền thông'),
('IND004', 'Kế toán / Tài chính', 'Kế toán, kiểm toán, tài chính')
ON CONFLICT (industry_id) DO NOTHING;

INSERT INTO fields (field_id, industry_id, name, description) VALUES
('FLD001', 'IND001', 'Backend Developer', 'Java, Node.js, Python'),
('FLD002', 'IND001', 'Frontend Developer', 'React, Angular, Vue'),
('FLD003', 'IND002', 'Accountant', 'Kế toán tổng hợp')
ON CONFLICT (field_id) DO NOTHING;

INSERT INTO jobs (
  job_id, company_id, company_name, employer_id, title, description,
  candidate_requirements, salary_detail, benefits_detail, work_schedule,
  province, ward, address_detail, salary_min, salary_max, salary_negotiable,
  experience_required, deadline, created_at, updated_at, views, number_of_applications,
  is_top, company_subscription_id, package_id, package_label, marketing_assignment_id,
  marketing_entitlement_id, marketing_package_category, marketing_package_type,
  marketing_package_label, rank, education, quantity, age_range, industry_id,
  status, job_type, requirement_tags, benefit_tags, specialties, related_categories,
  skills, company_logo_url
) VALUES
('JOB001', 'COMP001', 'FPT Software', 'EMP001', 'Senior Java Backend Developer', 'Phat trien backend Java, Spring Boot, Microservices', 'Java, Spring Boot, REST API, SQL, Docker', 'Luong theo nang luc', 'BHXH day du, laptop, review luong', 'Thu 2 - Thu 6', 'Thành phố Hà Nội', 'Cầu Giấy', '88 Duy Tan', 25000000, 40000000, false, '5 năm', NOW() + interval '30 days', NOW(), NOW(), 120, 0, true, NULL, 'HP001', 'Trending Post', NULL, NULL, 'HIGHLIGHT', 'TRENDING_POST', 'TRENDING', 'Senior', 'Đại học', 5, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://fpt.png'),
('JOB002', 'COMP002', 'VNG Corp', 'EMP002', 'Frontend React Developer', 'Xay dung giao dien ReactJS va TypeScript', 'React, TypeScript, HTML, CSS', 'Luong canh tranh', 'Remote linh hoat, phu cap an trua', 'Thu 2 - Thu 6', 'Thành phố Hồ Chí Minh', 'Quận 7', '123 Nguyen Van Linh', 18000000, 30000000, true, '2 năm', NOW() + interval '20 days', NOW(), NOW(), 95, 0, false, NULL, 'JP001', 'Basic Job', NULL, NULL, NULL, NULL, NULL, 'Middle', 'Đại học', 3, '1-3 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://vng.png'),
('JOB003', 'COMP003', 'Viettel', 'EMP003', 'DevOps Engineer', 'Quan ly CI/CD, Docker, Kubernetes, AWS', 'Docker, Kubernetes, CI/CD, Linux', 'Thuong KPI', 'Bao hiem, dao tao, phu cap onsite', 'Thu 2 - Thu 6', 'Thành phố Hà Nội', 'Nam Từ Liêm', '1 Giang Vo', 30000000, 50000000, false, '4 năm', NOW() + interval '25 days', NOW(), NOW(), 160, 0, true, NULL, 'EF001', 'Hot Effect', NULL, NULL, 'EFFECT', 'EFFECT_HOT', 'HOT', 'Senior', 'Thạc sĩ', 2, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://viettel.png'),
('JOB004', 'COMP004', 'Momo', 'EMP004', 'Product Owner', 'Quan ly backlog, phoi hop team san pham', 'Agile, Scrum, Product Thinking', 'Luong va thuong du an', 'BHXH, budget hoc tap, team building', 'Thu 2 - Thu 6', 'Thành phố Hồ Chí Minh', 'Quận 1', '53 Nguyen Hue', 28000000, 45000000, true, '3 năm', NOW() + interval '28 days', NOW(), NOW(), 110, 0, false, NULL, 'EF002', 'Bold Effect', NULL, NULL, 'EFFECT', 'EFFECT_BOLD', 'Chữ đậm', 'Trưởng nhóm', 'Đại học', 1, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://momo.png'),
('JOB005', 'COMP005', 'Tiki', 'EMP005', 'Business Analyst', 'Phan tich nghiep vu, viet tai lieu yeu cau', 'BA, SQL, UAT, Communication', 'Luong co ban + thuong', 'BHXH, phu cap com', 'Thu 2 - Thu 6', 'Thành phố Hồ Chí Minh', 'Quận 3', 'Lau 2, 52 Le Dai Hanh', 16000000, 28000000, true, '2 năm', NOW() + interval '18 days', NOW(), NOW(), 88, 0, true, NULL, 'HP002', 'Industry Priority', NULL, NULL, 'HIGHLIGHT', 'INDUSTRY_PRIORITY', 'BEST SELLER', 'Middle', 'Đại học', 2, '1-3 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://tiki.png'),
('JOB006', 'COMP012', 'VinFast', 'EMP012', 'Embedded Software Engineer', 'Phat trien phan mem nhung cho he thong xe dien', 'C, C++, Embedded Linux', 'Luong theo nang luc', 'BHXH, xe dua don, moi truong R&D', 'Thu 2 - Thu 6', 'Thành phố Hải Phòng', 'Hải An', 'Dinh Vu Cat Hai', 22000000, 38000000, false, '5 năm', NOW() + interval '24 days', NOW(), NOW(), 130, 0, true, NULL, 'EF003', 'Frame Effect', NULL, NULL, 'EFFECT', 'EFFECT_FRAME', 'Đóng khung', 'Senior', 'Đại học', 4, '3-5 năm', 'IND001', 'ACTIVE', 'FULL_TIME', '[]', '[]', '[]', '[]', '[]', 'https://vinfast.png')
ON CONFLICT (job_id) DO NOTHING;
