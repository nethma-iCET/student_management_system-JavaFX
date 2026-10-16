-- =======================================================
-- Student Management System - Database Version 2 (v2)
-- =======================================================

CREATE DATABASE IF NOT EXISTS student_management_system_v2;
USE student_management_system_v2;


CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS batches (
    batch_id VARCHAR(20) PRIMARY KEY,
    batch_name VARCHAR(50) NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS lecture_modes (
    mode_id VARCHAR(10) PRIMARY KEY,
    mode_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(30) PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    nic VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    birthday DATE NOT NULL,
    batch VARCHAR(50) NOT NULL,
    lecture_mode VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subjects (
    subject_id VARCHAR(20) PRIMARY KEY,
    subject_name VARCHAR(100) NOT NULL,
    credit_hours INT DEFAULT 3,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS marks (
    mark_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(30) NOT NULL,
    subject_id VARCHAR(20) NOT NULL,
    marks DECIMAL(5, 2) NOT NULL,
    exam_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE ON UPDATE CASCADE
);


INSERT INTO users (username, password)
VALUES ('admin', 'admin123')
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO batches (batch_id, batch_name, course_name)
VALUES
    ('110', 'Batch 110', 'BSc (Hons) Software Engineering'),
    ('111', 'Batch 111', 'BSc (Hons) Computer Science'),
    ('112', 'Batch 112', 'BSc (Hons) Computer Science'),
    ('120', 'Batch 120', 'Diploma in Software Engineering'),
    ('121', 'Batch 121', 'Diploma in Information Technology'),
    ('122', 'Batch 122', 'Diploma in Information Technology'),
    ('iCM120', 'Batch iCM120', 'Certificate in Management'),
    ('iCD121', 'Batch iCD121', 'Certificate in Software Development'),
    ('iCD122', 'Batch iCD122', 'Certificate in Software Development'),
    ('iCM123', 'Batch iCM123', 'Certificate in Management'),
    ('iCD124', 'Batch iCD124', 'Certificate in Software Development'),
    ('iCD125', 'Batch iCD125', 'Certificate in Software Development')
ON DUPLICATE KEY UPDATE batch_name = batch_name;

INSERT INTO lecture_modes (mode_id, mode_name)
VALUES
    ('PR', 'Physical'),
    ('OL', 'Online')
ON DUPLICATE KEY UPDATE mode_name = mode_name;

INSERT INTO subjects (subject_id, subject_name, credit_hours)
VALUES
    ('SUB-PRF', 'Programming Fundamentals (PRF)', 4),
    ('SUB-DBMS', 'Database Management Systems (DBMS)', 4),
    ('SUB-OOP', 'Object Oriented Programming (OOP)', 4)
ON DUPLICATE KEY UPDATE subject_name = subject_name;

INSERT INTO students (student_id, student_name, nic, email, birthday, batch, lecture_mode, address)
VALUES
    ('PR26110001', 'Kavindu Sandaruwan', '200223456789', 'kavindu.sandaruwan@gmail.com', '2002-08-14', '110', 'PR', 'No. 45, Temple Road, Maharagama, Sri Lanka'),
    ('PR26110002', 'Nimal Perera', '200134567890', 'nimal.perera@gmail.com', '2001-05-10', '110', 'PR', 'No. 12, Galle Road, Colombo, Sri Lanka'),
    ('PR26110003', 'Sunil Shantha', '200045678901', 'sunil.shantha@gmail.com', '2000-11-20', '110', 'PR', 'No. 88, Kandy Road, Kiribathgoda, Sri Lanka')
ON DUPLICATE KEY UPDATE student_name = student_name;

INSERT INTO marks (student_id, subject_id, marks, exam_date)
VALUES
    ('PR26110001', 'SUB-PRF', 85.50, '2026-06-15'),
    ('PR26110001', 'SUB-DBMS', 92.00, '2026-06-18'),
    ('PR26110002', 'SUB-PRF', 78.00, '2026-06-15'),
    ('PR26110002', 'SUB-DBMS', 88.50, '2026-06-18'),
    ('PR26110003', 'SUB-PRF', 90.00, '2026-06-15'),
    ('PR26110003', 'SUB-DBMS', 84.00, '2026-06-18');
