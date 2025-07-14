package org.example.service.User.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.example.dto.response.UserResponseDTO;
import org.example.repository.UserRepository;
import org.example.service.User.UserService;
import org.springframework.stereotype.Service;
import org.example.model.User;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO getInfoUser(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(
            user.getUserId(),
            user.getUsername(),
            user.getEmployee() != null ? user.getEmployee().getEmployeeId() : null,
            user.getEmployee() != null ? user.getEmployee().getFirstName() : null,
            user.getEmployee() != null ? user.getEmployee().getLastName() : null,
            user.getEmployee() != null ? user.getEmployee().getEmail() : null
        );
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<UserResponseDTO> users = userRepository.findAll().stream()
            .map(user -> new UserResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmployee() != null ? user.getEmployee().getEmployeeId() : null,
                user.getEmployee() != null ? user.getEmployee().getFirstName() : null,
                user.getEmployee() != null ? user.getEmployee().getLastName() : null,
                user.getEmployee() != null ? user.getEmployee().getEmail() : null
            ))
            .collect(Collectors.toList());

            return users;
    }
}
