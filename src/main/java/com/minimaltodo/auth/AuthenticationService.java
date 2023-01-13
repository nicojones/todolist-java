package com.minimaltodo.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minimaltodo.config.JwtService;
import com.minimaltodo.list.user.Role;
import com.minimaltodo.list.user.User;
import com.minimaltodo.list.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService; 

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) throws IllegalStateException {
        User user = User.builder()
        .email(request.getEmail())
        .name(request.getName())
        .created(new Date(System.currentTimeMillis()))
        .updated(new Date(System.currentTimeMillis()))
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .build();

        userService.addnewUser(user);
        
        String jwtToken = jwtService.generateToken(userClaims(user), user);

        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws UsernameNotFoundException {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        User user = userService.loadUserByUsername(request.getEmail());

        String jwtToken = jwtService.generateToken(userClaims(user), user);

        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .build();
    }

    private Map<String, Object> userClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("role", user.getRole());
        claims.put("username", user.getUsername());

        return claims;
    }

}
