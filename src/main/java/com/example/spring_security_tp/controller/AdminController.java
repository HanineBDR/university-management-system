package com.example.spring_security_tp.controller;

import com.example.spring_security_tp.model.Mark;
import com.example.spring_security_tp.model.Module;
import com.example.spring_security_tp.model.Student;
import com.example.spring_security_tp.model.Teacher;
import com.example.spring_security_tp.model.User;
import com.example.spring_security_tp.service.MarkService;
import com.example.spring_security_tp.service.ModuleService;
import com.example.spring_security_tp.service.StudentService;
import com.example.spring_security_tp.service.TeacherService;
import com.example.spring_security_tp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private MarkService markService;

    // ========================================
    // User Management
    // ========================================

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/users", consumes = "application/json")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> request) {
        System.out.println("[DEBUG] AdminController: Received request to create user: " + request);
        try {
            String username = (String) request.get("username");
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            String role = (String) request.get("role");

            // Validate required fields
            if (username == null || email == null || password == null || role == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }

            // Check if username or email already exists
            if (userService.usernameExists(username)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
            }
            if (userService.emailExists(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }

            // Create user
            User user = new User(username, email, password, role);
            user = userService.createUser(user);

            // Create corresponding Student or Teacher profile
            if ("STUDENT".equals(role)) {
                String fullName = (String) request.get("fullName");
                if (fullName == null)
                    fullName = username;

                Student student = new Student();
                student.setUser(user);
                student.setFullName(fullName);
                student.setEnrollmentDate(LocalDate.now());
                studentService.createStudent(student);
            } else if ("TEACHER".equals(role)) {
                String fullName = (String) request.get("fullName");
                if (fullName == null)
                    fullName = username;

                Teacher teacher = new Teacher();
                teacher.setUser(user);
                teacher.setFullName(fullName);
                teacher.setHireDate(LocalDate.now());
                teacherService.createTeacher(teacher);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "User created successfully",
                    "user", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            // Update User account
            User userDetails = new User();
            if (request.containsKey("username"))
                userDetails.setUsername((String) request.get("username"));
            if (request.containsKey("email"))
                userDetails.setEmail((String) request.get("email"));
            if (request.containsKey("password") && !((String) request.get("password")).isEmpty())
                userDetails.setPassword((String) request.get("password"));
            if (request.containsKey("role"))
                userDetails.setRole((String) request.get("role"));
            if (request.containsKey("enabled"))
                userDetails.setEnabled((Boolean) request.get("enabled"));

            User updatedUser = userService.updateUser(id, userDetails);

            // Update Profile Full Name if applicable
            if (request.containsKey("fullName")) {
                String fullName = (String) request.get("fullName");
                if ("STUDENT".equals(updatedUser.getRole())) {
                    studentService.getStudentByUserId(id).ifPresent(s -> {
                        s.setFullName(fullName);
                        studentService.updateStudent(s.getId(), s);
                    });
                } else if ("TEACHER".equals(updatedUser.getRole())) {
                    teacherService.getTeacherByUserId(id).ifPresent(t -> {
                        t.setFullName(fullName);
                        teacherService.updateTeacher(t.getId(), t);
                    });
                }
            }

            return ResponseEntity.ok(Map.of(
                    "message", "User and profile updated successfully",
                    "user", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/enable")
    public ResponseEntity<?> enableUser(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        try {
            Boolean enabled = request.get("enabled");
            if (enabled == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "enabled field is required"));
            }

            User user = userService.enableUser(id, enabled);
            return ResponseEntity.ok(Map.of(
                    "message", "User " + (enabled ? "enabled" : "disabled") + " successfully",
                    "user", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================================
    // Student Management
    // ========================================

    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudent(@PathVariable Long id) {
        try {
            Optional<Student> student = studentService.getStudentById(id);
            if (student.isPresent()) {
                return ResponseEntity.ok(student.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================================
    // Teacher Management
    // ========================================

    @GetMapping("/teachers")
    public ResponseEntity<?> getAllTeachers() {
        try {
            List<Teacher> teachers = teacherService.getAllTeachers();
            return ResponseEntity.ok(teachers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/teachers/{id}")
    public ResponseEntity<?> getTeacher(@PathVariable Long id) {
        try {
            Optional<Teacher> teacher = teacherService.getTeacherById(id);
            if (teacher.isPresent()) {
                return ResponseEntity.ok(teacher.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================================
    // Module Management
    // ========================================

    @GetMapping("/modules")
    public ResponseEntity<?> getAllModules() {
        try {
            List<Module> modules = moduleService.getAllModules();
            return ResponseEntity.ok(modules);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================================
    // Mark Management
    // ========================================

    @GetMapping("/marks")
    public ResponseEntity<?> getAllMarks() {
        try {
            List<Mark> marks = markService.getAllMarks();
            return ResponseEntity.ok(marks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/marks/student/{studentId}")
    public ResponseEntity<?> getMarksByStudentId(@PathVariable Long studentId) {
        try {
            List<Mark> marks = markService.getMarksByStudentId(studentId);
            return ResponseEntity.ok(marks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
