package com.example.spring_security_tp.repository;

import com.example.spring_security_tp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find student by user ID
     */
    Optional<Student> findByUserId(Long userId);

    /**
     * Find student by user username
     */
    @Query("SELECT s FROM Student s WHERE s.user.username = :username")
    Optional<Student> findByUsername(@Param("username") String username);

    /**
     * Find all students with their user accounts eagerly fetched
     */
    @Query("SELECT s FROM Student s JOIN FETCH s.user")
    List<Student> findAllWithUser();

    /**
     * Find all students
     */
    List<Student> findAll();

    /**
     * Check if student exists by user ID
     */
    boolean existsByUserId(Long userId);
}
