package org.example.dto.request;

import java.util.Set;

public class SignupRequestDTO {
    private String userName;
    private String password;
    private Integer employeeId;
    private Set<String> roles;

    public SignupRequestDTO() {}

    public SignupRequestDTO(String userName, String password, Integer employeeId, Set<String> roles) {
        this.userName = userName;
        this.password = password;
        this.employeeId = employeeId;
        this.roles = roles;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
} 