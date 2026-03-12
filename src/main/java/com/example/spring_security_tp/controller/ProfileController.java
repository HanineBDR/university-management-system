package com.example.spring_security_tp.controller;

import com.example.spring_security_tp.model.Student;
import com.example.spring_security_tp.model.Teacher;
import com.example.spring_security_tp.model.User;
import com.example.spring_security_tp.service.StudentService;
import com.example.spring_security_tp.service.TeacherService;
import com.example.spring_security_tp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    /**
     * Get current authenticated user's profile
     * Available to all authenticated users
     */
    @GetMapping
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<User> userOpt = userService.getUserByUsername(username);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("role", user.getRole());
            profile.put("enabled", user.getEnabled());

            // Add additional info based on role
            if ("STUDENT".equals(user.getRole())) {
                Optional<Student> studentOpt = studentService.getStudentByUserId(user.getId());
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    profile.put("fullName", student.getFullName());
                    profile.put("enrollmentDate", student.getEnrollmentDate());
                    profile.put("studentId", student.getId());
                }
            } else if ("TEACHER".equals(user.getRole())) {
                Optional<Teacher> teacherOpt = teacherService.getTeacherByUserId(user.getId());
                if (teacherOpt.isPresent()) {
                    Teacher teacher = teacherOpt.get();
                    profile.put("fullName", teacher.getFullName());
                    profile.put("hireDate", teacher.getHireDate());
                    profile.put("teacherId", teacher.getId());
                }
            }

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
