package com.minimaltodo.list.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/search")
    public ResponseEntity<List<String>> searchUsers(
        @RequestParam("q") String email,
        @AuthenticationPrincipal User user
    ) {
        List<String> results = service
            .findByEmail(email)
            .stream()
            .filter(u -> !u.getEmail().equals(user.getEmail()))
            .map(u -> u.getEmail())
            .toList();
        return ResponseEntity.ok(results);
    }

}
