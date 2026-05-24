TRUNCATE TABLE favourites CASCADE;
TRUNCATE TABLE jobs CASCADE;
TRUNCATE TABLE fields CASCADE;
TRUNCATE TABLE industrys CASCADE;

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
('IND015', 'Khác', 'Các ngành nghề khác')
ON CONFLICT (industry_id) DO NOTHING;

-- Fields
INSERT INTO fields (field_id, industry_id, name, description) VALUES
('FLD001', 'IND001', 'Backend Developer', 'Java, Node.js, Python'),
('FLD002', 'IND001', 'Frontend Developer', 'React, Angular, Vue'),
('FLD003', 'IND002', 'Accountant', 'Kế toán tổng hợp')
ON CONFLICT (field_id) DO NOTHING;

-- Jobs
INSERT INTO jobs (job_id, company_id, company_name, employer_id, title, description, location, salary_min, salary_max, salary_negotiable, experience_required, deadline, created_at, status, job_type, industry_id, is_top, number_of_applications, quantity, views) VALUES
('JOB001', 'COMP001', 'FPT Software', 'EMP001', 'Java Senior Developer', 'Yêu cầu 5 năm KN Java', 'Ha Noi', 2000, 3500, false, '5 years', NOW() + interval '30 days', NOW(), 'ACTIVE', 'FULL_TIME', 'IND001', false, 0, 5, 0),
('JOB002', 'COMP002', 'VNG Corp', 'EMP002', 'React Developer', 'Yêu cầu ReactJS, Redux', 'HCM', 1500, 2500, true, '2 years', NOW() + interval '15 days', NOW(), 'ACTIVE', 'FULL_TIME', 'IND001', false, 0, 10, 0)
ON CONFLICT (job_id) DO NOTHING;


-- =========================================================
-- JOBS + RELATION DATA
-- Full relation with:
-- companies
-- employers
-- industrys
-- fields
-- =========================================================

-- JOBS
INSERT INTO jobs (
    job_id,
    company_id,
    company_name,
    employer_id,
    title,
    description,
    location,
    salary_min,
    salary_max,
    salary_negotiable,
    experience_required,
    deadline,
    created_at,
    status,
    job_type,
    industry_id,
    is_top,
    number_of_applications,
    quantity,
    views
) VALUES

      ('JOB003','COMP003','Viettel','EMP003',
       'Backend Java Developer',
       'Phát triển hệ thống backend sử dụng Spring Boot và Microservices',
       'Ha Noi',
       1800,3200,false,
       '3 years',
       NOW() + interval '20 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       12,
       5,
       120),

      ('JOB004','COMP004','Momo','EMP004',
       'Frontend React Developer',
       'Xây dựng giao diện ReactJS, TypeScript và TailwindCSS',
       'HCM',
       1600,2800,true,
       '2 years',
       NOW() + interval '25 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       false,
       8,
       3,
       90),

      ('JOB005','COMP005','Tiki','EMP005',
       'DevOps Engineer',
       'Quản lý CI/CD, Docker, Kubernetes',
       'HCM',
       2200,4000,false,
       '4 years',
       NOW() + interval '18 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       20,
       2,
       210),

      ('JOB006','COMP006','Shopee','EMP006',
       'QA Automation Engineer',
       'Selenium, API Testing, Performance Testing',
       'HCM',
       1200,2200,true,
       '1 year',
       NOW() + interval '14 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       false,
       5,
       4,
       75),

      ('JOB007','COMP007','Grab','EMP007',
       'Data Analyst',
       'SQL, Power BI, Data Visualization',
       'HCM',
       1500,2600,false,
       '2 years',
       NOW() + interval '21 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       false,
       15,
       6,
       180),

      ('JOB008','COMP008','Tek Expert','EMP008',
       'Technical Support Engineer',
       'Hỗ trợ hệ thống cloud và server',
       'Ha Noi',
       1000,1800,true,
       '1 year',
       NOW() + interval '10 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       false,
       4,
       5,
       60),

      ('JOB009','COMP009','NashTech','EMP009',
       'Fullstack Developer',
       'Java Spring Boot + ReactJS',
       'HCM',
       2000,3500,false,
       '3 years',
       NOW() + interval '28 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       18,
       3,
       240),

      ('JOB010','COMP010','Zalo','EMP010',
       'Mobile Developer',
       'Android Kotlin hoặc iOS Swift',
       'HCM',
       1800,3200,false,
       '2 years',
       NOW() + interval '16 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       9,
       4,
       135),

      ('JOB011','COMP011','Lazada','EMP011',
       'Business Analyst',
       'Phân tích yêu cầu nghiệp vụ hệ thống',
       'HCM',
       1400,2500,true,
       '2 years',
       NOW() + interval '12 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND002',
       false,
       6,
       2,
       80),

      ('JOB012','COMP012','VinFast','EMP012',
       'Embedded Software Engineer',
       'C/C++, Embedded Linux',
       'Hai Phong',
       2500,4500,false,
       '5 years',
       NOW() + interval '30 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       25,
       8,
       320),

      ('JOB013','COMP013','Base.vn','EMP013',
       'UI/UX Designer',
       'Thiết kế sản phẩm SaaS',
       'HCM',
       1200,2200,true,
       '2 years',
       NOW() + interval '15 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       false,
       7,
       3,
       95),

      ('JOB014','COMP014','KMS','EMP014',
       'Python Developer',
       'Django, FastAPI, RESTful API',
       'HCM',
       1800,3300,false,
       '3 years',
       NOW() + interval '22 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       14,
       5,
       175),

      ('JOB015','COMP015','NAB','EMP015',
       'Cloud Engineer',
       'AWS, Terraform, Kubernetes',
       'HCM',
       2600,4200,false,
       '4 years',
       NOW() + interval '26 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       21,
       4,
       290),

      ('JOB016','COMP016','SmartDev','EMP016',
       'Junior Java Developer',
       'Java Core, Spring Boot cơ bản',
       'Da Nang',
       800,1500,true,
       '0 year',
       NOW() + interval '20 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       false,
       3,
       6,
       45),

      ('JOB017','COMP017','Sun*','EMP017',
       'BrSE',
       'Tiếng Nhật N2, cầu nối kỹ thuật',
       'Ha Noi',
       2000,3800,false,
       '3 years',
       NOW() + interval '24 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       16,
       3,
       205),

      ('JOB018','COMP018','Amanotes','EMP018',
       'Game Developer',
       'Unity, C#, Mobile Game',
       'HCM',
       1700,3000,true,
       '2 years',
       NOW() + interval '17 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       false,
       11,
       5,
       160),

      ('JOB019','COMP019','Be Group','EMP019',
       'Product Owner',
       'Quản lý backlog sản phẩm',
       'HCM',
       2500,4200,false,
       '4 years',
       NOW() + interval '19 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       19,
       2,
       260),

      ('JOB020','COMP020','OneMount','EMP020',
       'System Administrator',
       'Linux, Monitoring, Networking',
       'Ha Noi',
       1500,2700,false,
       '2 years',
       NOW() + interval '13 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       false,
       10,
       4,
       115),

      ('JOB021','COMP001','FPT Software','EMP001',
       'AI Engineer',
       'Machine Learning, TensorFlow, Python',
       'Ha Noi',
       2500,5000,false,
       '3 years',
       NOW() + interval '29 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       30,
       6,
       400),

      ('JOB022','COMP002','VNG Corp','EMP002',
       'Cyber Security Engineer',
       'Security testing, SIEM, SOC',
       'HCM',
       2300,4200,false,
       '4 years',
       NOW() + interval '27 days',
       NOW(),
       'ACTIVE',
       'FULL_TIME',
       'IND001',
       true,
       17,
       3,
       230)

    ON CONFLICT (job_id) DO NOTHING;

