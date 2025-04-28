
package com.internlink.internlink.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resetTokens")
public class ResetToken {

    @Id
    private String email;
    private String token;
    private Instant expiry;

    public ResetToken() {
    }

    public ResetToken(String email, String token, Instant expiry) {
        this.email = email;
        this.token = token;
        this.expiry = expiry;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
