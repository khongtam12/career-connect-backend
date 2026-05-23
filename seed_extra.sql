-- Company Subscriptions
INSERT INTO company_subscriptions (id, company_id, package_id, start_date, end_date, status, job_post_limit, job_posted_count, package_label) VALUES
('SUB001', 'COMP001', 'JP001', NOW(), NOW() + interval '30 days', 'ACTIVE', 10, 0, 'Basic Job'),
('SUB002', 'COMP002', 'JP002', NOW(), NOW() + interval '30 days', 'ACTIVE', 20, 0, 'Urgent Job')
ON CONFLICT (id) DO NOTHING;

-- Company Verifications
INSERT INTO company_verifications (verification_id, company_id, submitted_at, verified_at, note, status, submitted_tax_code, verified_by, business_license) VALUES
('VER001', 'COMP001', NOW() - interval '1 day', NOW(), 'Initial verification', 'APPROVED', '0101248141', 'ADM001', 'https://license.png')
ON CONFLICT (verification_id) DO NOTHING;

-- Active BRANDING entitlements (scope COMPANY) to test featured companies
INSERT INTO company_marketing_entitlements (
	id,
	company_id,
	payment_id,
	package_id,
	package_label,
	package_category,
	package_type,
	target_scope,
	usage_limit,
	used_count,
	quantity,
	duration_days,
	start_date,
	end_date,
	status
) VALUES
('ENTBR001', 'COMP001', 'PAYBR001', 'BR001', 'Logo thương hiệu', 'BRANDING', 'BRANDING_LOGO', 'COMPANY', 1, 1, 1, 28, NOW() - interval '2 days', NOW() + interval '26 days', 'ACTIVE'),
('ENTBR002', 'COMP003', 'PAYBR002', 'BR001', 'Logo thương hiệu', 'BRANDING', 'BRANDING_LOGO', 'COMPANY', 1, 1, 1, 28, NOW() - interval '5 days', NOW() + interval '23 days', 'ACTIVE'),
('ENTBR003', 'COMP005', 'PAYBR003', 'BR001', 'Logo thương hiệu', 'BRANDING', 'BRANDING_LOGO', 'COMPANY', 1, 1, 1, 28, NOW() - interval '1 day', NOW() + interval '27 days', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO company_marketing_assignments (
	id,
	entitlement_id,
	company_id,
	target_id,
	placement,
	package_id,
	package_label,
	package_category,
	package_type,
	target_scope,
	assigned_at,
	expires_at,
	status
) VALUES
('ASGBR001', 'ENTBR001', 'COMP001', 'COMP001', 'HOME_FEATURED_COMPANY', 'BR001', 'Logo thương hiệu', 'BRANDING', 'BRANDING_LOGO', 'COMPANY', NOW() - interval '2 days', NOW() + interval '26 days', 'ACTIVE'),
('ASGBR002', 'ENTBR002', 'COMP003', 'COMP003', 'HOME_FEATURED_COMPANY', 'BR001', 'Logo thương hiệu', 'BRANDING', 'BRANDING_LOGO', 'COMPANY', NOW() - interval '5 days', NOW() + interval '23 days', 'ACTIVE'),
('ASGBR003', 'ENTBR003', 'COMP005', 'COMP005', 'HOME_FEATURED_COMPANY', 'BR001', 'Logo thương hiệu', 'BRANDING', 'BRANDING_LOGO', 'COMPANY', NOW() - interval '1 day', NOW() + interval '27 days', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- Demo JOB marketing entitlements / assignments are stored in job-service DB
-- and are reflected directly from `seed_job.sql` updates.
