package com.example.crmbackend.service;

import com.example.crmbackend.dto.CustomerDTO;
import com.example.crmbackend.dto.PageResponse;

import java.util.List;

/**
 * Service interface for Customer operations
 */
public interface CustomerService {
    PageResponse<CustomerDTO> getAllCustomers(int page, int size, String sortBy, String sortDir, Long ownerId, Long segmentId, List<Long> tagIds);
    Long getTotal(Long ownerId, Long segmentId, List<Long> tagIds);
    CustomerDTO getCustomerById(Long id);
    CustomerDTO createCustomer(CustomerDTO dto);
    CustomerDTO updateCustomer(CustomerDTO dto);
    void deleteCustomer(Long id, boolean permanent);
}
