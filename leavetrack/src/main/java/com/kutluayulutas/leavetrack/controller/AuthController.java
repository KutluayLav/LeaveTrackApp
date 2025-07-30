package com.kutluayulutas.leavetrack.controller;

import com.kutluayulutas.leavetrack.dto.request.LoginRequest;
import com.kutluayulutas.leavetrack.dto.request.RegisterRequest;
import com.kutluayulutas.leavetrack.dto.request.TokenRefreshRequest;
import com.kutluayulutas.leavetrack.dto.response.AuthResponse;
import com.kutluayulutas.leavetrack.dto.response.SuccessResponse;
import com.kutluayulutas.leavetrack.dto.response.UserDTO;
import com.kutluayulutas.leavetrack.service.AuthService;
import com.kutluayulutas.leavetrack.service.IAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<AuthResponse>> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(
                SuccessResponse.<AuthResponse>builder()
                        .message("Login successful")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(authResponse)
                        .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<UserDTO>> register(@RequestBody RegisterRequest registerRequest) {
        UserDTO userDTO = authService.register(registerRequest);
        return ResponseEntity.ok(
                SuccessResponse.<UserDTO>builder()
                        .message("Registration successful")
                        .status(HttpStatus.CREATED)
                        .timestamp(LocalDateTime.now())
                        .data(userDTO)
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<Void>> logout(Authentication authentication) {
        String email = authentication.getName();
        authService.logout(email);

        return ResponseEntity.ok(
                SuccessResponse.<Void>builder()
                        .message("Logout successful")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<SuccessResponse<AuthResponse>> refreshToken(@RequestBody TokenRefreshRequest request) {
        AuthResponse authResponse = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(
                SuccessResponse.<AuthResponse>builder()
                        .message("Token refreshed successfully")
                        .status(HttpStatus.OK)
                        .timestamp(LocalDateTime.now())
                        .data(authResponse)
                        .build()
        );
    }




}
