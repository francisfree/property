package com.castle.property.controller;

import com.castle.property.PropertyApplicationTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTests extends PropertyApplicationTests {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUpMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void loginWithValidCredentials() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "username", "admin",
                "password", "admin123"
        ));

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user", notNullValue()))
                .andExpect(jsonPath("$.user.username").value("admin"));
    }

    @Test
    public void loginWithInvalidPassword() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "username", "admin",
                "password", "wrongpassword"
        ));

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginWithInvalidUsername() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "username", "nonexistent",
                "password", "password"
        ));

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginWithEmptyFields() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "username", "",
                "password", ""
        ));

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getMeWithoutTokenReturns401() throws Exception {
        mvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getMeWithValidToken() throws Exception {
        String token = getAuthToken("admin", "admin123");

        mvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@example.com"));
    }

    @Test
    public void changePasswordWorks() throws Exception {
        String token = getAuthToken("admin", "admin123");

        String body = objectMapper.writeValueAsString(Map.of(
                "currentPassword", "admin123",
                "newPassword", "newpassword123"
        ));

        mvc.perform(post("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        // Verify new password works
        String newToken = getAuthToken("admin", "newpassword123");
        if (newToken == null) {
            throw new RuntimeException("New password should work");
        }

        // Change back to original
        body = objectMapper.writeValueAsString(Map.of(
                "currentPassword", "newpassword123",
                "newPassword", "admin123"
        ));

        mvc.perform(post("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer " + newToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    public void changePasswordWithWrongCurrentPassword() throws Exception {
        String token = getAuthToken("admin", "admin123");

        String body = objectMapper.writeValueAsString(Map.of(
                "currentPassword", "wrongpassword",
                "newPassword", "newpassword123"
        ));

        mvc.perform(post("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logoutWorks() throws Exception {
        String token = getAuthToken("admin", "admin123");

        mvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private String getAuthToken(String username, String password) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "username", username,
                    "password", password
            ));

            var result = mvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = result.getResponse().getContentAsString();
            var jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("token").asText();
        } catch (Exception e) {
            return null;
        }
    }
}
