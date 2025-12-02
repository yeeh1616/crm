package com.example.crmbackend.service.impl;

import com.example.crmbackend.dto.CustomerDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.entity.Customer;
import com.example.crmbackend.entity.Segment;
import com.example.crmbackend.entity.Tag;
import com.example.crmbackend.entity.User;
import com.example.crmbackend.mapper.CustomerMapper;
import com.example.crmbackend.mapper.TagMapper;
import com.example.crmbackend.repository.*;
import com.example.crmbackend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for Customer operations
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final CustomerTagRepository custoemrTagRepository;
    private final SegmentRepository segmentRepository;
    private final ActivityRepository activityRepository;
    private final CustomerMapper customerMapper;
    private final TagMapper tagMapper;

    @Transactional
    @Override
    public PageResponse<CustomerDTO> getAllCustomers(int page, int size, String sortBy, String sortDir, Long ownerId, Long segmentId, List<Long> tagIds) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Customer> customerPage;
        
        // Determine which query to use based on provided parameters
        if (segmentId != null && tagIds != null && !tagIds.isEmpty()) {
            // Search by both segment and tags
            customerPage = customerRepository.findBySegmentIdAndTagIds(segmentId, tagIds, pageable);
        } else if (segmentId != null) {
            // Search by segment only
            customerPage = customerRepository.findBySegmentId(segmentId, pageable);
        } else if (tagIds != null && !tagIds.isEmpty()) {
            // Search by tags only
            customerPage = customerRepository.findByTagIds(tagIds, pageable);
        } else if (ownerId != null) {
            // Search by owner
            customerPage = customerRepository.findByOwnerId(ownerId, pageable);
        } else {
            // Get all active customers
            customerPage = customerRepository.findAllActive(pageable);
        }
        List<Long> customerIds = customerPage.getContent().stream()
                .map(Customer::getId)
                .collect(Collectors.toList());

        List<Map<String, Object>> scores = activityRepository.findActivityScoresByCustomerIds(customerIds);
        Map<Long, Integer> scoreMap = scores.stream()
                .collect(Collectors.toMap(
                        m -> (Long) m.get("customerId"),
                        m -> ((Number) m.get("score")).intValue()
                ));

        var content = customerPage.getContent().stream().map(customer -> {
            var dto = customerMapper.toDTO(customer);
            dto.setActivityScore(scoreMap.getOrDefault(customer.getId(), 0));
            return dto;
        }).collect(Collectors.toList());

        return PageResponse.<CustomerDTO>builder()
                .content(content)
                .page(customerPage.getNumber())
                .size(customerPage.getSize())
                .totalElements(customerPage.getTotalElements())
                .totalPages(customerPage.getTotalPages())
                .first(customerPage.isFirst())
                .last(customerPage.isLast())
                .build();
    }

    @Override
    public Long getTotal(Long ownerId, Long segmentId, List<Long> tagIds) {
        Long total;

        // Determine which query to use based on provided parameters
        if (segmentId != null && tagIds != null && !tagIds.isEmpty()) {
            // Search by both segment and tags
            total = customerRepository.countCustomers(null, segmentId, tagIds);
        } else if (segmentId != null) {
            // Search by segment only
            total = customerRepository.countCustomers(null, segmentId, null);
        } else if (tagIds != null && !tagIds.isEmpty()) {
            // Search by tags only
            total = customerRepository.countCustomers(null, null, tagIds);
        } else if (ownerId != null) {
            // Search by owner
            total = customerRepository.countCustomers(ownerId, null, null);
        } else {
            // Get all active customers
            total = customerRepository.countCustomers(null, null, null);
        }

        return total;
    }

    @Cacheable(value = "customerList", key = "#id")
    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (customer.isDeleted()) {
            throw new RuntimeException("Customer is deleted");
        }
        return customerMapper.toDTO(customer);
    }

    @Transactional
    @CacheEvict(value = "customerList", allEntries = true)
    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Customer customer = customerMapper.toEntity(dto);

        customer.setOwner(owner);

        if (dto.getSegmentId() != null) {
            Segment segment = segmentRepository.findById(dto.getSegmentId())
                    .orElseThrow(() -> new RuntimeException("Segment not found"));
            customer.setSegment(segment);
        }

        if (dto.getTags() != null && dto.getTags().size() != 0) {
            List<Tag> tags = tagMapper.toEntityList(dto.getTags());
            customer.setTags(tags);
        }

        customer = customerRepository.save(customer);
        return customerMapper.toDTO(customer);
    }

    @Transactional
    @CacheEvict(value = "customerList", allEntries = true)
    @Override
    public CustomerDTO updateCustomer(CustomerDTO dto) {
        Customer customer = customerRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (customer.isDeleted()) {
            throw new RuntimeException("Customer is deleted");
        }

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setCompany(dto.getCompany());
        customer.setTitle(dto.getTitle());
        customer.setSource(dto.getSource());
        customer.setCountry(dto.getCountry());
        customer.setCity(dto.getCity());

        if (dto.getSegmentId() != null) {
            Segment segment = segmentRepository.findById(dto.getSegmentId())
                    .orElseThrow(() -> new RuntimeException("Segment not found"));
            customer.setSegment(segment);
        }

        if (dto.getTags() != null && dto.getTags().size() != 0) {
            List<Tag> tags = tagMapper.toEntityList(dto.getTags());
            customer.setTags(tags);
        }

        if (dto.getOwnerId() != null && !dto.getOwnerId().equals(customer.getOwner().getId())) {
            User owner = userRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("Owner not found"));
            customer.setOwner(owner);
        }
        custoemrTagRepository.deleteByCustomerId(customer.getId());
        customer = customerRepository.save(customer);
        return customerMapper.toDTO(customer);
    }

    @Transactional
    @CacheEvict(value = "customerList", allEntries = true)
    @Override
    public void deleteCustomer(Long id, boolean permanent) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (permanent && isAdmin) {
            customerRepository.delete(customer);
        } else {
            customer.setDeletedAt(LocalDateTime.now());
            customerRepository.save(customer);
        }
    }
}

