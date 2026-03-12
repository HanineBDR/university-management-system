
USE student_management_db;

CREATE TABLE IF NOT EXISTS mark_reports (
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