-- =========================================================
-- CV - Job Matching Test Data
-- Muc tieu:
-- - Tao 1 job co JD day du de test AI matching
-- - Tao 4 CV trong cv-service database
-- - Tao 4 application cung apply vao 1 job de recruiter thay ranking
--
-- Dieu kien:
-- - Da co du lieu company / employer / candidate / industry co san
-- - Co cac ID sau tu seed cu:
--   COMP001, EMP001, IND001
--   CAND001, CAND002, CAND003, CAND004
-- =========================================================

-- =========================================================
-- 1. JOB TEST CHO MATCHING
-- Database: job-service
-- =========================================================

\connect "job-service"

DELETE FROM jobs WHERE job_id IN ('JOBMATCH001', 'JOBMATCH002');

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
    location,
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
    deleted_at,
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
    company_logo_url,
    company_size,
    company_address,
    job_level,
    work_type,
    employment_type,
    province_code,
    district,
    contact_email,
    contact_phone
) VALUES
(
    'JOBMATCH001',
    'COMP001',
    'FPT Software',
    'EMP001',
    'Backend Java Developer',
    'Phat trien va van hanh he thong backend voi Java, Spring Boot va Microservices. Tham gia thiet ke REST API, toi uu SQL, logging, monitoring va phoi hop cung frontend, DevOps.',
    'Java, Spring Boot, REST API, SQL, Microservices, Docker, Git. Uu tien ung vien co kinh nghiem AWS hoac CI/CD.',
    'Luong canh tranh theo nang luc. Review luong 2 lan moi nam.',
    'Bao hiem day du, laptop, du an lon, co mentor ky thuat.',
    'Thu 2 - Thu 6, hybrid 3 ngay onsite.',
    'Ha Noi',
    1800,
    3200,
    false,
    '3 years',
    CURRENT_DATE + INTERVAL '30 days',
    NOW(),
    NOW(),
    120,
    4,
    true,
    NULL,
    'PKG-TEST-001',
    'Premium',
    NULL,
    'Senior',
    'Dai hoc',
    2,
    '22-35',
    'IND001',
    'ACTIVE',
    'FULL_TIME',
    '["Java","Spring Boot","REST API","SQL","Docker"]',
    '["Bao hiem","Hybrid","Review luong"]',
    '["Backend","Microservices","Cloud-ready"]',
    '["Backend Developer","Java Developer","Software Engineer"]',
    '["Java","Spring Boot","REST API","SQL","Microservices","Docker","Git","AWS"]',
    'https://fpt.png',
    '30000',
    'Ha Noi',
    'Senior',
    'Hybrid',
    'Full-time',
    'HN',
    'Cau Giay',
    'hr@fpt.com',
    '02437687077'
),
(
    'JOBMATCH002',
    'COMP001',
    'FPT Software',
    'EMP001',
    'Python FastAPI Developer',
    'Phat trien backend Python su dung FastAPI, PostgreSQL, Docker va tich hop AI service.',
    'Python, FastAPI, PostgreSQL, Docker, REST API. Uu tien co NLP, ML hoac sentence-transformers.',
    'Thuong theo du an va KPI.',
    'BHXH, training, budget hoc tap.',
    'Thu 2 - Thu 6.',
    'Ha Noi',
    1600,
    2800,
    true,
    '2 years',
    CURRENT_DATE + INTERVAL '25 days',
    NOW(),
    NOW(),
    45,
    0,
    false,
    NULL,
    'PKG-TEST-002',
    'Standard',
    NULL,
    'Middle',
    'Dai hoc',
    1,
    '22-32',
    'IND001',
    'ACTIVE',
    'FULL_TIME',
    '["Python","FastAPI","PostgreSQL","Docker"]',
    '["Training","Laptop"]',
    '["Backend","AI Integration"]',
    '["Python Developer","Backend Developer"]',
    '["Python","FastAPI","PostgreSQL","Docker","REST API","NLP"]',
    'https://fpt.png',
    '30000',
    'Ha Noi',
    'Middle',
    'Onsite',
    'Full-time',
    'HN',
    'Cau Giay',
    'hr@fpt.com',
    '02437687077'
)
ON CONFLICT (job_id) DO NOTHING;

