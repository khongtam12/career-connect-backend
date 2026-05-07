-- Insert Companies
INSERT INTO companies (company_id, name, logo, tax_code, website, email, phone, address, description, company_size, founded_year, status_company, created_at) VALUES
('COMP001', 'FPT Software', 'https://fpt.png', '0101248141', 'https://fpt.com', 'hr@fpt.com', '02437687077', 'Ha Noi', 'IT Services', 30000, 1999, 'VERIFIED', NOW()),
('COMP002', 'VNG Corp', 'https://vng.png', '0303215392', 'https://vng.com', 'hr@vng.com', '02839623888', 'HCM', 'Internet Services', 5000, 2004, 'VERIFIED', NOW()),
('COMP003', 'Viettel', 'https://viettel.png', '0100109106', 'https://viettel.vn', 'hr@viettel.vn', '18008098', 'Ha Noi', 'Telecom', 50000, 1989, 'VERIFIED', NOW()),
('COMP004', 'Momo', 'https://momo.png', '0305289153', 'https://momo.vn', 'hr@momo.vn', '02839151550', 'HCM', 'Fintech', 2000, 2007, 'VERIFIED', NOW()),
('COMP005', 'Tiki', 'https://tiki.png', '0309535990', 'https://tiki.vn', 'hr@tiki.vn', '19006035', 'HCM', 'E-commerce', 3000, 2010, 'VERIFIED', NOW()),
('COMP006', 'Shopee', 'https://shopee.png', '0313566133', 'https://shopee.vn', 'hr@shopee.vn', '19001221', 'HCM', 'E-commerce', 4000, 2015, 'VERIFIED', NOW()),
('COMP007', 'Grab', 'https://grab.png', '0312650437', 'https://grab.com', 'hr@grab.com', '02871087108', 'HCM', 'Ride-hailing', 1500, 2014, 'VERIFIED', NOW()),
('COMP008', 'Tek Expert', 'https://tek.png', '0107753177', 'https://tek.com', 'hr@tek.com', '02439561234', 'Ha Noi', 'Technical Support', 2000, 2011, 'VERIFIED', NOW()),
('COMP009', 'NashTech', 'https://nash.png', '0302014728', 'https://nashtech.com', 'hr@nash.com', '02838106200', 'HCM', 'Software', 2500, 2000, 'VERIFIED', NOW()),
('COMP010', 'Zalo', 'https://zalo.png', '0303215392-1', 'https://zalo.me', 'hr@zalo.me', '02839623888', 'HCM', 'Messaging', 1000, 2012, 'VERIFIED', NOW()),
('COMP011', 'Lazada', 'https://lazada.png', '0311226743', 'https://lazada.vn', 'hr@lazada.vn', '19001007', 'HCM', 'E-commerce', 2000, 2012, 'VERIFIED', NOW()),
('COMP012', 'VinFast', 'https://vinfast.png', '0105847076', 'https://vinfast.vn', 'hr@vinfast.vn', '1900232389', 'Hai Phong', 'Automotive', 10000, 2017, 'VERIFIED', NOW()),
('COMP013', 'Base.vn', 'https://base.png', '0107431268', 'https://base.vn', 'hr@base.vn', '02422466981', 'HCM', 'SaaS', 500, 2016, 'VERIFIED', NOW()),
('COMP014', 'KMS', 'https://kms.png', '0306325510', 'https://kms.com', 'hr@kms.com', '02838486000', 'HCM', 'Software', 1500, 2009, 'VERIFIED', NOW()),
('COMP015', 'NAB', 'https://nab.png', '0315754562', 'https://nab.com', 'hr@nab.com', '02836224000', 'HCM', 'Banking IT', 1000, 2019, 'VERIFIED', NOW()),
('COMP016', 'SmartDev', 'https://sd.png', '0401625341', 'https://smartdev.com', 'hr@sd.com', '02363888321', 'Da Nang', 'Software', 300, 2014, 'VERIFIED', NOW()),
('COMP017', 'Sun*', 'https://sun.png', '0105813350', 'https://sun.vn', 'hr@sun.vn', '02437955463', 'Ha Noi', 'Software', 2000, 2012, 'VERIFIED', NOW()),
('COMP018', 'Amanotes', 'https://ama.png', '0313137258', 'https://amanotes.com', 'hr@ama.com', '02862908231', 'HCM', 'Gaming', 200, 2014, 'VERIFIED', NOW()),
('COMP019', 'Be Group', 'https://be.png', '0315184852', 'https://be.vn', 'hr@be.vn', '1900232345', 'HCM', 'Ride-hailing', 1000, 2018, 'VERIFIED', NOW()),
('COMP020', 'OneMount', 'https://om.png', '0108906161', 'https://onemount.com', 'hr@om.com', '02432045555', 'Ha Noi', 'Ecosystem', 2000, 2019, 'VERIFIED', NOW())
ON CONFLICT (company_id) DO NOTHING;

