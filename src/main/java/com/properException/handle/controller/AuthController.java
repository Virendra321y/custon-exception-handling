package com.properException.handle.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.properException.handle.dto.ApiResponse;
import com.properException.handle.entity.Role;
import com.properException.handle.entity.Role.RoleName;
import com.properException.handle.entity.User;
import com.properException.handle.reposetory.RoleRepository;
import com.properException.handle.reposetory.UserRepository;
import com.properException.handle.securityConfig.JwtTokenProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class SignUpRequest {
        private String name;
        private String email;
        private String contact;
        private String password;
        private Set<String> roles;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class JwtAuthResponse {
        private String token;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> authenticateUser(
            @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        String jwt = tokenProvider.generateToken(authentication);
        ApiResponse<JwtAuthResponse> response = ApiResponse.<JwtAuthResponse>builder()
            .success(true)
            .message("Login successful")
            .data(new JwtAuthResponse(jwt))
            .statusCode(HttpStatus.OK.value())
            .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(
            @RequestBody SignUpRequest signUpRequest) {
                if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            ApiResponse<String> resp = ApiResponse.<String>builder()
                .success(false)
                .message("Email is already in use")
                .data(null)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
                if (userRepository.existsByContact(signUpRequest.getContact())) {
            ApiResponse<String> resp = ApiResponse.<String>builder()
                .success(false)
                .message("Contact number is already in use")
                .data(null)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }
        User user = User.builder()
            .name(signUpRequest.getName())
            .email(signUpRequest.getEmail())
            .contact(signUpRequest.getContact())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .build();

        Set<Role> roles = new HashSet<>();
        for (String role : signUpRequest.getRoles()) {
            RoleName roleName = RoleName.valueOf(role);
            Role rm = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(rm);
        }
        user.setRoles(roles);
        userRepository.save(user);

        ApiResponse<String> response = ApiResponse.<String>builder()
            .success(true)
            .message("User registered successfully")
            .data("User registered with id " + user.getId())
            .statusCode(HttpStatus.CREATED.value())
            .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
