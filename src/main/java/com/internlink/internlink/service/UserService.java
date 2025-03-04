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

@Service
public class UserService implements UserDetailsService {

    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername called with username: " + username); // Debug log

        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return user;
    }

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

    // Register or create a new user with password encoding
    public User createUser(String username, String password, String role) {
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setUserRole(role);

        mongoTemplate.save(user);
        return user;
    }
}