-- Sample BRANDING jobs to power Featured Companies on public home page
UPDATE jobs
SET marketing_package_category = 'BRANDING',
    marketing_package_type = 'BRANDING_LOGO',
    marketing_package_label = 'Logo thương hiệu',
    updated_at = NOW()
WHERE job_id IN ('JOB001', 'JOB003', 'JOB005');

-- Demo marketing labels for candidate home page
-- Highlight: tăng độ ưu tiên hiển thị
UPDATE jobs
SET marketing_package_category = 'HIGHLIGHT',
    marketing_package_type = 'TRENDING_POST',
    marketing_package_label = 'TRENDING',
    is_top = true,
    updated_at = NOW()
WHERE job_id IN ('JOB001', 'JOB005');

-- Effect: HOT / bold / frame
UPDATE jobs
SET marketing_package_category = 'EFFECT',
    marketing_package_type = 'EFFECT_HOT',
    marketing_package_label = 'HOT',
    updated_at = NOW()
WHERE job_id = 'JOB003';

UPDATE jobs
SET marketing_package_category = 'EFFECT',
    marketing_package_type = 'EFFECT_BOLD',
    marketing_package_label = 'Chữ đậm',
    updated_at = NOW()
WHERE job_id = 'JOB004';

UPDATE jobs
SET marketing_package_category = 'EFFECT',
    marketing_package_type = 'EFFECT_FRAME',
    marketing_package_label = 'Đóng khung',
    updated_at = NOW()
WHERE job_id = 'JOB006';

-- =========================================================
-- JOB ↔ FIELD RELATION

-- =========================================================
-- FAVORITE JOBS
-- =========================================================

INSERT INTO favourites (favourite_id, candidate_id, job_id, created_at) VALUES
                                                                            ('FAV001','CAND001','JOB003',NOW()),
                                                                            ('FAV002','CAND001','JOB004',NOW()),
                                                                            ('FAV003','CAND002','JOB005',NOW()),
                                                                            ('FAV004','CAND002','JOB009',NOW()),
                                                                            ('FAV005','CAND003','JOB012',NOW()),
                                                                            ('FAV006','CAND004','JOB016',NOW()),
                                                                            ('FAV007','CAND005','JOB018',NOW())
    ON CONFLICT (favourite_id) DO NOTHING;