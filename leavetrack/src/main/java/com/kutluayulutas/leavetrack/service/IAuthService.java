package com.kutluayulutas.leavetrack.service;

import com.kutluayulutas.leavetrack.dto.request.LoginRequest;
import com.kutluayulutas.leavetrack.dto.request.RegisterRequest;
import com.kutluayulutas.leavetrack.dto.response.AuthResponse;
import com.kutluayulutas.leavetrack.dto.response.UserDTO;

public interface IAuthService {

    UserDTO register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String token);
    AuthResponse refreshToken(String requestRefreshToken);

}
