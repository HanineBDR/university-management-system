package com.example.spring_security_tp.repository;

import com.example.spring_security_tp.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * Find teacher by user ID
     */
    Optional<Teacher> findByUserId(Long userId);

    /**
     * Find teacher by user username
     */
    @Query("SELECT t FROM Teacher t WHERE t.user.username = :username")
    Optional<Teacher> findByUsername(@Param("username") String username);

    /**
     * Find all teachers
     */
    List<Teacher> findAll();

    /**
     * Check if teacher exists by user ID
     */
    boolean existsByUserId(Long userId);
}
