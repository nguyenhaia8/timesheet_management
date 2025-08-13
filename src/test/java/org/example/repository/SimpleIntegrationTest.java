package org.example.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SimpleIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testApplicationContextLoads() {
        // Test that Spring Boot application context loads successfully
        assertNotNull(applicationContext);
        assertTrue(applicationContext.getBeanDefinitionCount() > 0);
    }

    @Test
    void testBasicSpringBootFunctionality() {
        // Test basic Spring Boot functionality
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertNotNull(beanNames);
        assertTrue(beanNames.length > 0);
        
        // Verify some common Spring Boot beans are present
        boolean hasDataSource = false;
        boolean hasEntityManagerFactory = false;
        
        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains("datasource")) {
                hasDataSource = true;
            }
            if (beanName.toLowerCase().contains("entitymanager")) {
                hasEntityManagerFactory = true;
            }
        }
        
        // These should be present in a Spring Boot JPA application
        assertTrue(hasDataSource, "DataSource bean should be present");
        assertTrue(hasEntityManagerFactory, "EntityManagerFactory bean should be present");
    }

    @Test
    void testRepositoryBeansAreLoaded() {
        // Test that repository beans are loaded
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        
        boolean hasDepartmentRepository = false;
        boolean hasUserRepository = false;
        
        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains("departmentrepository")) {
                hasDepartmentRepository = true;
            }
            if (beanName.toLowerCase().contains("userrepository")) {
                hasUserRepository = true;
            }
        }
        
        // Verify repository beans are loaded
        assertTrue(hasDepartmentRepository, "DepartmentRepository bean should be present");
        assertTrue(hasUserRepository, "UserRepository bean should be present");
    }

    @Test
    void testServiceBeansAreLoaded() {
        // Test that service beans are loaded
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        
        boolean hasUserService = false;
        
        for (String beanName : beanNames) {
            if (beanName.toLowerCase().contains("userservice")) {
                hasUserService = true;
            }
        }
        
        // Verify service beans are loaded
        assertTrue(hasUserService, "UserService bean should be present");
    }

    @Test
    void testConfigurationPropertiesAreLoaded() {
        // Test that configuration properties are loaded
        assertNotNull(applicationContext.getEnvironment());
        assertNotNull(applicationContext.getEnvironment().getActiveProfiles());
        
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        assertTrue(activeProfiles.length > 0, "At least one profile should be active");
        assertTrue(contains(activeProfiles, "test"), "Test profile should be active");
    }
    
    private boolean contains(String[] array, String value) {
        for (String item : array) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
