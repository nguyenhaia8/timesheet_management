package org.example.dto.response;

public class LoginErrorResponseDTO {
    private int statusCode;
    private String errorType;
    private String message;
    private boolean success;

    public LoginErrorResponseDTO(int statusCode, String errorType, String message) {
        this.statusCode = statusCode;
        this.errorType = errorType;
        this.message = message;
        this.success = false;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
