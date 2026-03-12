package com.example.spring_security_tp.service;

import com.example.spring_security_tp.model.User;
import com.example.spring_security_tp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("\n[DEBUG] --- Auth Attempt for: " + username + " ---");

        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> {
                    System.out.println("[DEBUG] FAILED: User '" + username + "' not found in DB.");
                    return new UsernameNotFoundException("User not found: " + username);
                });

        System.out.println("[DEBUG] Found user: " + user.getUsername() + " | Enabled: " + user.getEnabled()
                + " | Role: " + user.getRole());

        // Check if account is enabled
        if (!user.getEnabled()) {
            System.out.println("[DEBUG] FAILED: Account is disabled.");
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }

        // Add ROLE_ prefix if not already present
        String role = user.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        System.out.println("[DEBUG] Created Authority: " + role);
        System.out.println("[DEBUG] -----------------------------------\n");

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                AuthorityUtils.createAuthorityList(role));
    }
}