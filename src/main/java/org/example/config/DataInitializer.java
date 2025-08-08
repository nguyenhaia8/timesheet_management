package org.example.config;

import org.example.model.Role;
import org.example.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Ensure all required roles exist (idempotent)
        ensureRoleExists("ADMIN", "Administrator with full access");
        ensureRoleExists("MANAGER", "Manager with department access");
        ensureRoleExists("EMPLOYEE", "Regular employee");
    }

    private void ensureRoleExists(String roleName, String description) {
        roleRepository.findByRoleName(roleName)
            .orElseGet(() -> {
                Role role = new Role(roleName, description);
                Role saved = roleRepository.save(role);
                System.out.println("Initialized missing role: " + roleName);
                return saved;
            });
    }
} 