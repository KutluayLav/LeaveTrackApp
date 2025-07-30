package com.kutluayulutas.leavetrack.controller;

import com.kutluayulutas.leavetrack.dto.request.RegisterRequest;
import com.kutluayulutas.leavetrack.dto.response.SuccessResponse;
import com.kutluayulutas.leavetrack.dto.response.UserDTO;
import com.kutluayulutas.leavetrack.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final IUserService userService;

    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(SuccessResponse.<List<UserDTO>>builder()
                .message("All users retrieved successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .data(users)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<UserDTO>> getUserById(@PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(SuccessResponse.<UserDTO>builder()
                .message("User retrieved successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .data(user)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<UserDTO>> createUser(@RequestBody RegisterRequest request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.<UserDTO>builder()
                .message("User created successfully")
                .status(HttpStatus.CREATED)
                .timestamp(LocalDateTime.now())
                .data(createdUser)
                .build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<UserDTO>> updateUser(@PathVariable String id, @RequestBody RegisterRequest request) {
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(SuccessResponse.<UserDTO>builder()
                .message("User updated successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .data(updatedUser)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(SuccessResponse.<Void>builder()
                .message("User deleted successfully")
                .status(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
