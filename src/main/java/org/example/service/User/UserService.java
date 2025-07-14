package org.example.service.User;

import java.util.List;

import org.example.dto.response.UserResponseDTO;

public interface UserService {
    UserResponseDTO getInfoUser(String username, String password);
    List<UserResponseDTO> getAllUsers();
}
