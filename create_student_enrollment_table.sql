-- Tạo bảng student_enrollment để quản lý sinh viên đăng ký học các môn
CREATE TABLE `student_enrollment` (
  `enrollment_id` int(11) NOT NULL AUTO_INCREMENT,
  `student_id` int(11) NOT NULL,
  `subject_class_id` int(11) NOT NULL,
  `enrollment_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` enum('active','dropped','completed') DEFAULT 'active',
  PRIMARY KEY (`enrollment_id`),
  UNIQUE KEY `unique_student_subject_class` (`student_id`, `subject_class_id`),
  KEY `student_id` (`student_id`),
  KEY `subject_class_id` (`subject_class_id`),
  CONSTRAINT `student_enrollment_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `student_enrollment_ibfk_2` FOREIGN KEY (`subject_class_id`) REFERENCES `subject_class` (`subject_class_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Thêm dữ liệu mẫu
INSERT INTO `student_enrollment` (`student_id`, `subject_class_id`, `status`) VALUES
(1, 1, 'active'), -- Nguyễn Tiến Đạt học MB101A
(1, 2, 'active'), -- Nguyễn Tiến Đạt học DA101A
(2, 1, 'active'), -- Linh Vu học MB101A
(2, 2, 'active'), -- Linh Vu học DA101A
(3, 1, 'active'); -- Nguyen Van Hoa học MB101A 