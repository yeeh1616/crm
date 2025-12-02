package com.example.crmbackend.service;

import com.example.crmbackend.dto.ActivityDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.entity.Activity;
import com.example.crmbackend.entity.Lead;
import com.example.crmbackend.mapper.ActivityMapper;
import com.example.crmbackend.repository.ActivityRepository;
import com.example.crmbackend.repository.LeadRepository;
import com.example.crmbackend.service.impl.ActivityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ActivityServiceImpl activityService;

    private Activity activity;
    private ActivityDTO activityDTO;
    private Lead lead;

    @BeforeEach
    void setUp() {
        lead = Lead.builder()
                .id(1L)
                .contactName("Test Lead")
                .build();

        activity = Activity.builder()
                .id(1L)
                .lead(lead)
                .type(Activity.Type.CALL)
                .content("Test call")
                .nextFollowUpAt(LocalDateTime.now().plusDays(1))
                .subscribedReminder(true)
                .sentReminder(false)
                .build();

        activityDTO = ActivityDTO.builder()
                .id(1L)
                .leadId(1L)
                .type(Activity.Type.CALL)
                .content("Test call")
                .nextFollowUpAt(LocalDateTime.now().plusDays(1))
                .subscribedReminder(true)
                .sentReminder(false)
                .build();
    }

    @Test
    void getAllActivities_ReturnsPageResponse() {
        Pageable pageable = PageRequest.of(0, 10, org.springframework.data.domain.Sort.by("id").descending());
        Page<Activity> activityPage = new PageImpl<>(Collections.singletonList(activity), pageable, 1);

        when(activityRepository.findAll(any(Pageable.class))).thenReturn(activityPage);
        when(activityMapper.toDTO(activity)).thenReturn(activityDTO);

        PageResponse<ActivityDTO> result = activityService.getAllActivities(0, 10, "id", "desc", null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        verify(activityRepository).findAll(any(Pageable.class));
    }

    @Test
    void getAllActivities_WithLeadId_FiltersByLead() {
        Pageable pageable = PageRequest.of(0, 10, org.springframework.data.domain.Sort.by("id").descending());
        Page<Activity> activityPage = new PageImpl<>(Collections.singletonList(activity), pageable, 1);

        when(activityRepository.findByLeadId(eq(1L), any(Pageable.class))).thenReturn(activityPage);
        when(activityMapper.toDTO(activity)).thenReturn(activityDTO);

        PageResponse<ActivityDTO> result = activityService.getAllActivities(0, 10, "id", "desc", 1L);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(activityRepository).findByLeadId(eq(1L), any(Pageable.class));
    }

    @Test
    void getActivityById_ReturnsActivityDTO() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(activityMapper.toDTO(activity)).thenReturn(activityDTO);

        ActivityDTO result = activityService.getActivityById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(activityRepository).findById(1L);
    }

    @Test
    void getActivityById_ThrowsException_WhenNotFound() {
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> activityService.getActivityById(1L));
    }

    @Test
    void createActivity_WithLead_SavesAndReturnsDTO() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        when(activityMapper.toDTO(activity)).thenReturn(activityDTO);

        ActivityDTO result = activityService.createActivity(activityDTO);

        assertNotNull(result);
        verify(activityRepository).save(any(Activity.class));
    }

    @Test
    void createActivity_WithoutLead_SavesAndReturnsDTO() {
        activityDTO.setLeadId(null);
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        when(activityMapper.toDTO(activity)).thenReturn(activityDTO);

        ActivityDTO result = activityService.createActivity(activityDTO);

        assertNotNull(result);
        verify(leadRepository, never()).findById(anyLong());
        verify(activityRepository).save(any(Activity.class));
    }

    @Test
    void createActivity_WithReminder_SendsRabbitMQMessage() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        when(activityMapper.toDTO(activity)).thenReturn(activityDTO);

        activityService.createActivity(activityDTO);

        verify(rabbitTemplate).convertAndSend(eq("activity.delay.queue"), eq(1L), any(org.springframework.amqp.core.MessagePostProcessor.class));
    }

    @Test
    void createActivity_ThrowsException_WhenLeadNotFound() {
        when(leadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> activityService.createActivity(activityDTO));
    }

    @Test
    void updateActivity_UpdatesAndReturnsDTO() {
        ActivityDTO updatedDTO = ActivityDTO.builder()
                .id(1L)
                .type(Activity.Type.EMAIL)
                .content("Updated content")
                .build();

        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));
        when(activityRepository.save(activity)).thenReturn(activity);
        when(activityMapper.toDTO(activity)).thenReturn(updatedDTO);

        ActivityDTO result = activityService.updateActivity(1L, updatedDTO);

        assertNotNull(result);
        verify(activityRepository).save(activity);
    }

    @Test
    void updateActivity_ThrowsException_WhenNotFound() {
        when(activityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> activityService.updateActivity(1L, activityDTO));
    }

    @Test
    void deleteActivity_DeletesSuccessfully() {
        doNothing().when(activityRepository).deleteById(1L);

        activityService.deleteActivity(1L);

        verify(activityRepository).deleteById(1L);
    }
}

