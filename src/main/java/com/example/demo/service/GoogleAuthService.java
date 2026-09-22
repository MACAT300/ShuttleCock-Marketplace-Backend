package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
public class GoogleAuthService {

    @Value("${google.client.id}")
    private String googleClientId;

    private final RestTemplate restTemplate = new RestTemplate();

    public static class GoogleUserInfo {
        public String email;
        public String name;
    }

    public GoogleUserInfo verify(String idToken) {
        String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

        Map<String, Object> claims;
        try {
            claims = restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new IllegalArgumentException("Invalid Google token");
        }

        if (claims == null) {
            throw new IllegalArgumentException("Invalid Google token");
        }

        String aud = String.valueOf(claims.get("aud"));
        if (!googleClientId.equals(aud)) {
            throw new IllegalArgumentException("Token was not issued for this app");
        }

        Object emailVerified = claims.get("email_verified");
        if (emailVerified == null || !"true".equals(String.valueOf(emailVerified))) {
            throw new IllegalArgumentException("Google email not verified");
        }

        GoogleUserInfo info = new GoogleUserInfo();
        info.email = String.valueOf(claims.get("email"));
        info.name = claims.containsKey("name") ? String.valueOf(claims.get("name")) : info.email;
        return info;
    }
}