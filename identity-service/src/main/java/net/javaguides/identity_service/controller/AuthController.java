package net.javaguides.identity_service.controller;


import net.javaguides.common_lib.dto.ApiResponse;
import net.javaguides.identity_service.annotation.CurrentUser;
import net.javaguides.identity_service.dto.AuthRequest;
import net.javaguides.identity_service.dto.UserDto;
import net.javaguides.identity_service.entity.UserCredential;
import net.javaguides.identity_service.service.AuthService;
import net.javaguides.identity_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserService userService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCredential userCredential) {
        return authService.saveUser(userCredential);
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<String>> getToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authenticate.isAuthenticated()) {
                String generateToken = authService.generateToken(authRequest.getUsername());
                ApiResponse<String> apiResponse = new ApiResponse<>(generateToken, HttpStatus.OK.value());
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                ApiResponse<String> apiResponse = new ApiResponse<>("Invalid access!", HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);            }
        }catch(Exception e){
            ApiResponse<String> apiResponse = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestParam("token") String token) {
        try {
            authService.validateToken(token);
            ApiResponse<String> apiResponse = new ApiResponse<>("Token is valid", HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }catch(Exception e){
            ApiResponse<String> apiResponse = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getCurrentUser(@CurrentUser UserDetails currentUser) {
        try {
            UserDto userDto = userService.getUserByUsername(currentUser.getUsername());
            ApiResponse<UserDto> apiResponse = new ApiResponse<>(userDto, HttpStatus.OK.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<String> apiResponse = new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
