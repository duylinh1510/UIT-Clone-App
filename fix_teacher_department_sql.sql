-- Thêm cột department_id vào bảng teacher
ALTER TABLE teacher ADD COLUMN department_id INT(11) NULL AFTER teacher_email;

-- Thêm foreign key constraint
ALTER TABLE teacher ADD CONSTRAINT fk_teacher_department 
FOREIGN KEY (department_id) REFERENCES department(department_id) 
ON DELETE SET NULL ON UPDATE CASCADE;

-- Cập nhật dữ liệu cho các teacher hiện tại
-- Teacher 1: Dương Phi Long -> Khoa CNTT (id=1)
UPDATE teacher SET department_id = 1 WHERE teacher_id = 1;

-- Teacher 2: Nguyễn Minh Nhựt -> Khoa CNTT (id=1) 
UPDATE teacher SET department_id = 1 WHERE teacher_id = 2;

-- Teacher 3: Thai Bao Tran -> Hệ thống thông tin (id=2)
UPDATE teacher SET department_id = 2 WHERE teacher_id = 3; 