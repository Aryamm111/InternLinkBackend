package com.internlink.internlink.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.User;

@Service
public class AuthService {

    private final UserService userService; // Inject UserService

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get authenticated username
        User user = userService.findByUsername(username); // Fetch user details
        return (user != null) ? user.getId() : null; // Return user ID
    }

    public String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().iterator().next().getAuthority(); // Get role
    }
}
