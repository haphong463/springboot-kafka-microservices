package net.javaguides.identity_service.service;

public interface JwtService {
    void validateToken(final String token);
    String generateToken(String userName);
}
