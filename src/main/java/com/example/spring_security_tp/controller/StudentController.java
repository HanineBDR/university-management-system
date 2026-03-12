package com.example.spring_security_tp.controller;

import com.example.spring_security_tp.model.*;
import com.example.spring_security_tp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private MarkService markService;

    @Autowired
    private MarkReportService markReportService;

    /**
     * Get current student's information
     */
    private Student getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> userOpt = userService.getUserByUsername(username);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        Optional<Student> studentOpt = studentService.getStudentByUserId(userOpt.get().getId());
        if (!studentOpt.isPresent()) {
            throw new RuntimeException("Student profile not found");
        }

        return studentOpt.get();
    }

    // ========================================
    // Profile
    // ========================================

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Student student = getCurrentStudent();

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", student.getId());
            profile.put("fullName", student.getFullName());
            profile.put("enrollmentDate", student.getEnrollmentDate());
            profile.put("username", student.getUser().getUsername());
            profile.put("email", student.getUser().getEmail());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================================
    // Marks
    // ========================================

    @GetMapping("/marks")
    public ResponseEntity<?> getMarks() {
        try {
            Student student = getCurrentStudent();
            List<Mark> marks = markService.getMarksByStudentId(student.getId());

            // Transform marks to include module information
            List<Map<String, Object>> marksData = marks.stream()
                    .map(mark -> {
                        Map<String, Object> markData = new HashMap<>();
                        markData.put("id", mark.getId());
                        markData.put("score", mark.getScore());
                        markData.put("date", mark.getMarkDate());
                        markData.put("comments", mark.getComments());

                        // Add module info
                        if (mark.getModule() != null) {
                            Map<String, Object> moduleInfo = new HashMap<>();
                            moduleInfo.put("id", mark.getModule().getId());
                            moduleInfo.put("name", mark.getModule().getName());
                            moduleInfo.put("code", mark.getModule().getCode());
                            markData.put("module", moduleInfo);
                        }

                        // Add teacher info
                        if (mark.getTeacher() != null) {
                            Map<String, Object> teacherInfo = new HashMap<>();
                            teacherInfo.put("id", mark.getTeacher().getId());
                            teacherInfo.put("fullName", mark.getTeacher().getFullName());
                            markData.put("teacher", teacherInfo);
                        }

                        return markData;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(marksData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/report")
    public ResponseEntity<?> getReport() {
        try {
            Student student = getCurrentStudent();
            List<Mark> marks = markService.getMarksByStudentId(student.getId());

            // Calculate statistics
            double average = 0.0;
            double highest = 0.0;
            double lowest = 20.0;
            int totalMarks = marks.size();

            if (!marks.isEmpty()) {
                average = marks.stream()
                        .mapToDouble(Mark::getScore)
                        .average()
                        .orElse(0.0);

                highest = marks.stream()
                        .mapToDouble(Mark::getScore)
                        .max()
                        .orElse(0.0);

                lowest = marks.stream()
                        .mapToDouble(Mark::getScore)
                        .min()
                        .orElse(0.0);
            }

            Map<String, Object> report = new HashMap<>();
            report.put("studentName", student.getFullName());
            report.put("enrollmentDate", student.getEnrollmentDate());
            report.put("totalMarks", totalMarks);
            report.put("average", Math.round(average * 100.0) / 100.0);
            report.put("highest", highest);
            report.put("lowest", lowest);
            report.put("marks", marks.stream()
                    .map(mark -> Map.of(
                            "module", mark.getModule().getName(),
                            "code", mark.getModule().getCode(),
                            "score", mark.getScore(),
                            "date", mark.getMarkDate(),
                            "teacher", mark.getTeacher().getFullName()))
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================================
    // Mark Reports
    // ========================================

    @PostMapping("/reports")
    public ResponseEntity<?> submitReport(@RequestBody Map<String, Object> request) {
        try {
            Student student = getCurrentStudent();
            Long markId = Long.valueOf(request.get("markId").toString());
            String reason = (String) request.get("reason");

            Optional<Mark> markOpt = markService.getMarkById(markId);
            if (!markOpt.isPresent() || !markOpt.get().getStudent().getId().equals(student.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid mark ID"));
            }

            // Check if already has a pending report
            List<MarkReport> pending = markReportService.getPendingReportsForMark(markId);
            if (!pending.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "A report is already pending for this mark"));
            }

            MarkReport report = new MarkReport(markOpt.get(), reason);
            MarkReport created = markReportService.createReport(report);

            return ResponseEntity.ok(Map.of(
                    "message", "Report submitted successfully",
                    "reportId", created.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getMyReports() {
        try {
            Student student = getCurrentStudent();
            List<MarkReport> reports = markReportService.getReportsByStudentId(student.getId());
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reports/delete/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        System.out.println("[DEBUG] Request to delete report ID: " + id);
        try {
            Student student = getCurrentStudent();
            Optional<MarkReport> reportOpt = markReportService.getReportById(id);

            if (!reportOpt.isPresent()) {
                System.out.println("[DEBUG] Report not found: " + id);
                return ResponseEntity.notFound().build();
            }

            MarkReport report = reportOpt.get();

            // SECURITY: Ensure the report belongs to this student
            if (!report.getMark().getStudent().getId().equals(student.getId())) {
                System.out.println(
                        "[DEBUG] Security check failed: User " + student.getId() + " tried to delete report " + id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only delete your own reports"));
            }

            // BUSINESS LOGIC: Can only delete if status is WAITING
            if (report.getStatus() != MarkReport.ReportStatus.WAITING) {
                System.out.println("[DEBUG] Invalid status for delete: " + report.getStatus());
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cannot delete a report that has already been reviewed"));
            }

            markReportService.deleteReport(id);
            System.out.println("[DEBUG] Report " + id + " deleted successfully");
            return ResponseEntity.ok(Map.of("message", "Report deleted successfully"));
        } catch (Exception e) {
            System.err.println("[DEBUG] Error deleting report " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
