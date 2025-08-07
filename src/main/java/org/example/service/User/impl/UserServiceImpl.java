package org.example.service.User.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.example.dto.response.UserResponseDTO;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.User.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", userName);
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            logger.error("User not found with username: {}", userName);
            throw new UsernameNotFoundException("User not found with username: " + userName);
        }
        logger.info("User found: {}, isActive: {}, authorities: {}", 
                   user.getUserName(), user.getIsActive(), user.getAuthorities());
        return user;
    }

    @Override
    public UserResponseDTO getInfoUser(String userName, String password) {
        User user = userRepository.findByUserNameAndPassword(userName, password);
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(
            user.getEmployee() != null ? user.getEmployee().getEmployeeId() : null,
            user.getEmployee() != null ? user.getEmployee().getFirstName() : null,
            user.getEmployee() != null ? user.getEmployee().getLastName() : null,
            user.getEmployee() != null ? user.getEmployee().getEmail() : null,
            user.getEmployee() != null && user.getEmployee().getManager() != null ? user.getEmployee().getManager().getEmployeeId() : null
        );
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<UserResponseDTO> users = userRepository.findAll().stream()
            .map(user -> new UserResponseDTO(
                user.getEmployee() != null ? user.getEmployee().getEmployeeId() : null,
                user.getEmployee() != null ? user.getEmployee().getFirstName() : null,
                user.getEmployee() != null ? user.getEmployee().getLastName() : null,
                user.getEmployee() != null ? user.getEmployee().getEmail() : null,
                user.getEmployee() != null && user.getEmployee().getManager() != null ? user.getEmployee().getManager().getEmployeeId() : null
            ))
            .collect(Collectors.toList());

            return users;
    }
}
