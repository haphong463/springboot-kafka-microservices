package net.javaguides.identity_service.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.SecurityContext;
import lombok.RequiredArgsConstructor;
import net.javaguides.identity_service.config.CustomUserDetails;
import net.javaguides.identity_service.dto.AuthRequest;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;

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
            if(signUpRequest.getRoles() == null){
                Role role = roleRepository.findByName(ERole.CUSTOMER)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                roles.add(role);
            }else{
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
            }
            userCredential.setRoles(roles);
            userCredentialRepository.save(userCredential);
            return "User added to the system!";
        }catch(Exception e){
            throw new RuntimeException("Error registering user: " + e.getMessage());
        }
    }

    @Override
    public String generateToken(AuthRequest authRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        Optional<UserCredential> optionalUser = userCredentialRepository.findByName(authRequest.getUsername());
        if(!optionalUser.isPresent()){
            throw new AuthException("Invalid credentials! Please try again!",HttpStatus.UNAUTHORIZED);
        }

        UserCredential userCredential = optionalUser.get();
        SecurityContextHolder.getContext().setAuthentication(authentication);



        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(authentication);

        Cookie cookie = new Cookie("token", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);
        response.addCookie(cookie);
        return jwtToken;
    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    private boolean checkExistingUsername(String username){
        return userCredentialRepository.findByName(username).isPresent();
    }
}
