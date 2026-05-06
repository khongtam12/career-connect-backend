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
('PT001', '', '', 'POINTS', 'Gói 100 điểm', 90, 'https://via.placeholder.com/100x60?text=Points', true, 4, 'Point Service', NULL, 2820000, false, 'POINT_SERVICE')
ON CONFLICT (package_id) DO NOTHING;

-- jobpackage_box_types
INSERT INTO jobpackage_box_types (package_id, box_type) VALUES
('EF001','TRANG_CHU'),('EF001','TUYEN_GAP'),('EF001','UU_TIEN'),('EF001','NGANH'),
('EF002','TRANG_CHU'),('EF002','UU_TIEN'),('EF003','NGANH'),
('HP001','TRANG_CHU'),('HP002','NGANH')
ON CONFLICT DO NOTHING;
