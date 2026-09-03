package com.castle.property.controller;

import com.castle.property.PropertyApplicationTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTests extends PropertyApplicationTests {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "/api/v1/users";

    @Before
    public void setUpMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void listUsersWorks() throws Exception {
        mvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void nonAdminCannotListUsers() throws Exception {
        mvc.perform(get(baseUrl))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createUserWorks() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "username", "testuser_" + System.currentTimeMillis(),
                "email", "test_" + System.currentTimeMillis() + "@example.com",
                "password", "password123",
                "firstName", "Test",
                "lastName", "User",
                "roleNames", java.util.List.of("USER")
        ));

        mvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createUserWithDuplicateUsernameReturns400() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "username", "admin",
                "email", "unique_" + System.currentTimeMillis() + "@example.com",
                "password", "password123",
                "firstName", "Test",
                "lastName", "User",
                "roleNames", java.util.List.of("USER")
        ));

        mvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createUserWithDuplicateEmailReturns400() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "username", "unique_" + System.currentTimeMillis(),
                "email", "admin@example.com",
                "password", "password123",
                "firstName", "Test",
                "lastName", "User",
                "roleNames", java.util.List.of("USER")
        ));

        mvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void enableDisableUserWorks() throws Exception {
        // First get the admin user
        MvcResult listResult = mvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = listResult.getResponse().getContentAsString();
        var jsonNode = objectMapper.readTree(responseBody);
        String userId = jsonNode.get("content").get(0).get("id").asText();

        // Disable user
        mvc.perform(patch(baseUrl + "/" + userId + "/disable"))
                .andExpect(status().isOk());

        // Enable user
        mvc.perform(patch(baseUrl + "/" + userId + "/enable"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void resetPasswordWorks() throws Exception {
        // Create a user first
        String createUserBody = objectMapper.writeValueAsString(Map.of(
                "username", "resetpwd_" + System.currentTimeMillis(),
                "email", "resetpwd_" + System.currentTimeMillis() + "@example.com",
                "password", "oldpassword123",
                "firstName", "Reset",
                "lastName", "Password",
                "roleNames", java.util.List.of("USER")
        ));

        MvcResult createResult = mvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserBody))
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseBody = createResult.getResponse().getContentAsString();
        var createJson = objectMapper.readTree(createResponseBody);
        String userId = createJson.get("id").asText();

        // Reset password
        String resetBody = objectMapper.writeValueAsString(Map.of(
                "newPassword", "newpassword123"
        ));

        mvc.perform(post(baseUrl + "/" + userId + "/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetBody))
                .andExpect(status().isOk());

        // Verify the user can login with the new password
        String loginBody = objectMapper.writeValueAsString(Map.of(
                "username", createJson.get("username").asText(),
                "password", "newpassword123"
        ));

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    public void unauthenticatedUserCannotAccessUsers() throws Exception {
        mvc.perform(get(baseUrl))
                .andExpect(status().isUnauthorized());
    }
}
