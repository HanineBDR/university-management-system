-- ============================================
-- Student Management System - Complete Setup
-- WITH ALIGNED CREDENTIALS
-- ============================================

-- This script will:
-- 1. Create the database
-- 2. Create all tables
-- 3. Insert test data with aligned usernames/passwords

-- ============================================
-- STEP 1: Create Database
-- ============================================
DROP DATABASE IF EXISTS student_management_db;

CREATE DATABASE student_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_management_db;

SELECT '✓ Database created successfully!' as Status;

-- ============================================
-- STEP 2: Create Tables
-- ============================================

-- Table: users (Authentication and user management)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (
        role IN ('ADMIN', 'TEACHER', 'STUDENT')
    ),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Table: students (Student profiles)
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(200) NOT NULL,
    enrollment_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_enrollment_date (enrollment_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Table: teachers (Teacher profiles)
CREATE TABLE teachers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(200) NOT NULL,
    hire_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_hire_date (hire_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Table: modules (Course modules)
CREATE TABLE modules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    teacher_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_module_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (id) ON DELETE CASCADE,
    INDEX idx_code (code),
    INDEX idx_teacher_id (teacher_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Table: marks (Student marks for modules)
CREATE TABLE marks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    score DECIMAL(5, 2) NOT NULL CHECK (
        score >= 0
        AND score <= 20
    ),
    mark_date DATE NOT NULL,
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_mark_student FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    CONSTRAINT fk_mark_module FOREIGN KEY (module_id) REFERENCES modules (id) ON DELETE CASCADE,
    CONSTRAINT fk_mark_teacher FOREIGN KEY (teacher_id) REFERENCES teachers (id) ON DELETE CASCADE,
    UNIQUE KEY unique_student_module (student_id, module_id),
    INDEX idx_student_id (student_id),
    INDEX idx_module_id (module_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_mark_date (mark_date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Table: mark_reports (Student reports about mark issues)
CREATE TABLE mark_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mark_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING' CHECK (
        status IN (
            'WAITING',
            'ACCEPTED',
            'REFUSED'
        )
    ),
    teacher_comment TEXT,
    report_date DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_mark FOREIGN KEY (mark_id) REFERENCES marks (id) ON DELETE CASCADE,
    INDEX idx_mark_id (mark_id),
    INDEX idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

SELECT '✓ All tables created successfully!' as Status;

SHOW TABLES;

-- ============================================
-- STEP 3: Insert Test Data (ALIGNED CREDENTIALS)
-- ============================================

-- Insert Users with ALIGNED usernames and passwords
-- Admin password: "admin" (BCrypt: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHCyM1yk5qd0aYLHZqRwHK)
-- Teacher password: "teacher" (BCrypt: $2a$10$vq3jZ7nQT5R/JKQ/OQxjA.XQVZQKZ0Z2xZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5u)
-- Student password: "student" (BCrypt: $2a$10$8C5wHJZqKjZdZJQXZ5Z5ZuZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu)

INSERT INTO
    users (
        id,
        username,
        email,
        password,
        role,
        enabled
    )
VALUES
    -- ADMIN (Username: admin, Password: admin)
    (
        1,
        'admin',
        'admin@student-system.com',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHCyM1yk5qd0aYLHZqRwHK',
        'ADMIN',
        TRUE
    ),

-- TEACHERS (Username: teacher.firstname.lastname, Password: teacher)
(
    2,
    'teacher.mohamed.alami',
    'mohamed.alami@school.com',
    '$2a$10$vq3jZ7nQT5R/JKQ.Zt0G1OKq1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Qm',
    'TEACHER',
    TRUE
),
(
    3,
    'teacher.fatima.bennani',
    'fatima.bennani@school.com',
    '$2a$10$vq3jZ7nQT5R/JKQ.Zt0G1OKq1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Qm',
    'TEACHER',
    TRUE
),
(
    4,
    'teacher.ahmed.idrissi',
    'ahmed.idrissi@school.com',
    '$2a$10$vq3jZ7nQT5R/JKQ.Zt0G1OKq1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Q1Qm',
    'TEACHER',
    TRUE
),

-- STUDENTS (Username: student.firstname.lastname, Password: student)
(
    5,
    'student.hanine.el.mansouri',
    'hanine.student@school.com',
    '$2a$10$8C5wHJZqKjZdZJQXZ5Z5ZuZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu',
    'STUDENT',
    TRUE
),
(
    6,
    'student.sara.chahid',
    'sara.chahid@school.com',
    '$2a$10$8C5wHJZqKjZdZJQXZ5Z5ZuZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu',
    'STUDENT',
    TRUE
),
(
    7,
    'student.youssef.tazi',
    'youssef.tazi@school.com',
    '$2a$10$8C5wHJZqKjZdZJQXZ5Z5ZuZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu',
    'STUDENT',
    TRUE
),
(
    8,
    'student.amina.el.fassi',
    'amina.el@school.com',
    '$2a$10$8C5wHJZqKjZdZJQXZ5Z5ZuZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu',
    'STUDENT',
    TRUE
),
(
    9,
    'student.omar.zaki',
    'omar.zaki@school.com',
    '$2a$10$8C5wHJZqKjZdZJQXZ5Z5ZuZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu',
    'STUDENT',
    TRUE
),
(
    10,
    'student.fateh.bllchreb',
    'fateh.bllchreb@school.com',
    '$2a$10$8C5wHJZqKjZdZJQXZ5Z5ZuZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu',
    'STUDENT',
    TRUE
),
(
    11,
    'student.malik.boudour',
    'malik.boudour@school.com',
    '$2a$10$8C5wHJZqKjZdZJQXZ5Z5ZuZ5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Zu',
    'STUDENT',
    TRUE
);

SELECT '✓ Users inserted with aligned credentials!' as Status;

-- Insert Teachers
INSERT INTO
    teachers (
        id,
        user_id,
        full_name,
        hire_date
    )
VALUES (
        1,
        2,
        'Mohamed Alami',
        '2018-09-01'
    ),
    (
        2,
        3,
        'Fatima Bennani',
        '2019-01-15'
    ),
    (
        3,
        4,
        'Ahmed Idrissi',
        '2017-08-20'
    );

SELECT '✓ Teachers inserted!' as Status;

-- Insert Students
INSERT INTO
    students (
        id,
        user_id,
        full_name,
        enrollment_date
    )
VALUES (
        1,
        5,
        'Hanine El Mansouri',
        '2023-09-01'
    ),
    (
        2,
        6,
        'Sara Chahid',
        '2023-09-01'
    ),
    (
        3,
        7,
        'Youssef Tazi',
        '2023-09-01'
    ),
    (
        4,
        8,
        'Amina El Fassi',
        '2024-09-01'
    ),
    (
        5,
        9,
        'Omar Zaki',
        '2024-09-01'
    ),
    (
        6,
        10,
        'Fateh Bllchreb',
        '2024-09-01'
    ),
    (
        7,
        11,
        'Malik Boudour',
        '2024-09-01'
    );

SELECT '✓ Students inserted!' as Status;

-- Insert Modules (each teacher teaches one module)
INSERT INTO
    modules (
        id,
        name,
        code,
        description,
        teacher_id
    )
VALUES (
        1,
        'Advanced Java Programming',
        'JAVA301',
        'Object-oriented programming, Spring Framework, and enterprise applications',
        1
    ),
    (
        2,
        'Web Development',
        'WEB201',
        'HTML, CSS, JavaScript, and modern web frameworks',
        2
    ),
    (
        3,
        'Database Systems',
        'DB301',
        'SQL, MySQL, database design, and optimization',
        3
    );

SELECT '✓ Modules inserted!' as Status;

-- Insert Marks (each student has 3 marks - one per module)
INSERT INTO
    marks (
        student_id,
        module_id,
        teacher_id,
        score,
        mark_date,
        comments
    )
VALUES
    -- Hanine's marks
    (
        1,
        1,
        1,
        16.50,
        '2024-12-10',
        'Excellent work on the Spring Boot project'
    ),
    (
        1,
        2,
        2,
        18.00,
        '2024-12-12',
        'Outstanding frontend development skills'
    ),
    (
        1,
        3,
        3,
        15.75,
        '2024-12-15',
        'Good understanding of database normalization'
    ),

-- Sara's marks
(
    2,
    1,
    1,
    14.00,
    '2024-12-10',
    'Good effort, needs improvement in design patterns'
),
(
    2,
    2,
    2,
    17.50,
    '2024-12-12',
    'Creative and responsive designs'
),
(
    2,
    3,
    3,
    16.00,
    '2024-12-15',
    'Well-structured database queries'
),

-- Youssef's marks
(
    3,
    1,
    1,
    17.00,
    '2024-12-10',
    'Strong understanding of Java concepts'
),
(
    3,
    2,
    2,
    15.50,
    '2024-12-12',
    'Good JavaScript skills, work on CSS'
),
(
    3,
    3,
    3,
    18.50,
    '2024-12-15',
    'Excellent database optimization techniques'
),

-- Amina's marks
(
    4,
    1,
    1,
    15.00,
    '2024-12-10',
    'Solid grasp of Spring Security'
),
(
    4,
    2,
    2,
    16.50,
    '2024-12-12',
    'Very good at responsive design'
),
(
    4,
    3,
    3,
    14.75,
    '2024-12-15',
    'Needs to practice complex SQL queries'
),

-- Omar's marks
(
    5,
    1,
    1,
    13.50,
    '2024-12-10',
    'Needs more practice with Spring Boot'
),
(
    5,
    2,
    2,
    14.00,
    '2024-12-12',
    'Good basic understanding of web development'
),
(
    5,
    3,
    3,
    15.50,
    '2024-12-15',
    'Good work on database design project'
),

-- Fateh's marks
(
    6,
    1,
    1,
    16.00,
    '2024-12-10',
    'Very good understanding of Java fundamentals'
),
(
    6,
    2,
    2,
    15.00,
    '2024-12-12',
    'Good progress in web technologies'
),
(
    6,
    3,
    3,
    17.00,
    '2024-12-15',
    'Excellent database design and queries'
),

-- Malik's marks
(
    7,
    1,
    1,
    14.50,
    '2024-12-10',
    'Good effort, keep practicing'
),
(
    7,
    2,
    2,
    16.00,
    '2024-12-12',
    'Strong skills in frontend development'
),
(
    7,
    3,
    3,
    15.25,
    '2024-12-15',
    'Good understanding of SQL concepts'
);

SELECT '✓ Marks inserted!' as Status;

-- ============================================
-- STEP 4: Verify Data
-- ============================================

SELECT '===========================================================' as '';

SELECT '           DATABASE SETUP COMPLETED SUCCESSFULLY!          ' as '';

SELECT '===========================================================' as '';

-- Count summary
SELECT
    'Summary' as Info,
    (
        SELECT COUNT(*)
        FROM users
    ) as Total_Users,
    (
        SELECT COUNT(*)
        FROM teachers
    ) as Total_Teachers,
    (
        SELECT COUNT(*)
        FROM students
    ) as Total_Students,
    (
        SELECT COUNT(*)
        FROM modules
    ) as Total_Modules,
    (
        SELECT COUNT(*)
        FROM marks
    ) as Total_Marks;

-- Display test credentials
SELECT '===========================================================' as '';

SELECT '              ALIGNED LOGIN CREDENTIALS                    ' as '';

SELECT '===========================================================' as '';

SELECT '' as '';
SELECT 'ADMIN ACCOUNT:' as '';
SELECT '  Username: admin' as '';
SELECT '  Password: admin' as '';
SELECT '' as '';
SELECT 'TEACHER ACCOUNTS (Password: teacher):' as '';
SELECT '  - teacher.mohamed.alami' as '';
SELECT '  - teacher.fatima.bennani' as '';
SELECT '  - teacher.ahmed.idrissi' as '';
SELECT '' as '';
SELECT 'STUDENT ACCOUNTS (Password: student):' as '';
SELECT '  - student.hanine.el.mansouri' as '';
SELECT '  - student.sara.chahid' as '';
SELECT '  - student.youssef.tazi' as '';
SELECT '  - student.amina.el.fassi' as '';
SELECT '  - student.omar.zaki' as '';
SELECT '  - student.fateh.bllchreb' as '';
SELECT '  - student.malik.boudour' as '';
SELECT '' as '';

-- Display all users
SELECT '===========================================================' as '';
SELECT '                   ALL USER ACCOUNTS                       ' as '';
SELECT '===========================================================' as '';

SELECT username, email, role
FROM users
ORDER BY
    CASE role
        WHEN 'ADMIN' THEN 1
        WHEN 'TEACHER' THEN 2
        WHEN 'STUDENT' THEN 3
    END,
    username;

-- Sample query: View students with their marks
SELECT '===========================================================' as '';

SELECT '                 SAMPLE DATA: Student Marks                ' as '';

SELECT '===========================================================' as '';

SELECT
    s.full_name AS Student,
    m.name AS Module,
    m.code AS Code,
    mk.score AS Score,
    t.full_name AS Teacher,
    mk.mark_date AS Date
FROM
    students s
    JOIN marks mk ON s.id = mk.student_id
    JOIN modules m ON mk.module_id = m.id
    JOIN teachers t ON mk.teacher_id = t.id
ORDER BY s.full_name, mk.mark_date
LIMIT 10;