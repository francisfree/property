package com.castle.property.config;

import com.castle.property.entity.Role;
import com.castle.property.entity.User;
import com.castle.property.repository.RoleRepository;
import com.castle.property.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitialAdminBootstrap {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.initial-admin.username:admin}")
    private String adminUsername;

    @Value("${app.security.initial-admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.security.initial-admin.password:admin123}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void createInitialAdmin() {
        try {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("ADMIN");
                        role.setDescription("Administrator with full access");
                        return roleRepository.save(role);
                    });

            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("USER");
                        role.setDescription("Standard user with basic access");
                        return roleRepository.save(role);
                    });

            if (userRepository.countByRolesName("ADMIN") == 0) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setEmail(adminEmail);
                admin.setPasswordHash(passwordEncoder.encode(adminPassword));
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                admin.setEnabled(true);

                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                roles.add(userRole);
                admin.setRoles(roles);

                userRepository.save(admin);
                log.info("Initial admin user created: {}", adminUsername);
            } else {
                log.info("Admin user already exists, skipping initial admin creation");
            }
        } catch (Exception e) {
            log.error("Failed to create initial admin user", e);
        }
    }
}
