package com.example.spring_security_tp.service;

import com.example.spring_security_tp.model.MarkReport;
import com.example.spring_security_tp.repository.MarkReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MarkReportService {

    @Autowired
    private MarkReportRepository markReportRepository;

    public MarkReport createReport(MarkReport report) {
        report.setStatus(MarkReport.ReportStatus.WAITING);
        report.setReportDate(LocalDateTime.now());
        return markReportRepository.save(report);
    }

    public List<MarkReport> getReportsByStudentId(Long studentId) {
        return markReportRepository.findByStudentId(studentId);
    }

    public List<MarkReport> getReportsByTeacherId(Long teacherId) {
        return markReportRepository.findByTeacherId(teacherId);
    }

    public Optional<MarkReport> getReportById(Long id) {
        return markReportRepository.findById(id);
    }

    public MarkReport updateReportStatus(Long id, MarkReport.ReportStatus status, String comment) {
        MarkReport report = markReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(status);
        report.setTeacherComment(comment);

        return markReportRepository.save(report);
    }

    public List<MarkReport> getPendingReportsForMark(Long markId) {
        return markReportRepository.findPendingByMarkId(markId);
    }

    public void deleteReport(Long id) {
        markReportRepository.deleteById(id);
    }
}
