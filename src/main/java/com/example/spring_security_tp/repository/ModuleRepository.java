package com.example.spring_security_tp.repository;

import com.example.spring_security_tp.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    /**
     * Find modules by teacher ID
     */
    List<Module> findByTeacherId(Long teacherId);

    /**
     * Find module by code
     */
    Optional<Module> findByCode(String code);

    /**
     * Find all modules
     */
    List<Module> findAll();

    /**
     * Check if module exists by code
     */
    boolean existsByCode(String code);
}
