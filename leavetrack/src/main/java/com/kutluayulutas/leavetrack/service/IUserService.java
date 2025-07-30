package com.kutluayulutas.leavetrack.service;

import com.kutluayulutas.leavetrack.dto.request.RegisterRequest;
import com.kutluayulutas.leavetrack.dto.response.UserDTO;

import java.util.List;

public interface IUserService {
    
    List<UserDTO> getAllUsers();
    
    UserDTO getUserById(String id);
    
    UserDTO createUser(RegisterRequest request);
    
    UserDTO updateUser(String id, RegisterRequest request);
    
    void deleteUser(String id);
}
