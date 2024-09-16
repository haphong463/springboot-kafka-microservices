package net.javaguides.identity_service.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.javaguides.identity_service.dto.SignUpRequest;
import net.javaguides.identity_service.entity.Role;
import net.javaguides.identity_service.entity.UserCredential;
import net.javaguides.identity_service.enums.ERole;
import net.javaguides.identity_service.exception.AuthException;
import net.javaguides.identity_service.repository.RoleRepository;
import net.javaguides.identity_service.repository.UserCredentialRepository;
import net.javaguides.identity_service.service.AuthService;
import net.javaguides.identity_service.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    @Override
    public String saveUser(SignUpRequest signUpRequest) {
        try {
            boolean existingUsername = checkExistingUsername(signUpRequest.getName());
            if(existingUsername){
                throw new AuthException("Username already exists in the database!", HttpStatus.BAD_REQUEST);
            }
            UserCredential userCredential = new UserCredential();
            userCredential.setName(signUpRequest.getName());
            userCredential.setEmail(signUpRequest.getEmail());
            userCredential.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            Set<Role> roles = new HashSet<>();
            for (String roleName : signUpRequest.getRoles()) {
                ERole eRole;
                try {
                    eRole = ERole.valueOf(roleName.toUpperCase());  // Chuyển vai trò sang chữ in hoa
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid role name: " + roleName);
                }

                Role role = roleRepository.findByName(eRole)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                roles.add(role);
            }
            userCredential.setRoles(roles);
            userCredentialRepository.save(userCredential);
            return "User added to the system!";
        }catch(Exception e){
            throw new RuntimeException("Error registering user: " + e.getMessage());
        }
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
