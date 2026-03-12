package com.example.spring_security_tp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "marks", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "student_id", "module_id" })
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties("marks")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id", nullable = false)
    @JsonIgnoreProperties("marks")
    private Module module;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", nullable = false)
    @JsonIgnoreProperties("modules")
    private Teacher teacher;

    @Column(nullable = false, precision = 5, scale = 2)
    private Double score;

    @Column(name = "mark_date", nullable = false)
    private LocalDate markDate;

    @Column(columnDefinition = "TEXT")
    private String comments;

    // Constructors
    public Mark() {
    }

    public Mark(Student student, Module module, Teacher teacher, Double score, LocalDate markDate) {
        this.student = student;
        this.module = module;
        this.teacher = teacher;
        this.score = score;
        this.markDate = markDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public LocalDate getMarkDate() {
        return markDate;
    }

    public void setMarkDate(LocalDate markDate) {
        this.markDate = markDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