-- =========================================================
-- 2. CV TEST
-- Database: cv-service
-- =========================================================

\connect "cv-service"

DELETE FROM cv_skills WHERE cv_id IN (
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    '33333333-3333-3333-3333-333333333333',
    '44444444-4444-4444-4444-444444444444'
);

DELETE FROM cv_experiences WHERE cv_id IN (
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    '33333333-3333-3333-3333-333333333333',
    '44444444-4444-4444-4444-444444444444'
);

DELETE FROM cv_educations WHERE cv_id IN (
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    '33333333-3333-3333-3333-333333333333',
    '44444444-4444-4444-4444-444444444444'
);

DELETE FROM cvs WHERE id IN (
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    '33333333-3333-3333-3333-333333333333',
    '44444444-4444-4444-4444-444444444444'
);

INSERT INTO cvs (
    id,
    user_id,
    name,
    template_id,
    avatar_url,
    status,
    updated_at,
    file_url,
    full_name,
    email,
    phone,
    address,
    dob,
    job_title,
    linkedin,
    summary
) VALUES
(
    '11111111-1111-1111-1111-111111111111',
    'CAND001',
    'CV Backend Java - Nguyen Van A',
    1,
    'https://ui-avatars.com/api/?name=Nguyen+Van+A',
    'PUBLISHED',
    NOW(),
    'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
    'Nguyen Van A',
    'cand1@gmail.com',
    '0912345671',
    'Ha Noi',
    '1995-01-01',
    'Senior Java Backend Developer',
    'https://linkedin.com/in/nguyenvana',
    'Backend engineer voi 3+ nam kinh nghiem Java, Spring Boot, REST API, SQL va microservices. Tung lam viec voi Docker, Git va toi uu he thong doanh nghiep.'
),
(
    '22222222-2222-2222-2222-222222222222',
    'CAND002',
    'CV Fullstack - Le Thi B',
    1,
    'https://ui-avatars.com/api/?name=Le+Thi+B',
    'PUBLISHED',
    NOW(),
    'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
    'Le Thi B',
    'cand2@gmail.com',
    '0912345672',
    'HCM',
    '1998-01-01',
    'Fullstack Developer',
    'https://linkedin.com/in/lethib',
    'Fullstack developer co kinh nghiem React, Node.js, Java co ban va SQL. Da tham gia xay dung API noi bo va dashboard quan tri.'
),
(
    '33333333-3333-3333-3333-333333333333',
    'CAND003',
    'CV Solution Architect - Tran Van C',
    1,
    'https://ui-avatars.com/api/?name=Tran+Van+C',
    'PUBLISHED',
    NOW(),
    'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
    'Tran Van C',
    'cand3@gmail.com',
    '0912345673',
    'Da Nang',
    '1992-01-01',
    'Principal Backend Engineer',
    'https://linkedin.com/in/tranvanc',
    'Kien truc su giai phap va backend engineer hon 7 nam kinh nghiem voi Java, Spring Boot, Microservices, Docker, Kubernetes, AWS va system design.'
),
(
    '44444444-4444-4444-4444-444444444444',
    'CAND004',
    'CV Business Analyst - Pham Thi D',
    1,
    'https://ui-avatars.com/api/?name=Pham+Thi+D',
    'PUBLISHED',
    NOW(),
    'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
    'Pham Thi D',
    'cand4@gmail.com',
    '0912345674',
    'Ha Noi',
    '2000-01-01',
    'Business Analyst',
    'https://linkedin.com/in/phamthid',
    'Business analyst moi ra truong, phan tich nghiep vu, viet tai lieu va ho tro UAT. Khong chuyen ve backend Java.'
);

INSERT INTO cv_skills (id, name, level, cv_id) VALUES
('a1111111-1111-1111-1111-111111111111', 'Java', 'Advanced', '11111111-1111-1111-1111-111111111111'),
('a1111111-1111-1111-1111-111111111112', 'Spring Boot', 'Advanced', '11111111-1111-1111-1111-111111111111'),
('a1111111-1111-1111-1111-111111111113', 'REST API', 'Advanced', '11111111-1111-1111-1111-111111111111'),
('a1111111-1111-1111-1111-111111111114', 'SQL', 'Advanced', '11111111-1111-1111-1111-111111111111'),
('a1111111-1111-1111-1111-111111111115', 'Microservices', 'Intermediate', '11111111-1111-1111-1111-111111111111'),
('a1111111-1111-1111-1111-111111111116', 'Docker', 'Intermediate', '11111111-1111-1111-1111-111111111111'),
('a1111111-1111-1111-1111-111111111117', 'Git', 'Advanced', '11111111-1111-1111-1111-111111111111'),

