package com.example.spring_security_tp.service;

import com.example.spring_security_tp.model.Module;
import com.example.spring_security_tp.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    /**
     * Create a new module
     */
    public Module createModule(Module module) {
        return moduleRepository.save(module);
    }

    /**
     * Get module by ID
     */
    public Optional<Module> getModuleById(Long id) {
        return moduleRepository.findById(id);
    }

    /**
     * Get module by code
     */
    public Optional<Module> getModuleByCode(String code) {
        return moduleRepository.findByCode(code);
    }

    /**
     * Get modules by teacher ID
     */
    public List<Module> getModulesByTeacherId(Long teacherId) {
        return moduleRepository.findByTeacherId(teacherId);
    }

    /**
     * Get all modules
     */
    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    /**
     * Update a module
     */
    public Module updateModule(Long id, Module moduleDetails) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));

        module.setName(moduleDetails.getName());
        module.setCode(moduleDetails.getCode());
        module.setDescription(moduleDetails.getDescription());

        if (moduleDetails.getTeacher() != null) {
            module.setTeacher(moduleDetails.getTeacher());
        }

        return moduleRepository.save(module);
    }

    /**
     * Delete a module
     */
    public void deleteModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found with id: " + id));
        moduleRepository.delete(module);
    }

    /**
     * Check if module code exists
     */
    public boolean codeExists(String code) {
        return moduleRepository.existsByCode(code);
    }
}
