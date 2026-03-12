package com.example.spring_security_tp.config;

import com.example.spring_security_tp.model.User;
import com.example.spring_security_tp.model.Teacher;
import com.example.spring_security_tp.model.Student;
import com.example.spring_security_tp.repository.UserRepository;
import com.example.spring_security_tp.repository.TeacherRepository;
import com.example.spring_security_tp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("###  DYNAMIC USERNAME ALIGNMENT (Personalized)  ###");
        System.out.println("=".repeat(60));

        // 1. Align Admin
        userRepository.findByUsername("admin").ifPresent(u -> {
            u.setPassword(passwordEncoder.encode("admin"));
            userRepository.save(u);
            System.out.println("[*] Admin account secured: admin / admin");
        });

        // 2. Align All Teachers
        List<Teacher> teachers = teacherRepository.findAll();
        System.out.println("[*] Processing " + teachers.size() + " Teachers...");
        for (Teacher t : teachers) {
            String sanitizedName = t.getFullName().toLowerCase().trim().replace(" ", ".");
            String newUsername = "teacher." + sanitizedName;

            User u = t.getUser();
            u.setUsername(newUsername);
            u.setPassword(passwordEncoder.encode("teacher"));
            userRepository.save(u);
            System.out.println("  -> Teacher Ready: " + newUsername);
        }

        // 3. Align All Students
        List<Student> students = studentRepository.findAll();
        System.out.println("[*] Processing " + students.size() + " Students...");
        for (Student s : students) {
            String sanitizedName = s.getFullName().toLowerCase().trim().replace(" ", ".");
            String newUsername = "student." + sanitizedName;

            User u = s.getUser();
            u.setUsername(newUsername);
            u.setPassword(passwordEncoder.encode("student"));
            userRepository.save(u);
            System.out.println("  -> Student Ready: " + newUsername);
        }

        System.out.println("\n" + "#".repeat(60));
        System.out.println("###  RE-ALIGNMENT COMPLETE! CHECK LOGINS BELOW  ###");
        System.out.println("#".repeat(60));
        System.out.println("ADMIN   : admin / admin");
        System.out.println("TEACHER : teacher.name.surname (Pass: teacher)");
        System.out.println("STUDENT : student.name.surname (Pass: student)");
        System.out.println("#".repeat(60) + "\n");
    }
}
