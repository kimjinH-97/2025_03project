package com.example.finalproject.dto.login;

public class ApiResponse {
    private boolean success;
    private String message;

    // 생성자, getters, setters
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}