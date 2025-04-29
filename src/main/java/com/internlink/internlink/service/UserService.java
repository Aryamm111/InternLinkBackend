package com.internlink.internlink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "userDetails", key = "#email")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("loadUserByEmail called with email: " + email);

        User user = findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    @Cacheable(value = "usersByEmail", key = "#email")
    public User findByEmail(String email) {
        long startTime = System.nanoTime();

        String[] collections = { "students", "facultySupervisors", "companySupervisors", "hrmanagers" };

        for (String collection : collections) {
            Query query = new Query();
            query.addCriteria(Criteria.where("email").is(email));
            User foundUser = mongoTemplate.findOne(query, User.class, collection);
            if (foundUser != null) {
                long endTime = System.nanoTime();
                System.out.println("Sequential Execution Time: " + (endTime - startTime) + " ns");
                return foundUser;
            }
        }

        long endTime = System.nanoTime();
        System.out.println("Sequential Execution Time: " + (endTime - startTime) + " ns");
        return null;
    }

    public boolean userExistsByEmail(String email) {
        String[] collections = { "students", "facultySupervisors", "companySupervisors", "hrmanagers" };

        for (String collection : collections) {
            Query query = new Query(Criteria.where("email").is(email));
            if (mongoTemplate.exists(query, User.class, collection)) {
                return true;
            }
        }
        return false;
    }

    public String findEmailById(String id) {
        String[] collections = { "facultySupervisors", "companySupervisors" };

        for (String collection : collections) {
            Query query = new Query(Criteria.where("_id").is(id));
            query.fields().include("email"); // Only include the email field
            User user = mongoTemplate.findOne(query, User.class, collection);
            if (user != null) {
                return user.getEmail();
            }
        }

        return null;
    }

    @CacheEvict(value = "userDetails", key = "#email")
    public void evictUserFromCache(String email) {
        System.out.println("Evicting cache for user: " + email);
    }
}
