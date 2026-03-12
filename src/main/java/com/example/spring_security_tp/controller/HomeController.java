package com.example.spring_security_tp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "landing";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Authentication authentication) {
        // Redirect based on user role
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (role.equals("ROLE_TEACHER")) {
                return "redirect:/teacher/dashboard";
            } else if (role.equals("ROLE_STUDENT")) {
                return "redirect:/student/dashboard";
            }
        }

        return "redirect:/login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin";
    }

    @GetMapping("/teacher/dashboard")
    public String teacherDashboard() {
        return "teacher";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard() {
        return "student";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }

    @GetMapping("/academics")
    public String academics() {
        return "academics";
    }

    @GetMapping("/admissions")
    public String admissions() {
        return "admissions";
    }

    @GetMapping("/research")
    public String research() {
        return "research";
    }
}
