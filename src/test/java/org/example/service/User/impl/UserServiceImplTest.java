package org.example.service.User.impl;

import org.example.dto.response.UserResponseDTO;
import org.example.model.Employee;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Employee testEmployee;
    private Employee testManager;

    @BeforeEach
    void setUp() {
        // Create test manager
        testManager = new Employee();
        testManager.setEmployeeId(1);
        testManager.setFirstName("John");
        testManager.setLastName("Manager");
        testManager.setEmail("john.manager@company.com");

        // Create test employee
        testEmployee = new Employee();
        testEmployee.setEmployeeId(2);
        testEmployee.setFirstName("Jane");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("jane.doe@company.com");
        testEmployee.setManager(testManager);

        // Create test user
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUserName("jane.doe");
        testUser.setPassword("password123");
        testUser.setEmployee(testEmployee);
        testUser.setIsActive(true);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        String username = "jane.doe";
        when(userRepository.findByUserName(username)).thenReturn(testUser);

        // Act
        UserDetails result = userService.loadUserByUsername(username);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("password123", result.getPassword());
        assertTrue(result.isEnabled());
        verify(userRepository, times(1)).findByUserName(username);
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        // Arrange
        String username = "nonexistent.user";
        when(userRepository.findByUserName(username)).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userService.loadUserByUsername(username)
        );

        assertEquals("User not found with username: " + username, exception.getMessage());
        verify(userRepository, times(1)).findByUserName(username);
    }

    @Test
    void getInfoUser_WhenValidCredentials_ShouldReturnUserResponseDTO() {
        // Arrange
        String username = "jane.doe";
        String password = "password123";
        when(userRepository.findByUserNameAndPassword(username, password)).thenReturn(testUser);

        // Act
        UserResponseDTO result = userService.getInfoUser(username, password);

        // Assert
        assertNotNull(result);
        assertEquals(testEmployee.getEmployeeId(), result.employeeId());
        assertEquals(testEmployee.getFirstName(), result.employeeFirstName());
        assertEquals(testEmployee.getLastName(), result.employeeLastName());
        assertEquals(testEmployee.getEmail(), result.employeeEmail());
        assertEquals(testManager.getEmployeeId(), result.managerId());
        verify(userRepository, times(1)).findByUserNameAndPassword(username, password);
    }

    @Test
    void getInfoUser_WhenInvalidCredentials_ShouldReturnNull() {
        // Arrange
        String username = "jane.doe";
        String password = "wrongpassword";
        when(userRepository.findByUserNameAndPassword(username, password)).thenReturn(null);

        // Act
        UserResponseDTO result = userService.getInfoUser(username, password);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByUserNameAndPassword(username, password);
    }

    @Test
    void getInfoUser_WhenUserHasNoEmployee_ShouldReturnNullValues() {
        // Arrange
        User userWithoutEmployee = new User();
        userWithoutEmployee.setUserName("user.without.employee");
        userWithoutEmployee.setPassword("password123");
        userWithoutEmployee.setEmployee(null);

        String username = "user.without.employee";
        String password = "password123";
        when(userRepository.findByUserNameAndPassword(username, password)).thenReturn(userWithoutEmployee);

        // Act
        UserResponseDTO result = userService.getInfoUser(username, password);

        // Assert
        assertNotNull(result);
        assertNull(result.employeeId());
        assertNull(result.employeeFirstName());
        assertNull(result.employeeLastName());
        assertNull(result.employeeEmail());
        assertNull(result.managerId());
        verify(userRepository, times(1)).findByUserNameAndPassword(username, password);
    }

    @Test
    void getAllUsers_WhenUsersExist_ShouldReturnListOfUserResponseDTOs() {
        // Arrange
        User user1 = testUser;
        
        User user2 = new User();
        user2.setUserId(2);
        user2.setUserName("john.smith");
        user2.setPassword("password456");
        
        Employee employee2 = new Employee();
        employee2.setEmployeeId(3);
        employee2.setFirstName("John");
        employee2.setLastName("Smith");
        employee2.setEmail("john.smith@company.com");
        employee2.setManager(null); // No manager
        
        user2.setEmployee(employee2);
        user2.setIsActive(true);

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserResponseDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Check first user (with manager)
        UserResponseDTO firstUser = result.get(0);
        assertEquals(testEmployee.getEmployeeId(), firstUser.employeeId());
        assertEquals(testEmployee.getFirstName(), firstUser.employeeFirstName());
        assertEquals(testEmployee.getLastName(), firstUser.employeeLastName());
        assertEquals(testEmployee.getEmail(), firstUser.employeeEmail());
        assertEquals(testManager.getEmployeeId(), firstUser.managerId());
        
        // Check second user (without manager)
        UserResponseDTO secondUser = result.get(1);
        assertEquals(employee2.getEmployeeId(), secondUser.employeeId());
        assertEquals(employee2.getFirstName(), secondUser.employeeFirstName());
        assertEquals(employee2.getLastName(), secondUser.employeeLastName());
        assertEquals(employee2.getEmail(), secondUser.employeeEmail());
        assertNull(secondUser.managerId());
        
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_WhenNoUsersExist_ShouldReturnEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<UserResponseDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_WhenUserHasNoEmployee_ShouldHandleNullEmployee() {
        // Arrange
        User userWithoutEmployee = new User();
        userWithoutEmployee.setUserId(3);
        userWithoutEmployee.setUserName("user.without.employee");
        userWithoutEmployee.setPassword("password789");
        userWithoutEmployee.setEmployee(null);
        userWithoutEmployee.setIsActive(true);

        List<User> users = Arrays.asList(userWithoutEmployee);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserResponseDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        UserResponseDTO userDTO = result.get(0);
        assertNull(userDTO.employeeId());
        assertNull(userDTO.employeeFirstName());
        assertNull(userDTO.employeeLastName());
        assertNull(userDTO.employeeEmail());
        assertNull(userDTO.managerId());
        
        verify(userRepository, times(1)).findAll();
    }
}
