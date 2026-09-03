package com.castle.property.controller;

import com.castle.property.PropertyApplicationTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.RandomStringUtils;
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

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PropertyControllerTests extends PropertyApplicationTests {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private final String baseUrl = "/api/v1/properties";

    @Before
    public void setUpMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void listPropertiesWorks() throws Exception {
        mvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.totalElements", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void listPropertiesWithSearchWorks() throws Exception {
        mvc.perform(get(baseUrl).param("search", "Thika"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void listAllPropertiesWorks() throws Exception {
        mvc.perform(get(baseUrl + "/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getPropertyWorks() throws Exception {
        mvc.perform(get(baseUrl + "/25e1fa0c-1dc9-11f0-9cd2-0242ac120002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getPropertyNotFoundReturns400() throws Exception {
        mvc.perform(get(baseUrl + "/" + UUID.randomUUID()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createPropertyWorks() throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "API Test " + RandomStringUtils.randomAlphabetic(6),
                "location", "Test Location",
                "area", "Test Area"
        ));

        mvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createPropertyInvalidReturns400() throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "",
                "location", "",
                "area", ""
        ));

        mvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createPropertyDuplicateReturns400() throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "name", "Property 1",
                "location", "Githurai",
                "area", "Nairobi"
        ));

        mvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void propertyCreateUpdateLifecycleWorks() throws Exception {
        String uniqueName = "Lifecycle " + RandomStringUtils.randomAlphabetic(6);
        String createBody = objectMapper.writeValueAsString(java.util.Map.of(
                "name", uniqueName,
                "location", "L1",
                "area", "A1"
        ));

        MvcResult createResult = mvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String createdId = created.get("id").asText();

        String updateBody = objectMapper.writeValueAsString(java.util.Map.of(
                "name", uniqueName,
                "location", "L2 Updated",
                "area", "A1"
        ));

        mvc.perform(put(baseUrl + "/" + createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("L2 Updated"));

        mvc.perform(get(baseUrl + "/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(uniqueName));
    }

    @Test
    public void unauthenticatedRequestReturns401() throws Exception {
        mvc.perform(get(baseUrl))
                .andExpect(status().isUnauthorized());
    }
}