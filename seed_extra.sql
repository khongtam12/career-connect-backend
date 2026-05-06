-- Company Subscriptions
INSERT INTO company_subscriptions (id, company_id, package_id, start_date, end_date, status, job_post_limit, job_posted_count, package_label) VALUES
('SUB001', 'COMP001', 'JP001', NOW(), NOW() + interval '30 days', 'ACTIVE', 10, 0, 'Basic Job'),
('SUB002', 'COMP002', 'JP002', NOW(), NOW() + interval '30 days', 'ACTIVE', 20, 0, 'Urgent Job')
ON CONFLICT (id) DO NOTHING;

-- Company Verifications
INSERT INTO company_verifications (verification_id, company_id, submitted_at, verified_at, note, status, submitted_tax_code, verified_by, business_license) VALUES
('VER001', 'COMP001', NOW() - interval '1 day', NOW(), 'Initial verification', 'APPROVED', '0101248141', 'ADM001', 'https://license.png')
ON CONFLICT (verification_id) DO NOTHING;
