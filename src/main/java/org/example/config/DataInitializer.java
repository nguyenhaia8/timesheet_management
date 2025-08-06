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
        // Initialize default roles if they don't exist
        if (roleRepository.count() == 0) {
            Role adminRole = new Role("ADMIN", "Administrator with full access");
            Role managerRole = new Role("MANAGER", "Manager with department access");
            Role employeeRole = new Role("EMPLOYEE", "Regular employee");

            roleRepository.save(adminRole);
            roleRepository.save(managerRole);
            roleRepository.save(employeeRole);

            System.out.println("Default roles initialized successfully!");
        }
    }
} 