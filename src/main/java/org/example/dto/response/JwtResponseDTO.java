package org.example.dto.response;

import java.util.List;

public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private Integer userId;
    private String userName;
    private List<String> roles;

    public JwtResponseDTO(String token, Integer userId, String userName, List<String> roles) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
} 