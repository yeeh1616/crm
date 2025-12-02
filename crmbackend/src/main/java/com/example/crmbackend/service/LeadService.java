package com.example.crmbackend.service;

import com.example.crmbackend.dto.LeadDTO;
import com.example.crmbackend.dto.PageResponse;

/**
 * Service interface for Lead operations
 */
public interface LeadService {
    PageResponse<LeadDTO> getAllLeads(int page, int size, String sortBy, String sortDir, Long customerId);
    LeadDTO getLeadById(Long id);
    LeadDTO createLead(LeadDTO dto);
    LeadDTO updateLead(Long id, LeadDTO dto);
    LeadDTO convertLeadToCustomer(Long leadId);
    void deleteLead(Long id);
}
