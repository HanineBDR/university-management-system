package com.example.spring_security_tp.service;

import com.example.spring_security_tp.model.Teacher;
import com.example.spring_security_tp.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    /**
     * Create a new teacher
     */
    public Teacher createTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    /**
     * Get teacher by ID
     */
    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    /**
     * Get teacher by user ID
     */
    public Optional<Teacher> getTeacherByUserId(Long userId) {
        return teacherRepository.findByUserId(userId);
    }

    /**
     * Get teacher by username
     */
    public Optional<Teacher> getTeacherByUsername(String username) {
        return teacherRepository.findByUsername(username);
    }

    /**
     * Get all teachers
     */
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    /**
     * Update a teacher
     */
    public Teacher updateTeacher(Long id, Teacher teacherDetails) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));

        teacher.setFullName(teacherDetails.getFullName());
        teacher.setHireDate(teacherDetails.getHireDate());

        return teacherRepository.save(teacher);
    }

    /**
     * Delete a teacher
     */
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
        teacherRepository.delete(teacher);
    }

    /**
     * Check if teacher exists by user ID
     */
    public boolean existsByUserId(Long userId) {
        return teacherRepository.existsByUserId(userId);
    }
}
