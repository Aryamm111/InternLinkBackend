package com.internlink.internlink.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.User;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    // Retrieves the authenticated user's ID from the security context
    public String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the user's email from the authentication object
        String email = authentication.getName();

        // Find the user by their email in the database
        User user = userService.findByEmail(email);

        // Return user ID if found, otherwise return null
        return (user != null) ? user.getId() : null;
    }

    // Retrieves the authenticated user's role from the security context
    public String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the first authority (role) assigned to the user
        return authentication.getAuthorities().iterator().next().getAuthority();
    }
}
