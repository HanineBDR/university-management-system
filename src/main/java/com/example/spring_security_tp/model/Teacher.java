package com.example.spring_security_tp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"password", "enabled"}) // Don't expose sensitive data
    private User user;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // ⭐ CHANGE THIS - Use @JsonIgnore instead of @JsonIgnoreProperties
    private List<Module> modules = new ArrayList<>();

    // Constructors
    public Teacher() {
    }

    public Teacher(User user, String fullName, LocalDate hireDate) {
        this.user = user;
        this.fullName = fullName;
        this.hireDate = hireDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    // Helper method to add module
    public void addModule(Module module) {
        modules.add(module);
        module.setTeacher(this);
    }

    // Helper method to remove module
    public void removeModule(Module module) {
        modules.remove(module);
        module.setTeacher(null);
    }
}