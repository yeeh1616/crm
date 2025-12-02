package com.example.crmbackend.service.impl;

import com.example.crmbackend.dto.LeadDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.entity.Customer;
import com.example.crmbackend.entity.Lead;
import com.example.crmbackend.entity.User;
import com.example.crmbackend.mapper.LeadMapper;
import com.example.crmbackend.repository.CustomerRepository;
import com.example.crmbackend.repository.LeadRepository;
import com.example.crmbackend.repository.UserRepository;
import com.example.crmbackend.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Service implementation for Lead operations
 */
@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final LeadMapper leadMapper;

    @Override
    public PageResponse<LeadDTO> getAllLeads(int page, int size, String sortBy, String sortDir, Long customerId) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Lead> leadPage;
        if (customerId != null) {
            Page<Lead> leadPagetest = leadRepository.findAll(pageable);
            Page<Customer> customerPagetest = customerRepository.findAll(pageable);
            leadPage = leadRepository.findByCustomerId(customerId, pageable);
        } else {
            leadPage = leadRepository.findLeadUserAll(pageable);
        }

        return PageResponse.<LeadDTO>builder()
                .content(leadPage.getContent().stream().map(leadMapper::toDTO).collect(Collectors.toList()))
                .page(leadPage.getNumber())
                .size(leadPage.getSize())
                .totalElements(leadPage.getTotalElements())
                .totalPages(leadPage.getTotalPages())
                .first(leadPage.isFirst())
                .last(leadPage.isLast())
                .build();
    }

    @Override
    public LeadDTO getLeadById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        return leadMapper.toDTO(lead);
    }

    @Transactional
    @Override
    public LeadDTO createLead(LeadDTO dto) {
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Customer customer = null;
        if (dto.getCustomerId() != null) {
            customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }

        Lead lead = Lead.builder()
                .customer(customer)
                .contactName(dto.getContactName())
                .contactEmail(dto.getContactEmail())
                .contactPhone(dto.getContactPhone())
                .stage(dto.getStage() != null ? dto.getStage() : Lead.Stage.NEW)
                .status(dto.getStatus() != null ? dto.getStatus() : Lead.Status.ACTIVE)
                .source(dto.getSource())
                .owner(owner)
                .value(dto.getValue())
                .expectedCloseDate(dto.getExpectedCloseDate())
                .build();

        lead = leadRepository.save(lead);
        return leadMapper.toDTO(lead);
    }

    @Transactional
    @Override
    public LeadDTO updateLead(Long id, LeadDTO dto) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        if (dto.getContactName() != null) lead.setContactName(dto.getContactName());
        if (dto.getContactEmail() != null) lead.setContactEmail(dto.getContactEmail());
        if (dto.getContactPhone() != null) lead.setContactPhone(dto.getContactPhone());
        if (dto.getStage() != null) lead.setStage(dto.getStage());
        if (dto.getStatus() != null) lead.setStatus(dto.getStatus());
        if (dto.getSource() != null) lead.setSource(dto.getSource());
        if (dto.getValue() != null) lead.setValue(dto.getValue());
        if (dto.getExpectedCloseDate() != null) lead.setExpectedCloseDate(dto.getExpectedCloseDate());

        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            lead.setCustomer(customer);
        }

        lead = leadRepository.save(lead);
        return leadMapper.toDTO(lead);
    }

    @Transactional
    @Override
    public LeadDTO convertLeadToCustomer(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        if (lead.isConverted()) {
            throw new RuntimeException("Lead already converted");
        }

        // Create customer from lead
        Customer customer = Customer.builder()
                .name(lead.getContactName())
                .email(lead.getContactEmail())
                .phone(lead.getContactPhone())
                .source(lead.getSource())
                .owner(lead.getOwner())
                .build();

        customer = customerRepository.save(customer);

        // Update lead
        lead.setCustomer(customer);
        lead.setConvertedAt(LocalDateTime.now());
        lead.setStatus(Lead.Status.ARCHIVED);
        lead.setStage(Lead.Stage.WON);
        lead = leadRepository.save(lead);

        return leadMapper.toDTO(lead);
    }

    @Transactional
    @Override
    public void deleteLead(Long id) {
        leadRepository.deleteById(id);
    }
}

