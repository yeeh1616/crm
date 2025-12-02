package com.example.crmbackend.service;

import com.example.crmbackend.dto.CustomerDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.entity.Customer;
import com.example.crmbackend.entity.Segment;
import com.example.crmbackend.entity.Tag;
import com.example.crmbackend.entity.User;
import com.example.crmbackend.mapper.CustomerMapper;
import com.example.crmbackend.mapper.TagMapper;
import com.example.crmbackend.repository.ActivityRepository;
import com.example.crmbackend.repository.CustomerRepository;
import com.example.crmbackend.repository.CustomerTagRepository;
import com.example.crmbackend.repository.SegmentRepository;
import com.example.crmbackend.repository.TagRepository;
import com.example.crmbackend.repository.UserRepository;
import com.example.crmbackend.service.impl.CustomerServiceImpl;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link com.example.crmbackend.service.CustomerService}.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CustomerTagRepository customerTagRepository;

    @Mock
    private SegmentRepository segmentRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .id(1L)
                .username("owner")
                .role(User.Role.USER)
                .build();

        customer = Customer.builder()
                .id(10L)
                .name("Acme")
                .email("test@example.com")
                .owner(owner)
                .build();
    }

    @Test
    void getAllCustomers_ReturnsPagedResponse() {
        Page<Customer> page = new PageImpl<>(List.of(customer), PageRequest.of(0, 10), 1);

        when(customerRepository.findAllActive(any())).thenReturn(page);
        when(activityRepository.findActivityScoresByCustomerIds(anyList()))
                .thenReturn(List.of(Map.of("customerId", 10L, "score", 5)));

        CustomerDTO dto = new CustomerDTO();
        dto.setId(10L);
        when(customerMapper.toDTO(customer)).thenReturn(dto);

        PageResponse<CustomerDTO> response = customerService.getAllCustomers(
                0, 10, "id", "asc", null, null, null);

        assertEquals(1, response.getTotalElements());
        assertEquals(10L, response.getContent().get(0).getId());
        assertEquals(5, response.getContent().get(0).getActivityScore());
    }

    @Test
    void getCustomerById_WhenNotDeleted_ReturnsDto() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        CustomerDTO dto = new CustomerDTO();
        dto.setId(10L);
        when(customerMapper.toDTO(customer)).thenReturn(dto);

        CustomerDTO result = customerService.getCustomerById(10L);

        assertEquals(10L, result.getId());
    }

    @Test
    void getCustomerById_WhenDeleted_ThrowsException() {
        customer.setDeletedAt(LocalDateTime.now());
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.getCustomerById(10L));
        assertTrue(ex.getMessage().contains("deleted"));
    }

    @Test
    void createCustomer_SavesAndReturnsDto() {
        CustomerDTO dto = new CustomerDTO();
        dto.setOwnerId(1L);

        User owner = User.builder().id(1L).username("owner").role(User.Role.USER).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        when(customerMapper.toEntity(dto)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO resultDto = new CustomerDTO();
        resultDto.setId(10L);
        when(customerMapper.toDTO(customer)).thenReturn(resultDto);

        CustomerDTO created = customerService.createCustomer(dto);

        assertEquals(10L, created.getId());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateCustomer_UpdatesFieldsAndTags() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(10L);
        dto.setName("New Name");
        dto.setOwnerId(2L);

        User oldOwner = User.builder().id(1L).username("old").role(User.Role.USER).build();
        customer.setOwner(oldOwner);

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        User newOwner = User.builder().id(2L).username("new").role(User.Role.USER).build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(newOwner));

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO resultDto = new CustomerDTO();
        resultDto.setId(10L);
        when(customerMapper.toDTO(customer)).thenReturn(resultDto);

        CustomerDTO updated = customerService.updateCustomer(dto);

        assertEquals(10L, updated.getId());
        verify(customerTagRepository).deleteByCustomerId(10L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_SoftDeleteWhenNotAdmin() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(10L, false);

        assertNotNull(customer.getDeletedAt());
        verify(customerRepository).save(customer);
    }

    @Test
    void getTotal_DelegatesToRepository() {
        when(customerRepository.countCustomers(null, null, null)).thenReturn(5L);

        Long total = customerService.getTotal(null, null, null);

        assertEquals(5L, total);
    }
}


