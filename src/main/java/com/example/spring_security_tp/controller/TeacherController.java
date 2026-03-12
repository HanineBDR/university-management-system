package com.example.spring_security_tp.controller;

import com.example.spring_security_tp.model.Mark;
import com.example.spring_security_tp.model.Module;
import com.example.spring_security_tp.model.Student;
import com.example.spring_security_tp.model.Teacher;
import com.example.spring_security_tp.model.User;
import com.example.spring_security_tp.model.MarkReport;
import com.example.spring_security_tp.service.MarkService;
import com.example.spring_security_tp.service.ModuleService;
import com.example.spring_security_tp.service.StudentService;
import com.example.spring_security_tp.service.TeacherService;
import com.example.spring_security_tp.service.UserService;
import com.example.spring_security_tp.service.MarkReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private MarkService markService;

    @Autowired
    private MarkReportService markReportService;

    /**
     * Get current teacher's information
     */
    private Teacher getCurrentTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> userOpt = userService.getUserByUsername(username);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        Optional<Teacher> teacherOpt = teacherService.getTeacherByUserId(userOpt.get().getId());
        if (!teacherOpt.isPresent()) {
            throw new RuntimeException("Teacher profile not found");
        }

        System.out.println("[API] Current User: " + username + " (ID: " + userOpt.get().getId() + ")");
        System.out.println("[API] Linked Teacher Profile: " + teacherOpt.get().getFullName() + " (ID: "
                + teacherOpt.get().getId() + ")");

        return teacherOpt.get();
    }

    // ========================================
    // Module Management
    // ========================================

    @GetMapping("/modules")
    public ResponseEntity<?> getTeacherModules() {
        try {
            Teacher teacher = getCurrentTeacher();
            List<Module> modules = moduleService.getModulesByTeacherId(teacher.getId());

            List<Map<String, Object>> result = modules.stream().map(m -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getId());
                map.put("name", m.getName());
                map.put("code", m.getCode());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/module")
    public ResponseEntity<?> getTeacherModule() {
        try {
            Teacher teacher = getCurrentTeacher();
            List<Module> modules = moduleService.getModulesByTeacherId(teacher.getId());

            if (!modules.isEmpty()) {
                Module m = modules.get(0);
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getId());
                map.put("name", m.getName());
                map.put("code", m.getCode());
                map.put("description", m.getDescription());
                return ResponseEntity.ok(map);
            } else {
                return ResponseEntity.ok(Map.of("message", "No module assigned"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================================
    // Student Management (View Only)
    // ========================================

    @GetMapping("/students")
    public ResponseEntity<?> getStudentsInModule() {
        try {
            System.out.println("[API] Fetching all students with users for teacher dashboard...");
            // Use specialized query to avoid LazyInitializationException
            List<Map<String, Object>> students = studentService.getAllStudentsWithUser().stream()
                    .map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", s.getId());
                        map.put("fullName", s.getFullName());
                        map.put("enrollmentDate",
                                s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : null);

                        if (s.getUser() != null) {
                            Map<String, String> userMap = new HashMap<>();
                            userMap.put("username", s.getUser().getUsername());
                            userMap.put("email", s.getUser().getEmail());
                            map.put("user", userMap);
                        }
                        return map;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            System.err.println("[API ERROR] Error in getStudentsInModule: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================================
    // Mark Management
    // ========================================

    @GetMapping("/marks")
    public ResponseEntity<?> getTeacherMarks() {
        try {
            Teacher teacher = getCurrentTeacher();
            List<Mark> marks = markService.getMarksByModuleTeacherId(teacher.getId());

            // Manually map to avoid date serialization issues
            List<Map<String, Object>> result = marks.stream().map(m -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getId());
                map.put("score", m.getScore());
                map.put("markDate", m.getMarkDate() != null ? m.getMarkDate().toString() : null);
                map.put("comments", m.getComments());

                if (m.getStudent() != null) {
                    Map<String, Object> sMap = new HashMap<>();
                    sMap.put("id", m.getStudent().getId());
                    sMap.put("fullName", m.getStudent().getFullName());
                    map.put("student", sMap);
                }
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/marks")
    public ResponseEntity<?> addMark(@RequestBody Map<String, Object> request) {
        System.out.println("[DEBUG] TeacherController: POST /api/teacher/marks request: " + request);
        try {
            Teacher teacher = getCurrentTeacher();

            // Get module
            List<Module> modules = moduleService.getModulesByTeacherId(teacher.getId());
            if (modules.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Teacher has no assigned module"));
            }
            Module module = modules.get(0);

            // Get request data
            Long studentId = Long.valueOf(request.get("studentId").toString());
            Double score = Double.valueOf(request.get("score").toString());
            String comments = request.get("comments") != null ? request.get("comments").toString() : null;

            // Validate score
            if (score < 0 || score > 20) {
                return ResponseEntity.badRequest().body(Map.of("error", "Score must be between 0 and 20"));
            }

            // Check if student exists
            Optional<Student> studentOpt = studentService.getStudentById(studentId);
            if (!studentOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Student not found"));
            }

            // Check if mark already exists
            if (markService.existsByStudentIdAndModuleId(studentId, module.getId())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Mark already exists for this student and module"));
            }

            // Create mark
            Mark mark = new Mark();
            mark.setStudent(studentOpt.get());
            mark.setModule(module);
            mark.setTeacher(teacher);
            mark.setScore(score);
            mark.setMarkDate(LocalDate.now());
            mark.setComments(comments);

            mark = markService.createMark(mark);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Mark added successfully",
                    "markId", mark.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/marks/{id}")
    public ResponseEntity<?> updateMark(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        System.out.println("[DEBUG] TeacherController: PUT /api/teacher/marks/" + id + " request: " + request);
        try {
            Teacher teacher = getCurrentTeacher();

            // Verify mark belongs to teacher's module
            if (!markService.canTeacherModifyMark(id, teacher.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only modify marks for your own module"));
            }

            Optional<Mark> markOpt = markService.getMarkById(id);
            if (!markOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Mark mark = markOpt.get();

            // Update score if provided
            if (request.containsKey("score")) {
                Double score = Double.valueOf(request.get("score").toString());
                if (score < 0 || score > 20) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Score must be between 0 and 20"));
                }
                mark.setScore(score);
            }

            // Update comments if provided
            if (request.containsKey("comments")) {
                mark.setComments(request.get("comments").toString());
            }

            // Update date
            mark.setMarkDate(LocalDate.now());

            Mark updatedMark = markService.updateMark(id, mark);

            return ResponseEntity.ok(Map.of(
                    "message", "Mark updated successfully",
                    "markId", updatedMark.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/marks/{id}")
    public ResponseEntity<?> deleteMark(@PathVariable Long id) {
        try {
            Teacher teacher = getCurrentTeacher();

            // Verify mark belongs to teacher's module
            if (!markService.canTeacherModifyMark(id, teacher.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only delete marks for your own module"));
            }

            markService.deleteMark(id);
            return ResponseEntity.ok(Map.of("message", "Mark deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================================
    // Mark Reports Management
    // ========================================

    @GetMapping("/reports")
    public ResponseEntity<?> getModuleReports() {
        try {
            Teacher teacher = getCurrentTeacher();
            List<MarkReport> reports = markReportService.getReportsByTeacherId(teacher.getId());
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/reports/{id}")
    public ResponseEntity<?> updateReportStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        System.out.println("[DEBUG] TeacherController: PUT /api/teacher/reports/" + id + " request: " + request);
        try {
            Teacher teacher = getCurrentTeacher();

            // SECURITY: Verify the report exists and belongs to this teacher
            Optional<MarkReport> reportOpt = markReportService.getReportById(id);
            if (!reportOpt.isPresent()) {
                System.out.println("[DEBUG] Report " + id + " not found");
                return ResponseEntity.notFound().build();
            }

            MarkReport report = reportOpt.get();
            if (!report.getMark().getTeacher().getId().equals(teacher.getId())) {
                System.out.println("[DEBUG] Security breach attempt: Teacher " + teacher.getId() +
                        " trying to update report " + id + " belonging to teacher " +
                        report.getMark().getTeacher().getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You are not authorized to handle this report"));
            }

            String statusStr = (String) request.get("status");
            String comment = (String) request.get("teacherComment");

            if (statusStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }

            MarkReport.ReportStatus status = MarkReport.ReportStatus.valueOf(statusStr);
            MarkReport updated = markReportService.updateReportStatus(id, status, comment);

            System.out.println("[DEBUG] Report " + id + " updated to " + status + " by teacher " + teacher.getId());
            return ResponseEntity.ok(Map.of(
                    "message", "Report status updated to " + status,
                    "report", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status value: " + request.get("status")));
        } catch (Exception e) {
            System.err.println("[DEBUG ERROR] Failed to update report: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
