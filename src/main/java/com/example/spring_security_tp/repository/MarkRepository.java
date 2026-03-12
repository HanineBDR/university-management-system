package com.example.spring_security_tp.repository;

import com.example.spring_security_tp.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {

    /**
     * Find all marks for a specific student
     */
    List<Mark> findByStudentId(Long studentId);

    /**
     * Find all marks for a specific module
     */
    List<Mark> findByModuleId(Long moduleId);

    /**
     * Find all marks assigned by a specific teacher
     */
    List<Mark> findByTeacherId(Long teacherId);

    /**
     * Find marks for a module taught by a specific teacher
     */
    @Query("SELECT m FROM Mark m WHERE m.module.teacher.id = :teacherId")
    List<Mark> findByModuleTeacherId(@Param("teacherId") Long teacherId);

    /**
     * Find a specific mark for a student in a module
     */
    Optional<Mark> findByStudentIdAndModuleId(Long studentId, Long moduleId);

    /**
     * Find all marks
     */
    List<Mark> findAll();

    /**
     * Check if mark exists for student and module
     */
    boolean existsByStudentIdAndModuleId(Long studentId, Long moduleId);
}
