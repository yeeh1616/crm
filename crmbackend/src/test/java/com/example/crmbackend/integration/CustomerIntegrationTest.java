package com.example.crmbackend.integration;

import com.example.crmbackend.dto.CustomerDTO;
import com.example.crmbackend.entity.Customer;
import com.example.crmbackend.entity.Segment;
import com.example.crmbackend.entity.Tag;
import com.example.crmbackend.entity.User;
import com.example.crmbackend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SegmentRepository segmentRepository;

    @Autowired
    private TagRepository tagRepository;

    private User testUser;
    private Segment testSegment;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .passwordHash("$2a$10$dummy")
                .role(User.Role.USER)
                .build();
        testUser = userRepository.save(testUser);

        testSegment = Segment.builder()
                .name("Test Segment")
                .build();
        testSegment = segmentRepository.save(testSegment);

        testTag = Tag.builder()
                .name("Test Tag")
                .build();
        testTag = tagRepository.save(testTag);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createAndGetCustomer_IntegrationTest() throws Exception {
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("Integration Test Customer")
                .email("integration@example.com")
                .phone("1234567890")
                .ownerId(testUser.getId())
                .segmentId(testSegment.getId())
                .tags(Collections.singletonList(
                        com.example.crmbackend.dto.TagDTO.builder()
                                .id(testTag.getId())
                                .name(testTag.getName())
                                .build()))
                .build();

        String response = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Customer"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        CustomerDTO created = objectMapper.readValue(response, CustomerDTO.class);

        mockMvc.perform(get("/api/customers/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value("Integration Test Customer"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getAllCustomers_WithFilters_IntegrationTest() throws Exception {
        Customer customer = Customer.builder()
                .name("Filter Test Customer")
                .email("filter@example.com")
                .owner(testUser)
                .segment(testSegment)
                .build();
        customerRepository.save(customer);

        mockMvc.perform(get("/api/customers")
                        .param("ownerId", testUser.getId().toString())
                        .param("segment", testSegment.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}

