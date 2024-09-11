package net.javaguides.identity_service.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import net.javaguides.identity_service.entity.UserCredential;
import net.javaguides.identity_service.exception.AuthException;
import net.javaguides.identity_service.repository.UserCredentialRepository;
import net.javaguides.identity_service.service.AuthService;
import net.javaguides.identity_service.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserCredentialRepository userCredentialRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userCredentialRepository = userCredentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public String saveUser(UserCredential userCredential) {
        boolean existingUsername = checkExistingUsername(userCredential.getName());
        if(existingUsername){
            throw new AuthException("Username already exists in the database!", HttpStatus.BAD_REQUEST);
        }

        userCredential.setPassword(passwordEncoder.encode(userCredential.getPassword()));
        userCredentialRepository.save(userCredential);
        return "user added to the system";
    }

    @Override
    public String generateToken(String username, HttpServletResponse response) {
        String token = jwtService.generateToken(username);

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);
        response.addCookie(cookie);
        return token;
    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    private boolean checkExistingUsername(String username){
        return userCredentialRepository.findByName(username).isPresent();
    }
}
