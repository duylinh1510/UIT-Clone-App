-- Thêm cột department_code vào bảng department
ALTER TABLE department ADD COLUMN department_code VARCHAR(10) NULL AFTER department_id;

-- Thêm unique constraint
ALTER TABLE department ADD CONSTRAINT uk_department_code UNIQUE (department_code);

-- Cập nhật mã khoa cho các department hiện có
UPDATE department SET department_code = 'CNTT' WHERE department_name LIKE '%CNTT%' OR department_name LIKE '%Công nghệ thông tin%';
UPDATE department SET department_code = 'HTTT' WHERE department_name LIKE '%Hệ thống thông tin%';
UPDATE department SET department_code = 'KTDT' WHERE department_name LIKE '%điện%' AND department_name NOT LIKE '%Hệ thống%';
UPDATE department SET department_code = 'KTCK' WHERE department_name LIKE '%cơ khí%';
UPDATE department SET department_code = 'QTKD' WHERE department_name LIKE '%quản trị%';
UPDATE department SET department_code = 'KTXD' WHERE department_name LIKE '%dân dụng%' OR department_name LIKE '%xây dựng%'; 