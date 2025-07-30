package com.kutluayulutas.leavetrack.service;


import com.kutluayulutas.leavetrack.dto.request.LoginRequest;
import com.kutluayulutas.leavetrack.dto.request.RegisterRequest;
import com.kutluayulutas.leavetrack.dto.response.AuthResponse;
import com.kutluayulutas.leavetrack.dto.response.UserDTO;
import com.kutluayulutas.leavetrack.exception.CustomAuthenticationException;
import com.kutluayulutas.leavetrack.exception.EmailAlreadyUsedException;
import com.kutluayulutas.leavetrack.exception.NotFoundException;
import com.kutluayulutas.leavetrack.mapper.UserMapper;
import com.kutluayulutas.leavetrack.model.Department;
import com.kutluayulutas.leavetrack.model.Role;
import com.kutluayulutas.leavetrack.model.User;
import com.kutluayulutas.leavetrack.repository.DepartmentRepository;
import com.kutluayulutas.leavetrack.repository.UserRepository;
import com.kutluayulutas.leavetrack.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService, UserRepository userRepository, PasswordEncoder passwordEncoder, DepartmentRepository departmentRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.departmentRepository = departmentRepository;
    }


    @Override
    public UserDTO register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("Email is already in use.");
        }


        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found with id: " + request.getDepartmentId()));


        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNo(request.getPhoneNo())
                .authorities(Set.of(Role.ROLE_USER))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .department(department)
                .credentialsNonExpired(true)
                .isEnabled(true)
                .createdDate(LocalDateTime.now())
                .build();

        userRepository.save(user);

        UserDTO userDTO = UserMapper.toDTO(user);

        return userDTO;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

           // SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtTokenProvider.generateToken(authentication);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found with email: " + request.getEmail()));

            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);

            String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

            UserDTO userDTO = UserMapper.toDTO(user);

            logger.info("User '{}' logged in successfully", request.getEmail());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(userDTO)
                    .build();

        } catch (BadCredentialsException ex) {
            logger.warn("Login failed for user '{}': Bad credentials", request.getEmail());
            throw new CustomAuthenticationException("Invalid email or password.");
        } catch (Exception ex) {
            logger.error("Login failed for user '{}': {}", request.getEmail(), ex.getMessage());
            throw new CustomAuthenticationException("An unexpected error occurred during login.");
        }
    }


    @Override
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        refreshTokenService.deleteByEmail(email);
        logger.info("User '{}' logged out successfully", email);
    }

    @Override
    public AuthResponse refreshToken(String requestRefreshToken) {
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    User user = refreshToken.getUser();
                    String token = jwtTokenProvider.generateTokenWithEmail(user.getEmail());
                    String newRefreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();
                    UserDTO userDTO = UserMapper.toDTO(user);

                    logger.info("Refresh token used for user '{}'", user.getEmail());

                    return AuthResponse.builder()
                            .accessToken(token)
                            .refreshToken(newRefreshToken)
                            .user(userDTO)
                            .build();
                })
                .orElseThrow(() -> new CustomAuthenticationException("Refresh token is not in database!"));
    }




}
