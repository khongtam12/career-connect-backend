-- Industries
INSERT INTO industrys (industry_id, name, description) VALUES
('IND001', 'Công nghệ thông tin', 'Phần mềm, mạng, AI'),
('IND002', 'Tài chính - Ngân hàng', 'Ngân hàng, chứng khoán'),
('IND003', 'Y tế - Chăm sóc sức khỏe', 'Bệnh viện, dược phẩm')
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
