package org.example.integration;

import org.example.model.Client;
import org.example.model.Department;
import org.example.model.Employee;
import org.example.model.Project;
import org.example.repository.ClientRepository;
import org.example.repository.DepartmentRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration")
@Transactional
class ProjectIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Client testClient;
    private Employee testEmployee;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        // Create test data
        createTestData();
    }

    private void createTestData() {
        // Create department
        testDepartment = new Department();
        testDepartment.setName("Test Department");
        testDepartment.setCreatedAt(LocalDateTime.now());
        testDepartment.setUpdatedAt(LocalDateTime.now());
        testDepartment = departmentRepository.save(testDepartment);

        // Create employee
        testEmployee = new Employee();
        testEmployee.setFirstName("Test");
        testEmployee.setLastName("Manager");
        testEmployee.setEmail("test.manager@company.com");
        testEmployee.setPosition("Project Manager");
        testEmployee.setDepartment(testDepartment);
        testEmployee.setCreatedAt(LocalDateTime.now());
        testEmployee.setUpdatedAt(LocalDateTime.now());
        testEmployee = employeeRepository.save(testEmployee);

        // Create client
        testClient = new Client();
        testClient.setClientName("Test Client");
        testClient.setContactEmail("client@test.com");
        testClient.setContactPhone("123-456-7890");
        testClient.setAddress("123 Test Street, Test City");
        testClient.setCreatedAt(LocalDateTime.now());
        testClient.setUpdatedAt(LocalDateTime.now());
        testClient = clientRepository.save(testClient);
    }

    private Project createProjectWithTimestamps(String name, String description, LocalDate startDate, LocalDate endDate, Project.ProjectStatus status) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setStatus(status);
        project.setClient(testClient);
        project.setProjectManager(testEmployee);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    }

    @Test
    void testCreateProject() {
        // Arrange
        Project project = createProjectWithTimestamps(
            "Integration Test Project",
            "A project created for integration testing",
            LocalDate.now(),
            LocalDate.now().plusMonths(6),
            Project.ProjectStatus.PLANNING
        );

        // Act
        Project savedProject = projectRepository.save(project);

        // Assert
        assertNotNull(savedProject);
        assertNotNull(savedProject.getProjectId());
        assertEquals("Integration Test Project", savedProject.getName());
        assertEquals(testClient.getClientId(), savedProject.getClient().getClientId());
        assertEquals(testEmployee.getEmployeeId(), savedProject.getProjectManager().getEmployeeId());
        assertEquals(Project.ProjectStatus.PLANNING, savedProject.getStatus());
    }

    @Test
    void testFindProjectById() {
        // Arrange
        Project project = createProjectWithTimestamps(
            "Find Test Project",
            "Project to test find by ID",
            LocalDate.now(),
            LocalDate.now().plusMonths(3),
            Project.ProjectStatus.ACTIVE
        );
        Project savedProject = projectRepository.save(project);

        // Act
        Optional<Project> foundProject = projectRepository.findById(savedProject.getProjectId());

        // Assert
        assertTrue(foundProject.isPresent());
        assertEquals("Find Test Project", foundProject.get().getName());
        assertEquals(savedProject.getProjectId(), foundProject.get().getProjectId());
    }

    @Test
    void testFindAllProjects() {
        // Arrange - Create multiple projects
        Project project1 = createProjectWithTimestamps(
            "Project 1",
            "First test project",
            LocalDate.now(),
            LocalDate.now().plusMonths(2),
            Project.ProjectStatus.ACTIVE
        );
        projectRepository.save(project1);

        Project project2 = createProjectWithTimestamps(
            "Project 2",
            "Second test project",
            LocalDate.now(),
            LocalDate.now().plusMonths(4),
            Project.ProjectStatus.PLANNING
        );
        projectRepository.save(project2);

        // Act
        List<Project> allProjects = projectRepository.findAll();

        // Assert
        assertNotNull(allProjects);
        assertTrue(allProjects.size() >= 2);
        
        // Verify our test projects are in the list
        boolean foundProject1 = allProjects.stream()
                .anyMatch(p -> "Project 1".equals(p.getName()));
        boolean foundProject2 = allProjects.stream()
                .anyMatch(p -> "Project 2".equals(p.getName()));
        
        assertTrue(foundProject1, "Project 1 should be found in the list");
        assertTrue(foundProject2, "Project 2 should be found in the list");
    }

    @Test
    void testUpdateProject() {
        // Arrange
        Project project = createProjectWithTimestamps(
            "Update Test Project",
            "Project to test update functionality",
            LocalDate.now(),
            LocalDate.now().plusMonths(3),
            Project.ProjectStatus.PLANNING
        );
        Project savedProject = projectRepository.save(project);

        // Act - Update the project
        savedProject.setName("Updated Project Name");
        savedProject.setStatus(Project.ProjectStatus.ACTIVE);
        savedProject.setDescription("Updated description");
        Project updatedProject = projectRepository.save(savedProject);

        // Assert
        assertEquals("Updated Project Name", updatedProject.getName());
        assertEquals(Project.ProjectStatus.ACTIVE, updatedProject.getStatus());
        assertEquals("Updated description", updatedProject.getDescription());
    }

    @Test
    void testDeleteProject() {
        // Arrange
        Project project = createProjectWithTimestamps(
            "Delete Test Project",
            "Project to test delete functionality",
            LocalDate.now(),
            LocalDate.now().plusMonths(2),
            Project.ProjectStatus.PLANNING
        );
        Project savedProject = projectRepository.save(project);

        // Act
        projectRepository.delete(savedProject);

        // Assert
        Optional<Project> foundProject = projectRepository.findById(savedProject.getProjectId());
        assertFalse(foundProject.isPresent(), "Project should be deleted");
    }

    @Test
    void testFindProjectsByStatus() {
        // Arrange - Create projects with different statuses
        Project activeProject = createProjectWithTimestamps(
            "Active Project",
            "Active test project",
            LocalDate.now(),
            LocalDate.now().plusMonths(3),
            Project.ProjectStatus.ACTIVE
        );
        projectRepository.save(activeProject);

        Project planningProject = createProjectWithTimestamps(
            "Planning Project",
            "Planning test project",
            LocalDate.now(),
            LocalDate.now().plusMonths(6),
            Project.ProjectStatus.PLANNING
        );
        projectRepository.save(planningProject);

        // Act
        List<Project> activeProjects = projectRepository.findByStatus(Project.ProjectStatus.ACTIVE);
        List<Project> planningProjects = projectRepository.findByStatus(Project.ProjectStatus.PLANNING);

        // Assert
        assertNotNull(activeProjects);
        assertNotNull(planningProjects);
        
        // Verify active projects contain our test project
        boolean foundActiveProject = activeProjects.stream()
                .anyMatch(p -> "Active Project".equals(p.getName()));
        assertTrue(foundActiveProject, "Active project should be found");
        
        // Verify planning projects contain our test project
        boolean foundPlanningProject = planningProjects.stream()
                .anyMatch(p -> "Planning Project".equals(p.getName()));
        assertTrue(foundPlanningProject, "Planning project should be found");
    }

    @Test
    void testFindProjectsByProjectManager() {
        // Arrange - Create projects with the same project manager
        Project project1 = createProjectWithTimestamps(
            "Manager Project 1",
            "First project for the same manager",
            LocalDate.now(),
            LocalDate.now().plusMonths(2),
            Project.ProjectStatus.ACTIVE
        );
        projectRepository.save(project1);

        Project project2 = createProjectWithTimestamps(
            "Manager Project 2",
            "Second project for the same manager",
            LocalDate.now(),
            LocalDate.now().plusMonths(4),
            Project.ProjectStatus.PLANNING
        );
        projectRepository.save(project2);

        // Act
        List<Project> managerProjects = projectRepository.findByProjectManagerEmployeeId(testEmployee.getEmployeeId());

        // Assert
        assertNotNull(managerProjects);
        assertTrue(managerProjects.size() >= 2);
        
        // Verify both projects are found
        boolean foundProject1 = managerProjects.stream()
                .anyMatch(p -> "Manager Project 1".equals(p.getName()));
        boolean foundProject2 = managerProjects.stream()
                .anyMatch(p -> "Manager Project 2".equals(p.getName()));
        
        assertTrue(foundProject1, "Manager Project 1 should be found");
        assertTrue(foundProject2, "Manager Project 2 should be found");
    }
}
