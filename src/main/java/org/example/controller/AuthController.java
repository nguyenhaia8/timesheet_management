package org.example.controller;

import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.SignupRequestDTO;
import org.example.dto.response.JwtResponseDTO;
import org.example.dto.response.MessageResponseDTO;
import org.example.dto.response.UserResponseDTO;
import org.example.dto.response.LoginErrorResponseDTO;
import org.example.model.Employee;
import org.example.model.Role;
import org.example.model.User;
import org.example.model.UserRole;
import org.example.repository.DepartmentRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.example.repository.UserRoleRepository;
import org.example.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://127.0.0.1:3000", "http://127.0.0.1:3001", "http://127.0.0.1:3002"}, 
             allowedHeaders = "*", 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},
             allowCredentials = "true",
             maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @GetMapping("/test/{userName}")
    public ResponseEntity<?> testUser(@PathVariable String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.ok("User not found");
        }
        
        return ResponseEntity.ok("User found: " + user.getUserName() + 
                               ", isActive: " + user.getIsActive() + 
                               ", authorities: " + user.getAuthorities());
    }

    @GetMapping("/test-password/{userName}")
    public ResponseEntity<?> testPassword(@PathVariable String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.ok("User not found");
        }
        
        String storedPassword = user.getPassword();
        boolean matchesPassword = passwordEncoder.matches("password", storedPassword);
        boolean matchesPassword123 = passwordEncoder.matches("password123", storedPassword);
        
        return ResponseEntity.ok("User: " + user.getUserName() + 
                               ", stored password: " + storedPassword + 
                               ", matches 'password': " + matchesPassword + 
                               ", matches 'password123': " + matchesPassword123);
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok("Auth service is running");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            System.out.println("Attempting login for user: " + loginRequest.getUserName());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

            System.out.println("Authentication successful for user: " + loginRequest.getUserName());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            User userDetails = (User) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Update last login
            userDetails.setLastLogin(LocalDateTime.now());
            userRepository.save(userDetails);

            // Create UserResponseDTO with employee information
            UserResponseDTO infoUser = new UserResponseDTO(
                userDetails.getEmployee() != null ? userDetails.getEmployee().getEmployeeId() : null,
                userDetails.getEmployee() != null ? userDetails.getEmployee().getFirstName() : null,
                userDetails.getEmployee() != null ? userDetails.getEmployee().getLastName() : null,
                userDetails.getEmployee() != null ? userDetails.getEmployee().getEmail() : null,
                userDetails.getEmployee() != null && userDetails.getEmployee().getManager() != null ? userDetails.getEmployee().getManager().getEmployeeId() : null
            );

            System.out.println("Login successful, returning JWT token");
            return ResponseEntity.ok(new JwtResponseDTO(jwt, userDetails.getUserId(), userDetails.getUserName(), roles, infoUser));
        } catch (Exception e) {
            System.out.println("Login failed with exception: " + e.getMessage());
            e.printStackTrace();
            String errorMessage = "Invalid username or password. Please check your credentials and try again.";
            return ResponseEntity.status(401).body(new LoginErrorResponseDTO(401, "AUTHENTICATION_FAILED", errorMessage));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequestDTO signupRequest) {
        System.out.println("Signup request received for user: " + signupRequest.getUserName());
        
        try {
            if (userRepository.findByUserName(signupRequest.getUserName()) != null) {
                System.out.println("Username already exists: " + signupRequest.getUserName());
                return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: Username is already taken!"));
            }

        // Validate department exists
        if (signupRequest.getDepartmentId() != null) {
            var departmentOpt = departmentRepository.findById(signupRequest.getDepartmentId());
            if (departmentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: Department not found!"));
            }
        }

        // Validate manager exists if provided
        Employee manager = null;
        if (signupRequest.getManagerId() != null) {
            var managerOpt = employeeRepository.findById(signupRequest.getManagerId());
            if (managerOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponseDTO("Error: Manager not found!"));
            }
            manager = managerOpt.get();
        }

        // Create new employee first
        Employee employee = new Employee();
        employee.setFirstName(signupRequest.getFirstName());
        employee.setLastName(signupRequest.getLastName());
        employee.setEmail(signupRequest.getEmail());
        employee.setPosition(signupRequest.getPosition());
        
        if (signupRequest.getDepartmentId() != null) {
            employee.setDepartment(departmentRepository.findById(signupRequest.getDepartmentId()).get());
        }
        
        if (manager != null) {
            employee.setManager(manager);
        }
        
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());

        employee = employeeRepository.save(employee);

        // Create user linked to the new employee
        User user = new User();
        user.setUserName(signupRequest.getUserName());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmployee(employee);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName("EMPLOYEE")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                Role foundRole = roleRepository.findByRoleName(role.toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found."));
                roles.add(foundRole);
            });
        }

        // Create UserRole entries
        for (Role role : roles) {
            UserRole userRole = new UserRole(user, role);
            userRoleRepository.save(userRole);
        }

        return ResponseEntity.ok(new MessageResponseDTO("User registered successfully!"));
        } catch (Exception e) {
            System.out.println("Error during signup: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new MessageResponseDTO("Error: " + e.getMessage()));
        }
    }
} 