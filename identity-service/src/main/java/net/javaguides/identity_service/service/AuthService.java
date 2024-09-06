package net.javaguides.identity_service.service;

import net.javaguides.identity_service.entity.UserCredential;

public interface AuthService {
    String saveUser(UserCredential userCredential);
    String generateToken(String username);
    void validateToken(String token);
}
