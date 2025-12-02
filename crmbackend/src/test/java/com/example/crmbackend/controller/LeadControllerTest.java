package com.example.crmbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.crmbackend.dto.LeadDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.service.LeadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class LeadControllerTest {

    @Mock
    private LeadService leadService;

    @InjectMocks
    private LeadController leadController;

    @Test
    void getAllLeadsReturnsPageResponse() {
        PageResponse<LeadDTO> response = PageResponse.<LeadDTO>builder()
                .content(Collections.emptyList())
                .page(0)
                .size(10)
                .totalElements(0L)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();

        when(leadService.getAllLeads(anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(response);

        ResponseEntity<PageResponse<LeadDTO>> result = leadController.getAllLeads(
                0, 10, "id", "asc", null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().getPage()).isEqualTo(0);
    }

    @Test
    void getLeadByIdReturnsLeadDTO() {
        LeadDTO lead = LeadDTO.builder()
                .id(1L)
                .contactName("Test Lead")
                .contactEmail("lead@example.com")
                .build();

        when(leadService.getLeadById(1L)).thenReturn(lead);

        ResponseEntity<LeadDTO> result = leadController.getLeadById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(lead);
        assertThat(result.getBody().getId()).isEqualTo(1L);
        assertThat(result.getBody().getContactName()).isEqualTo("Test Lead");
    }

    @Test
    void createLeadReturnsLeadDTO() {
        LeadDTO input = LeadDTO.builder()
                .contactName("New Lead")
                .contactEmail("lead@example.com")
                .ownerId(1L)
                .build();

        LeadDTO created = LeadDTO.builder()
                .id(1L)
                .contactName("New Lead")
                .contactEmail("lead@example.com")
                .ownerId(1L)
                .build();

        when(leadService.createLead(input)).thenReturn(created);

        ResponseEntity<LeadDTO> result = leadController.createLead(input);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(created);
        assertThat(result.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void updateLeadReturnsUpdatedDTO() {
        LeadDTO input = LeadDTO.builder()
                .id(1L)
                .contactName("Updated Lead")
                .contactEmail("updated@example.com")
                .build();

        when(leadService.updateLead(eq(1L), any(LeadDTO.class))).thenReturn(input);

        ResponseEntity<LeadDTO> result = leadController.updateLead(1L, input);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(input);
        assertThat(result.getBody().getContactName()).isEqualTo("Updated Lead");
    }

    @Test
    void convertLeadToCustomerReturnsConvertedDTO() {
        LeadDTO converted = LeadDTO.builder()
                .id(1L)
                .contactName("Converted Lead")
                .build();

        when(leadService.convertLeadToCustomer(1L)).thenReturn(converted);

        ResponseEntity<LeadDTO> result = leadController.convertLeadToCustomer(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(converted);
        assertThat(result.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void deleteLeadReturnsNoContent() {
        doNothing().when(leadService).deleteLead(1L);

        ResponseEntity<Void> result = leadController.deleteLead(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(leadService).deleteLead(1L);
    }
}
