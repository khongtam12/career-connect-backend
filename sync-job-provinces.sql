\connect "job-service"

DO $$
DECLARE
    has_province boolean;
    has_location boolean;
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'jobs'
          AND column_name = 'province'
    ) INTO has_province;

    SELECT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'jobs'
          AND column_name = 'location'
    ) INTO has_location;

    IF has_province AND has_location THEN
        EXECUTE $upd$
            UPDATE jobs j
            SET
                province = CASE
                    WHEN src.source IS NULL THEN NULL
                    WHEN src.source ILIKE '%ha noi%' OR src.source ILIKE '%hà nội%' OR src.source ILIKE 'hn' THEN 'Thành phố Hà Nội'
                    WHEN src.source ILIKE '%hcm%' OR src.source ILIKE '%ho chi minh%' OR src.source ILIKE '%hồ chí minh%' OR src.source ILIKE '%sai gon%' THEN 'Thành phố Hồ Chí Minh'
                    WHEN src.source ILIKE '%hai phong%' OR src.source ILIKE '%hải phòng%' THEN 'Thành phố Hải Phòng'
                    WHEN src.source ILIKE '%da nang%' OR src.source ILIKE '%đà nẵng%' THEN 'Thành phố Đà Nẵng'
                    WHEN src.source ILIKE '%hue%' OR src.source ILIKE '%huế%' OR src.source ILIKE '%thua thien hue%' THEN 'Thành phố Huế'
                    WHEN src.source ILIKE '%can tho%' OR src.source ILIKE '%cần thơ%' THEN 'Thành phố Cần Thơ'
                    ELSE src.source
                END,
                location = CASE
                    WHEN src.source IS NULL THEN NULL
                    WHEN src.source ILIKE '%ha noi%' OR src.source ILIKE '%hà nội%' OR src.source ILIKE 'hn' THEN 'Thành phố Hà Nội'
                    WHEN src.source ILIKE '%hcm%' OR src.source ILIKE '%ho chi minh%' OR src.source ILIKE '%hồ chí minh%' OR src.source ILIKE '%sai gon%' THEN 'Thành phố Hồ Chí Minh'
                    WHEN src.source ILIKE '%hai phong%' OR src.source ILIKE '%hải phòng%' THEN 'Thành phố Hải Phòng'
                    WHEN src.source ILIKE '%da nang%' OR src.source ILIKE '%đà nẵng%' THEN 'Thành phố Đà Nẵng'
                    WHEN src.source ILIKE '%hue%' OR src.source ILIKE '%huế%' OR src.source ILIKE '%thua thien hue%' THEN 'Thành phố Huế'
                    WHEN src.source ILIKE '%can tho%' OR src.source ILIKE '%cần thơ%' THEN 'Thành phố Cần Thơ'
                    ELSE src.source
                END
            FROM (
                SELECT job_id, COALESCE(NULLIF(BTRIM(province), ''), NULLIF(BTRIM(location), '')) AS source
                FROM jobs
            ) src
            WHERE j.job_id = src.job_id
              AND src.source IS NOT NULL
        $upd$;
    END IF;
END $$;