('a2222222-2222-2222-2222-222222222221', 'React', 'Advanced', '22222222-2222-2222-2222-222222222222'),
('a2222222-2222-2222-2222-222222222222', 'Node.js', 'Intermediate', '22222222-2222-2222-2222-222222222222'),
('a2222222-2222-2222-2222-222222222223', 'Java', 'Beginner', '22222222-2222-2222-2222-222222222222'),
('a2222222-2222-2222-2222-222222222224', 'SQL', 'Intermediate', '22222222-2222-2222-2222-222222222222'),
('a2222222-2222-2222-2222-222222222225', 'Git', 'Intermediate', '22222222-2222-2222-2222-222222222222'),

('a3333333-3333-3333-3333-333333333331', 'Java', 'Expert', '33333333-3333-3333-3333-333333333333'),
('a3333333-3333-3333-3333-333333333332', 'Spring Boot', 'Expert', '33333333-3333-3333-3333-333333333333'),
('a3333333-3333-3333-3333-333333333333', 'Microservices', 'Expert', '33333333-3333-3333-3333-333333333333'),
('a3333333-3333-3333-3333-333333333334', 'SQL', 'Expert', '33333333-3333-3333-3333-333333333333'),
('a3333333-3333-3333-3333-333333333335', 'Docker', 'Expert', '33333333-3333-3333-3333-333333333333'),
('a3333333-3333-3333-3333-333333333336', 'AWS', 'Advanced', '33333333-3333-3333-3333-333333333333'),
('a3333333-3333-3333-3333-333333333337', 'Kubernetes', 'Advanced', '33333333-3333-3333-3333-333333333333'),
('a3333333-3333-3333-3333-333333333338', 'REST API', 'Expert', '33333333-3333-3333-3333-333333333333'),
('a3333333-3333-3333-3333-333333333339', 'Git', 'Expert', '33333333-3333-3333-3333-333333333333'),

('a4444444-4444-4444-4444-444444444441', 'Business Analysis', 'Intermediate', '44444444-4444-4444-4444-444444444444'),
('a4444444-4444-4444-4444-444444444442', 'UAT', 'Intermediate', '44444444-4444-4444-4444-444444444444'),
('a4444444-4444-4444-4444-444444444443', 'Documentation', 'Advanced', '44444444-4444-4444-4444-444444444444'),
('a4444444-4444-4444-4444-444444444444', 'Excel', 'Advanced', '44444444-4444-4444-4444-444444444444');

INSERT INTO cv_experiences (id, company, role, start_date, end_date, description, cv_id) VALUES
('b1111111-1111-1111-1111-111111111111', 'ABC Tech', 'Java Backend Developer', '2021-01', '2023-12', 'Phat trien REST API, xu ly SQL, Spring Boot va Docker cho he thong noi bo.', '11111111-1111-1111-1111-111111111111'),
('b1111111-1111-1111-1111-111111111112', 'XYZ Solution', 'Backend Engineer', '2024-01', 'Present', 'Lam microservices, logging, monitoring, tich hop GitLab CI/CD co ban.', '11111111-1111-1111-1111-111111111111'),

('b2222222-2222-2222-2222-222222222221', 'Startup One', 'Frontend Developer', '2023-01', 'Present', 'Xay dung dashboard React, co tham gia mot so API Java co ban.', '22222222-2222-2222-2222-222222222222'),

('b3333333-3333-3333-3333-333333333331', 'Global Product', 'Senior Backend Engineer', '2017-01', '2021-12', 'Thiet ke microservices Java, Docker, Kubernetes, AWS, toi uu he thong lon.', '33333333-3333-3333-3333-333333333333'),
('b3333333-3333-3333-3333-333333333332', 'Enterprise Corp', 'Principal Engineer', '2022-01', 'Present', 'Dan dat kien truc backend, API strategy, reliability va mentoring team.', '33333333-3333-3333-3333-333333333333'),

