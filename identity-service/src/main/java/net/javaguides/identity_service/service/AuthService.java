package net.javaguides.identity_service.service;

import jakarta.servlet.http.HttpServletResponse;
import net.javaguides.identity_service.dto.AuthRequest;
import net.javaguides.identity_service.dto.SignUpRequest;
import net.javaguides.identity_service.entity.UserCredential;

public interface AuthService {
    String saveUser(SignUpRequest userCredential);
    String generateToken(AuthRequest authRequest, HttpServletResponse response);
    void validateToken(String token);
}
