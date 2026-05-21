-- Khởi tạo các database cho từng Microservice
CREATE DATABASE "user-service";
CREATE DATABASE "job-service";
CREATE DATABASE "company-service";
CREATE DATABASE "application-service";
CREATE DATABASE "payment-service";
CREATE DATABASE "cv-service";

-- Cấp quyền (tùy chọn)
GRANT ALL PRIVILEGES ON DATABASE "user-service" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "job-service" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "company-service" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "application-service" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "payment-service" TO postgres;
GRANT ALL PRIVILEGES ON DATABASE "cv-service" TO postgres;
