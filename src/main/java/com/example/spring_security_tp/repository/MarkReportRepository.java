package com.example.spring_security_tp.repository;

import com.example.spring_security_tp.model.MarkReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarkReportRepository extends JpaRepository<MarkReport, Long> {

    @Query("SELECT r FROM MarkReport r WHERE r.mark.student.id = :studentId")
    List<MarkReport> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT r FROM MarkReport r WHERE r.mark.teacher.id = :teacherId")
    List<MarkReport> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT r FROM MarkReport r WHERE r.mark.id = :markId AND r.status = 'WAITING'")
    List<MarkReport> findPendingByMarkId(@Param("markId") Long markId);
}