-- Insert Verifications (Set half to APPROVED, half to PENDING)
INSERT INTO company_verifications (verification_id, company_id, status, submitted_tax_code, business_license, verified_by, submitted_at, verified_at, note) VALUES
('VER001', 'COMP001', 'APPROVED', '0101248141', 'license_fpt.pdf', 'system', NOW(), NOW(), 'Verified by system'),
('VER002', 'COMP002', 'APPROVED', '0303215392', 'license_vng.pdf', 'system', NOW(), NOW(), 'Verified by system'),
('VER003', 'COMP003', 'APPROVED', '0100109106', 'license_viettel.pdf', 'system', NOW(), NOW(), 'Verified by system'),
('VER004', 'COMP004', 'APPROVED', '0305289153', 'license_momo.pdf', 'system', NOW(), NOW(), 'Verified by system'),
('VER005', 'COMP005', 'APPROVED', '0309535990', 'license_tiki.pdf', 'system', NOW(), NOW(), 'Verified by system'),
('VER006', 'COMP006', 'APPROVED', '0313566133', 'license_shopee.pdf', 'system', NOW(), NOW(), 'Verified by system'),
('VER007', 'COMP007', 'APPROVED', '0312650437', 'license_grab.pdf', 'system', NOW(), NOW(), 'Verified by system'),
('VER008', 'COMP008', 'APPROVED', '0107753177', 'license_tek.pdf', 'system', NOW(), NOW(), 'Verified by system'),
('VER009', 'COMP009', 'APPROVED', '0302014728', 'license_nash.pdf', 'system', NOW(), NOW(), 'Verified by system'),
('VER010', 'COMP010', 'APPROVED', '0303215392-1', 'license_zalo.pdf', 'system', NOW(), NOW(), 'Verified by system'),
-- The following are PENDING for Admin to test approval flow
('VER011', 'COMP011', 'PENDING', '0311226743', 'license_lazada.pdf', NULL, NOW(), NULL, NULL),
('VER012', 'COMP012', 'PENDING', '0105847076', 'license_vinfast.pdf', NULL, NOW(), NULL, NULL),
('VER013', 'COMP013', 'PENDING', '0107431268', 'license_base.pdf', NULL, NOW(), NULL, NULL),
('VER014', 'COMP014', 'PENDING', '0306325510', 'license_kms.pdf', NULL, NOW(), NULL, NULL),
('VER015', 'COMP015', 'PENDING', '0315754562', 'license_nab.pdf', NULL, NOW(), NULL, NULL),
('VER016', 'COMP016', 'PENDING', '0401625341', 'license_smartdev.pdf', NULL, NOW(), NULL, NULL),
('VER017', 'COMP017', 'PENDING', '0105813350', 'license_sun.pdf', NULL, NOW(), NULL, NULL),
('VER018', 'COMP018', 'PENDING', '0313137258', 'license_amanotes.pdf', NULL, NOW(), NULL, NULL),
('VER019', 'COMP019', 'PENDING', '0315184852', 'license_be.pdf', NULL, NOW(), NULL, NULL),
('VER020', 'COMP020', 'PENDING', '0108906161', 'license_om.pdf', NULL, NOW(), NULL, NULL)
ON CONFLICT (verification_id) DO NOTHING;
