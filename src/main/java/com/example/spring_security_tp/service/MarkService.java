package com.example.spring_security_tp.service;

import com.example.spring_security_tp.model.Mark;
import com.example.spring_security_tp.model.Module;
import com.example.spring_security_tp.repository.MarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MarkService {

    @Autowired
    private MarkRepository markRepository;

    /**
     * Create a new mark
     * Validates that the teacher owns the module
     */
    public Mark createMark(Mark mark) {
        // Validate that the teacher owns the module
        Module module = mark.getModule();
        if (module != null && module.getTeacher() != null) {
            if (!module.getTeacher().getId().equals(mark.getTeacher().getId())) {
                throw new RuntimeException("Teacher does not own this module");
            }
        }

        return markRepository.save(mark);
    }

    /**
     * Get mark by ID
     */
    public Optional<Mark> getMarkById(Long id) {
        return markRepository.findById(id);
    }

    /**
     * Get all marks for a student
     */
    public List<Mark> getMarksByStudentId(Long studentId) {
        return markRepository.findByStudentId(studentId);
    }

    /**
     * Get all marks for a module
     */
    public List<Mark> getMarksByModuleId(Long moduleId) {
        return markRepository.findByModuleId(moduleId);
    }

    /**
     * Get all marks assigned by a teacher
     */
    public List<Mark> getMarksByTeacherId(Long teacherId) {
        return markRepository.findByTeacherId(teacherId);
    }

    /**
     * Get marks for modules taught by a teacher
     */
    public List<Mark> getMarksByModuleTeacherId(Long teacherId) {
        return markRepository.findByModuleTeacherId(teacherId);
    }

    /**
     * Get all marks
     */
    public List<Mark> getAllMarks() {
        return markRepository.findAll();
    }

    /**
     * Update a mark
     * Validates that the teacher owns the module
     */
    public Mark updateMark(Long id, Mark markDetails) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found with id: " + id));

        // Validate that the teacher owns the module (if teacher is being changed)
        if (markDetails.getTeacher() != null && mark.getModule() != null) {
            if (!mark.getModule().getTeacher().getId().equals(markDetails.getTeacher().getId())) {
                throw new RuntimeException("Teacher does not own this module");
            }
        }

        mark.setScore(markDetails.getScore());
        mark.setMarkDate(markDetails.getMarkDate());

        if (markDetails.getComments() != null) {
            mark.setComments(markDetails.getComments());
        }

        return markRepository.save(mark);
    }

    /**
     * Delete a mark
     */
    public void deleteMark(Long id) {
        Mark mark = markRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found with id: " + id));
        markRepository.delete(mark);
    }

    /**
     * Check if mark exists for student and module
     */
    public boolean existsByStudentIdAndModuleId(Long studentId, Long moduleId) {
        return markRepository.existsByStudentIdAndModuleId(studentId, moduleId);
    }

    /**
     * Validate if teacher can modify mark
     */
    public boolean canTeacherModifyMark(Long markId, Long teacherId) {
        Optional<Mark> markOpt = markRepository.findById(markId);
        if (markOpt.isPresent()) {
            Mark mark = markOpt.get();
            return mark.getModule().getTeacher().getId().equals(teacherId);
        }
        return false;
    }
}
