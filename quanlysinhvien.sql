-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th5 28, 2025 lúc 08:09 AM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `quanlysinhvien`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `department`
--

CREATE TABLE `department` (
  `department_id` int(11) NOT NULL,
  `department_code` varchar(10) DEFAULT NULL,
  `department_name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `department`
--

INSERT INTO `department` (`department_id`, `department_code`, `department_name`) VALUES
(1, 'CNTT', 'Khoa CNTT'),
(2, 'HTTT', 'Hệ thống thông tin'),
(5, 'CNPM', 'Cong Nghe Phan Mem'),
(6, 'MMT', 'Mang May Tinh');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `exam_schedule`
--

CREATE TABLE `exam_schedule` (
  `exam_schedule_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `subject_class_id` int(11) NOT NULL,
  `exam_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `exam_room` varchar(50) DEFAULT NULL,
  `semester` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `exam_schedule`
--

INSERT INTO `exam_schedule` (`exam_schedule_id`, `student_id`, `subject_class_id`, `exam_date`, `start_time`, `end_time`, `exam_room`, `semester`) VALUES
(1, 1, 1, '2025-06-15', '08:00:00', '10:00:00', 'P101', '2025HK1'),
(2, 2, 1, '2025-06-15', '08:00:00', '10:00:00', 'P101', '2025HK1'),
(3, 1, 2, '2025-06-20', '10:00:00', '12:00:00', 'P102', '2025HK1'),
(4, 2, 2, '2025-06-20', '10:00:00', '12:00:00', 'P102', '2025HK1');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `grade`
--

CREATE TABLE `grade` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `subject_class_id` int(11) NOT NULL,
  `process_grade` float DEFAULT NULL CHECK (`process_grade` >= 0 and `process_grade` <= 10),
  `practice_grade` float DEFAULT NULL CHECK (`practice_grade` >= 0 and `practice_grade` <= 10),
  `midterm_grade` float DEFAULT NULL CHECK (`midterm_grade` >= 0 and `midterm_grade` <= 10),
  `final_grade` float DEFAULT NULL CHECK (`final_grade` >= 0 and `final_grade` <= 10),
  `semester` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `grade`
--

INSERT INTO `grade` (`id`, `student_id`, `subject_class_id`, `process_grade`, `practice_grade`, `midterm_grade`, `final_grade`, `semester`) VALUES
(1, 1, 1, 8, 9, 7.5, 8.5, '2025HK1'),
(3, 1, 2, 9, 9.5, 8, 9, '2025HK1'),
(5, 2, 1, 10, 10, 10, 10, 'HK2 2024-2025'),
(6, 1, 1, 9, 9, 9, 9, 'HK2 2024-2025'),
(7, 2, 2, 10, 10, 10, 10, 'HK2 2024-2025'),
(8, 3, 1, 9, 9, 8, 8.5, 'HK2 2024-2025');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `program_class`
--

CREATE TABLE `program_class` (
  `program_class_id` int(11) NOT NULL,
  `program_class_code` varchar(20) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `year` int(11) NOT NULL,
  `department_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `program_class`
--

INSERT INTO `program_class` (`program_class_id`, `program_class_code`, `teacher_id`, `year`, `department_id`) VALUES
(1, 'CNTT2025A', 1, 2025, 1),
(2, 'CNTT2025B', 2, 2025, 1),
(3, 'HTTT2022.1', 1, 2022, 2),
(5, 'HTTT2024.1', 2, 2024, 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `student`
--

CREATE TABLE `student` (
  `student_id` int(11) NOT NULL,
  `student_code` varchar(8) NOT NULL,
  `user_id` int(11) NOT NULL,
  `student_full_name` varchar(100) NOT NULL,
  `program_class_id` int(11) NOT NULL,
  `date_of_birth` date DEFAULT NULL,
  `student_email` varchar(100) DEFAULT NULL,
  `student_address` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `student`
--

INSERT INTO `student` (`student_id`, `student_code`, `user_id`, `student_full_name`, `program_class_id`, `date_of_birth`, `student_email`, `student_address`) VALUES
(1, '22520226', 2, 'Nguyễn Tiến Đạt', 1, '2004-03-10', 'datnt@gmail.com', '123 Lê Văn Việt, TP. Thủ Đức'),
(2, '22520780', 3, 'Linh Vu', 3, '2004-07-20', 'linhvnd@gmail.com', '456 Xa lộ Hà Nội, TP. Thủ Đức'),
(3, '22520454', 6, 'Nguyen Van Hoa', 3, '2004-05-27', '22520454@gm.uit.edu.vn', 'Kien Giang'),
(4, '22520756', 9, 'Dang Quang Khanh Linh', 3, '2025-05-28', '22520756@gm.uit.edu.vn', 'Dong Nai');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `student_enrollment`
--

CREATE TABLE `student_enrollment` (
  `enrollment_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `subject_class_id` int(11) NOT NULL,
  `enrollment_date` datetime DEFAULT current_timestamp(),
  `status` enum('active','dropped','completed') DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `student_enrollment`
--

INSERT INTO `student_enrollment` (`enrollment_id`, `student_id`, `subject_class_id`, `enrollment_date`, `status`) VALUES
(1, 1, 1, '2025-05-28 12:41:55', 'active'),
(2, 1, 2, '2025-05-28 12:41:55', 'active'),
(3, 2, 1, '2025-05-28 12:41:55', 'active'),
(4, 2, 2, '2025-05-28 12:41:55', 'active'),
(5, 3, 1, '2025-05-28 12:41:55', 'active'),
(7, 4, 1, '2025-05-28 12:48:55', 'active');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `subject`
--

CREATE TABLE `subject` (
  `subject_id` int(11) NOT NULL,
  `subject_code` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `department_id` int(11) DEFAULT NULL,
  `credits` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `subject`
--

INSERT INTO `subject` (`subject_id`, `subject_code`, `name`, `department_id`, `credits`) VALUES
(1, 'MB101', 'Lập trình mobile', 6, 3),
(2, 'DA101', 'Phân tích dữ liệu', 2, 3),
(3, 'IS405.P23', 'Cơ sở dữ liệu phân tán', 2, 4),
(4, 'IS405', 'Dữ liệu lớn', 2, 4);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `subject_class`
--

CREATE TABLE `subject_class` (
  `subject_class_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `subject_class_code` varchar(20) NOT NULL,
  `semester` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `subject_class`
--

INSERT INTO `subject_class` (`subject_class_id`, `subject_id`, `teacher_id`, `subject_class_code`, `semester`) VALUES
(1, 1, 1, 'MB101A', '2025HK1'),
(2, 2, 2, 'DA101A', '2025HK1');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `teacher`
--

CREATE TABLE `teacher` (
  `teacher_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `teacher_full_name` varchar(100) NOT NULL,
  `teacher_email` varchar(100) NOT NULL,
  `department_id` int(11) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `teacher`
--

INSERT INTO `teacher` (`teacher_id`, `user_id`, `teacher_full_name`, `teacher_email`, `department_id`, `date_of_birth`) VALUES
(1, 4, 'Dương Phi Long', 'long@university.edu', 2, '1980-05-15'),
(2, 5, 'Nguyễn Minh Nhựt', 'nhut@university.edu', 2, '1985-09-22');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `timetable`
--

CREATE TABLE `timetable` (
  `timetable_id` int(11) NOT NULL,
  `subject_class_id` int(11) NOT NULL,
  `day_of_week` tinyint(4) NOT NULL,
  `period` int(11) NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `timetable`
--

INSERT INTO `timetable` (`timetable_id`, `subject_class_id`, `day_of_week`, `period`, `start_time`, `end_time`) VALUES
(1, 1, 2, 2, '08:00:00', '09:50:00'),
(2, 2, 2, 5, '10:00:00', '11:50:00'),
(3, 1, 3, 3, '07:31:00', '10:05:00');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `user`
--

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','student','teacher') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `user`
--

INSERT INTO `user` (`user_id`, `username`, `password`, `role`) VALUES
(1, 'admin1', 'admin123', 'admin'),
(2, 'datnt', 'password_hash', 'student'),
(3, 'linhvnd', 'password_hash', 'student'),
(4, 'longdp', 'password_hash', 'teacher'),
(5, 'nhutnm', 'password_hash', 'teacher'),
(6, 'nvhoa', '$2y$10$TgqvWBuP933vFwO.izF/k.sjzGlXcx3Q8neZHJfyJPfdZw3NJ0JCi', 'student'),
(9, 'linhdqk', '$2y$10$Iawg9dJ9lsC2FlCaqpwplOFLk4mbLxJOIeyCQ.0ePIGPKErXG/vXu', 'student');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `department`
--
ALTER TABLE `department`
  ADD PRIMARY KEY (`department_id`),
  ADD UNIQUE KEY `department_name` (`department_name`),
  ADD UNIQUE KEY `uk_department_code` (`department_code`);

--
-- Chỉ mục cho bảng `exam_schedule`
--
ALTER TABLE `exam_schedule`
  ADD PRIMARY KEY (`exam_schedule_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `subject_class_id` (`subject_class_id`);

--
-- Chỉ mục cho bảng `grade`
--
ALTER TABLE `grade`
  ADD PRIMARY KEY (`id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `subject_class_id` (`subject_class_id`);

--
-- Chỉ mục cho bảng `program_class`
--
ALTER TABLE `program_class`
  ADD PRIMARY KEY (`program_class_id`),
  ADD UNIQUE KEY `program_class_code` (`program_class_code`),
  ADD KEY `department_id` (`department_id`),
  ADD KEY `teacher_id` (`teacher_id`);

--
-- Chỉ mục cho bảng `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`student_id`),
  ADD UNIQUE KEY `student_code` (`student_code`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `program_class_id` (`program_class_id`);

--
-- Chỉ mục cho bảng `student_enrollment`
--
ALTER TABLE `student_enrollment`
  ADD PRIMARY KEY (`enrollment_id`),
  ADD UNIQUE KEY `unique_student_subject_class` (`student_id`,`subject_class_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `subject_class_id` (`subject_class_id`);

--
-- Chỉ mục cho bảng `subject`
--
ALTER TABLE `subject`
  ADD PRIMARY KEY (`subject_id`),
  ADD UNIQUE KEY `subject_code` (`subject_code`),
  ADD KEY `department_id` (`department_id`);

--
-- Chỉ mục cho bảng `subject_class`
--
ALTER TABLE `subject_class`
  ADD PRIMARY KEY (`subject_class_id`),
  ADD UNIQUE KEY `subject_class_code` (`subject_class_code`),
  ADD KEY `subject_id` (`subject_id`),
  ADD KEY `teacher_id` (`teacher_id`);

--
-- Chỉ mục cho bảng `teacher`
--
ALTER TABLE `teacher`
  ADD PRIMARY KEY (`teacher_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `fk_teacher_department` (`department_id`);

--
-- Chỉ mục cho bảng `timetable`
--
ALTER TABLE `timetable`
  ADD PRIMARY KEY (`timetable_id`),
  ADD KEY `subject_class_id` (`subject_class_id`);

--
-- Chỉ mục cho bảng `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `department`
--
ALTER TABLE `department`
  MODIFY `department_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `exam_schedule`
--
ALTER TABLE `exam_schedule`
  MODIFY `exam_schedule_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `grade`
--
ALTER TABLE `grade`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT cho bảng `program_class`
--
ALTER TABLE `program_class`
  MODIFY `program_class_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT cho bảng `student`
--
ALTER TABLE `student`
  MODIFY `student_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `student_enrollment`
--
ALTER TABLE `student_enrollment`
  MODIFY `enrollment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT cho bảng `subject`
--
ALTER TABLE `subject`
  MODIFY `subject_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `subject_class`
--
ALTER TABLE `subject_class`
  MODIFY `subject_class_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `teacher`
--
ALTER TABLE `teacher`
  MODIFY `teacher_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `timetable`
--
ALTER TABLE `timetable`
  MODIFY `timetable_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `exam_schedule`
--
ALTER TABLE `exam_schedule`
  ADD CONSTRAINT `exam_schedule_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `exam_schedule_ibfk_2` FOREIGN KEY (`subject_class_id`) REFERENCES `subject_class` (`subject_class_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `grade`
--
ALTER TABLE `grade`
  ADD CONSTRAINT `grade_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `grade_ibfk_2` FOREIGN KEY (`subject_class_id`) REFERENCES `subject_class` (`subject_class_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `program_class`
--
ALTER TABLE `program_class`
  ADD CONSTRAINT `program_class_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `program_class_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`teacher_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `student`
--
ALTER TABLE `student`
  ADD CONSTRAINT `student_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `student_ibfk_2` FOREIGN KEY (`program_class_id`) REFERENCES `program_class` (`program_class_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `student_enrollment`
--
ALTER TABLE `student_enrollment`
  ADD CONSTRAINT `student_enrollment_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `student_enrollment_ibfk_2` FOREIGN KEY (`subject_class_id`) REFERENCES `subject_class` (`subject_class_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `subject`
--
ALTER TABLE `subject`
  ADD CONSTRAINT `subject_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `subject_class`
--
ALTER TABLE `subject_class`
  ADD CONSTRAINT `subject_class_ibfk_1` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`subject_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `subject_class_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`teacher_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `teacher`
--
ALTER TABLE `teacher`
  ADD CONSTRAINT `fk_teacher_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `teacher_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `timetable`
--
ALTER TABLE `timetable`
  ADD CONSTRAINT `timetable_ibfk_1` FOREIGN KEY (`subject_class_id`) REFERENCES `subject_class` (`subject_class_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
