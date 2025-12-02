package com.example.crmbackend.service;

import com.example.crmbackend.dto.LeadDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.entity.Customer;
import com.example.crmbackend.entity.Lead;
import com.example.crmbackend.entity.User;
import com.example.crmbackend.mapper.LeadMapper;
import com.example.crmbackend.repository.CustomerRepository;
import com.example.crmbackend.repository.LeadRepository;
import com.example.crmbackend.repository.UserRepository;
import com.example.crmbackend.service.impl.LeadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link LeadService}.
 */
@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LeadMapper leadMapper;

    @InjectMocks
    private LeadServiceImpl leadService;

    private Lead lead;

    @BeforeEach
    void setUp() {
        User owner = User.builder().id(1L).username("owner").role(User.Role.USER).build();

        lead = Lead.builder()
                .id(5L)
                .contactName("John Doe")
                .owner(owner)
                .stage(Lead.Stage.NEW)
                .status(Lead.Status.ACTIVE)
                .build();
    }

    @Test
    void getAllLeads_NoCustomerId_UsesLeadUserAll() {
        Page<Lead> page = new PageImpl<>(List.of(lead), PageRequest.of(0, 10), 1);
        when(leadRepository.findLeadUserAll(any())).thenReturn(page);

        LeadDTO dto = new LeadDTO();
        dto.setId(5L);
        when(leadMapper.toDTO(lead)).thenReturn(dto);

        PageResponse<LeadDTO> response = leadService.getAllLeads(0, 10, "id", "asc", null);

        assertEquals(1, response.getTotalElements());
        assertEquals(5L, response.getContent().get(0).getId());
    }

    @Test
    void getLeadById_ReturnsDto() {
        when(leadRepository.findById(5L)).thenReturn(Optional.of(lead));

        LeadDTO dto = new LeadDTO();
        dto.setId(5L);
        when(leadMapper.toDTO(lead)).thenReturn(dto);

        LeadDTO result = leadService.getLeadById(5L);

        assertEquals(5L, result.getId());
    }

    @Test
    void createLead_PersistsLead() {
        LeadDTO dto = new LeadDTO();
        dto.setOwnerId(1L);

        User owner = User.builder().id(1L).username("owner").role(User.Role.USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        LeadDTO resultDto = new LeadDTO();
        resultDto.setId(5L);
        when(leadMapper.toDTO(lead)).thenReturn(resultDto);

        LeadDTO created = leadService.createLead(dto);

        assertEquals(5L, created.getId());
        verify(leadRepository).save(any(Lead.class));
    }

    @Test
    void updateLead_UpdatesSimpleFields() {
        when(leadRepository.findById(5L)).thenReturn(Optional.of(lead));
        when(leadRepository.save(any(Lead.class))).thenReturn(lead);

        LeadDTO dto = new LeadDTO();
        dto.setContactName("New Name");

        LeadDTO resultDto = new LeadDTO();
        resultDto.setId(5L);
        when(leadMapper.toDTO(lead)).thenReturn(resultDto);

        LeadDTO updated = leadService.updateLead(5L, dto);

        assertEquals(5L, updated.getId());
        verify(leadRepository).save(lead);
    }

    @Test
    void convertLeadToCustomer_WhenNotConverted_CreatesCustomerAndUpdatesLead() {
        // lead in setUp() has convertedAt == null, so it is not converted
        when(leadRepository.findById(5L)).thenReturn(Optional.of(lead));

        Customer savedCustomer = Customer.builder().id(100L).name("John Doe").build();
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        when(leadRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeadDTO dto = new LeadDTO();
        dto.setId(5L);
        when(leadMapper.toDTO(any(Lead.class))).thenReturn(dto);

        LeadDTO converted = leadService.convertLeadToCustomer(5L);

        assertEquals(5L, converted.getId());
        verify(customerRepository).save(any(Customer.class));
        verify(leadRepository).save(any(Lead.class));
    }

    @Test
    void deleteLead_DelegatesToRepository() {
        leadService.deleteLead(5L);

        verify(leadRepository).deleteById(5L);
    }
}


