-- Admins
INSERT INTO admins (admin_id, email, password, full_name, phone, avatar, created_at, updated_at, status) VALUES
('ADM001', 'admin@cc.com', '$2a$10$X87SxD1h8lshh8.92SxD1.Lh.v8.Lh.v8.Lh.v8.Lh.v8.Lh.v8', 'Admin 1', '0123456789', 'https://avatar.png', NOW(), NOW(), 'ACTIVE'),
('ADM002', 'mod@cc.com', '$2a$10$X87SxD1h8lshh8.92SxD1.Lh.v8.Lh.v8.Lh.v8.Lh.v8.Lh.v8', 'Admin 2', '0123456788', 'https://avatar.png', NOW(), NOW(), 'ACTIVE')
ON CONFLICT (admin_id) DO NOTHING;

-- Candidates
INSERT INTO candidates (candidate_id, email, password, full_name, phone, avatar, created_at, updated_at, status, date_of_birth, address, experience_year, current_job_title, expected_salary) VALUES
('CAND001', 'cand1@gmail.com', '$2a$10$7', 'Nguyen Van A', '0912345671', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', '1995-01-01', 'HN', 3, 'Java Dev', 1500),
('CAND002', 'cand2@gmail.com', '$2a$10$7', 'Le Thi B', '0912345672', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', '1998-01-01', 'HCM', 1, 'Web Dev', 1000),
('CAND003', 'cand3@gmail.com', '$2a$10$7', 'Tran Van C', '0912345673', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', '1992-01-01', 'DN', 7, 'PM', 2500),
('CAND004', 'cand4@gmail.com', '$2a$10$7', 'Pham Thi D', '0912345674', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', '2000-01-01', 'HN', 0, 'Analyst', 500),
('CAND005', 'cand5@gmail.com', '$2a$10$7', 'Hoang Van E', '0912345675', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', '1996-01-01', 'HCM', 4, 'Designer', 1800)
ON CONFLICT (candidate_id) DO NOTHING;

-- Employers
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
('EMP020', 'hr20@om.com', '$2a$10$7', 'Emp 20', '0987111020', 'https://avatar.png', NOW(), NOW(), 'ACTIVE', 'Chair', 'COMP020')
ON CONFLICT (employer_id) DO NOTHING;


