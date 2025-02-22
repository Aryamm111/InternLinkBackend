package com.internlink.internlink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.internlink.internlink.model.User;
import com.internlink.internlink.util.JwtUtil;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public User findByUsername(String username) {
        String[] collections = { "students", "facultySupervisors", "companySupervisors", "hrManagers" };

        for (String collection : collections) {
            Query query = new Query();
            query.addCriteria(Criteria.where("username").is(username));

            User foundUser = mongoTemplate.findOne(query, User.class, collection);
            if (foundUser != null) {
                return foundUser;
            }
        }
        return null;
    }

    public String login(String username, String password) {
        User foundUser = findByUsername(username);
        if (foundUser != null) {
            System.out.println("User found: " + foundUser.getUsername());

            if (passwordEncoder.matches(password, foundUser.getPassword())) {
                // ✅ Pass userId, username, and role correctly
                return jwtUtil.generateToken(foundUser.getId(), foundUser.getUsername(), foundUser.getUserRole());
            }
        }

        System.out.println("No user found or invalid password.");
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getUserRole())
                .build();
    }
}
