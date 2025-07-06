package com.properException.handle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.properException.handle.dto.ApiResponse;
import com.properException.handle.dto.UserDto;
import com.properException.handle.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("#id == principal.username or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createUser(
            @Valid @RequestBody UserDto dto) {
        UserDto created = userService.createUser(dto);
        ApiResponse<UserDto> resp = ApiResponse.<UserDto>builder()
            .success(true)
            .message("User created successfully")
            .data(created)
            .statusCode(HttpStatus.CREATED.value()) 
            .build();
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUser(
            @PathVariable("id") Long id) {
        UserDto dto = userService.getUserById(id);
        ApiResponse<UserDto> resp = ApiResponse.<UserDto>builder()
            .success(true)
            .message("User fetched successfully")
            .data(dto)
            .statusCode(HttpStatus.OK.value()) 
            .build();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserDto dto) {
        UserDto updated = userService.updateUser(id, dto);
        ApiResponse<UserDto> resp = ApiResponse.<UserDto>builder()
            .success(true)
            .message("User updated successfully")
            .data(updated)
            .build();
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> list = userService.getAllUsers();
        ApiResponse<List<UserDto>> resp = ApiResponse.<List<UserDto>>builder()
            .success(true)
            .message("Users fetched successfully")
            .data(list)
            .statusCode(HttpStatus.OK.value())
            .build();
        return ResponseEntity.ok(resp);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> resp = ApiResponse.<Void>builder()
            .success(true)
            .message("User deleted successfully")
            .data(null)
            .statusCode(HttpStatus.OK.value())
            .build();
        return ResponseEntity.ok(resp);
    }
}

