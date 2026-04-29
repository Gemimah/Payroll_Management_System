package com.erp.employee_system.dto.auth;

public record LoginResponse(
        String token,
        String tokenType
) {
}