('b4444444-4444-4444-4444-444444444441', 'Biz Team', 'BA Intern', '2024-06', 'Present', 'Phan tich nghiep vu, viet user story va test case.', '44444444-4444-4444-4444-444444444444');

INSERT INTO cv_educations (id, school, major, start_date, end_date, description, cv_id) VALUES
('c1111111-1111-1111-1111-111111111111', 'IUH', 'Cong nghe thong tin', '2013-09', '2017-06', 'Cu nhan CNTT', '11111111-1111-1111-1111-111111111111'),
('c2222222-2222-2222-2222-222222222221', 'HCMUS', 'Cong nghe phan mem', '2016-09', '2020-06', 'Cu nhan CNTT', '22222222-2222-2222-2222-222222222222'),
('c3333333-3333-3333-3333-333333333331', 'Bach Khoa', 'Khoa hoc may tinh', '2010-09', '2015-06', 'Ky su dai hoc', '33333333-3333-3333-3333-333333333333'),
('c4444444-4444-4444-4444-444444444441', 'Thuong Mai', 'He thong thong tin quan ly', '2018-09', '2022-06', 'Cu nhan', '44444444-4444-4444-4444-444444444444');

-- =========================================================
-- 3. APPLICATION TEST
-- Database: application-service
-- =========================================================

\connect "application-service"

DELETE FROM job_applications WHERE id IN (
    'APP-MATCH-001',
    'APP-MATCH-002',
    'APP-MATCH-003',
    'APP-MATCH-004',
    'APP-MATCH-005'
);

INSERT INTO job_applications (
    id,
    job_id,
    candidate_id,
    cv_id,
    company_id,
    industry_id,
    url,
    note,
    status,
    applied_at,
    updated_at,
    interview_date,
    interview_time,
    interview_location,
    rejection_reason
) VALUES
(
    'APP-MATCH-001',
    'JOBMATCH001',
    'CAND001',
    '11111111-1111-1111-1111-111111111111',
    'COMP001',
    'IND001',
    'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
    'Ung tuyen backend Java vi kinh nghiem phu hop.',
    'APPLIED',
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '3 days',
    NULL,
    NULL,
    NULL,
    NULL
),
(
    'APP-MATCH-002',
    'JOBMATCH001',
    'CAND002',
    '22222222-2222-2222-2222-222222222222',
    'COMP001',
    'IND001',
    'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
    'Muon chuyen huong sang backend.',
    'REVIEWING',
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '1 day',
    NULL,
    NULL,
    NULL,
    NULL
),
(
    'APP-MATCH-003',
    'JOBMATCH001',
    'CAND003',
    '33333333-3333-3333-3333-333333333333',
    'COMP001',
    'IND001',
    'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
    'Quan tam vi tri backend senior va architecture.',
    'INTERVIEW',
    NOW() - INTERVAL '4 days',
    NOW() - INTERVAL '12 hours',
    '2026-05-15',
    '09:00',
    'FPT Tower Ha Noi',
    NULL
),
(
    'APP-MATCH-004',
    'JOBMATCH001',
    'CAND004',
    '44444444-4444-4444-4444-444444444444',
    'COMP001',
    'IND001',
    'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
    'Muon thu suc voi IT product.',
    'REJECTED',
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '2 days',
    NULL,
    NULL,
    NULL,
    'Chua phu hop vi tri backend Java hien tai.'
),
(
    'APP-MATCH-005',
    'JOBMATCH002',
    'CAND001',
    '11111111-1111-1111-1111-111111111111',
    'COMP001',
    'IND001',
    'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
    'Thu them vai tro Python AI integration.',
    'APPLIED',
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '1 day',
    NULL,
    NULL,
    NULL,
    NULL
);

-- =========================================================
-- 4. CAP NHAT SO LUONG UNG TUYEN CHO JOB
-- Database: job-service
-- =========================================================

\connect "job-service"

UPDATE jobs
SET number_of_applications = 4,
    updated_at = NOW()
WHERE job_id = 'JOBMATCH001';

UPDATE jobs
SET number_of_applications = 1,
    updated_at = NOW()
WHERE job_id = 'JOBMATCH002';
