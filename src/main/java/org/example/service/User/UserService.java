package org.example.service.User;

import org.example.dto.response.UserResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserResponseDTO getInfoUser(String userName, String password);
    List<UserResponseDTO> getAllUsers();
}
