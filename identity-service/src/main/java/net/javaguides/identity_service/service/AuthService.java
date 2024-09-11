package net.javaguides.identity_service.service;

import jakarta.servlet.http.HttpServletResponse;
import net.javaguides.identity_service.entity.UserCredential;

public interface AuthService {
    String saveUser(UserCredential userCredential);
    String generateToken(String username, HttpServletResponse response);
    void validateToken(String token);
}
