package com.example.spring_security_tp.service;

import com.example.spring_security_tp.model.Student;
import com.example.spring_security_tp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Create a new student
     */
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    /**
     * Get student by ID
     */
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    /**
     * Get student by user ID
     */
    public Optional<Student> getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId);
    }

    /**
     * Get student by username
     */
    public Optional<Student> getStudentByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    /**
     * Get all students with their user accounts eagerly fetched
     */
    public List<Student> getAllStudentsWithUser() {
        return studentRepository.findAllWithUser();
    }

    /**
     * Get all students
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Update a student
     */
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        student.setFullName(studentDetails.getFullName());
        student.setEnrollmentDate(studentDetails.getEnrollmentDate());

        return studentRepository.save(student);
    }

    /**
     * Delete a student
     */
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        studentRepository.delete(student);
    }

    /**
     * Check if student exists by user ID
     */
    public boolean existsByUserId(Long userId) {
        return studentRepository.existsByUserId(userId);
    }
}
