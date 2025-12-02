package com.example.crmbackend.service.impl;

import com.example.crmbackend.config.RabbitMQConfig;
import com.example.crmbackend.dto.ActivityDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.entity.Activity;
import com.example.crmbackend.entity.Lead;
import com.example.crmbackend.mapper.ActivityMapper;
import com.example.crmbackend.repository.ActivityRepository;
import com.example.crmbackend.repository.LeadRepository;
import com.example.crmbackend.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Service implementation for Activity operations
 */
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final LeadRepository leadRepository;
    private final ActivityMapper activityMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public PageResponse<ActivityDTO> getAllActivities(int page, int size, String sortBy, String sortDir, Long leadId) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Activity> activityPage;
        if (leadId != null) {
            activityPage = activityRepository.findByLeadId(leadId, pageable);
        } else {
            activityPage = activityRepository.findAll(pageable);
        }

        return PageResponse.<ActivityDTO>builder()
                .content(activityPage.getContent().stream().map(activityMapper::toDTO).collect(Collectors.toList()))
                .page(activityPage.getNumber())
                .size(activityPage.getSize())
                .totalElements(activityPage.getTotalElements())
                .totalPages(activityPage.getTotalPages())
                .first(activityPage.isFirst())
                .last(activityPage.isLast())
                .build();
    }

    @Override
    public ActivityDTO getActivityById(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        return activityMapper.toDTO(activity);
    }

    @Transactional
    @Override
    public ActivityDTO createActivity(ActivityDTO dto) {
        Lead lead = null;
        if (dto.getLeadId() != null) {
            lead = leadRepository.findById(dto.getLeadId())
                    .orElseThrow(() -> new RuntimeException("Lead not found"));
        }

        Activity activity = Activity.builder()
                .lead(lead)
                .type(dto.getType())
                .content(dto.getContent())
                .outcome(dto.getOutcome())
                .nextFollowUpAt(dto.getNextFollowUpAt())
                .sentReminder(dto.isSentReminder())
                .subscribedReminder(dto.isSubscribedReminder())
                .build();

        activity = activityRepository.save(activity);

        // send remainder through rabbitMQ
        if (activity.isSubscribedReminder() && !activity.isSentReminder() && activity.getNextFollowUpAt() != null) {
            long calculatedDelay = Duration.between(LocalDateTime.now(), activity.getNextFollowUpAt()).toMillis();
            final long delay = calculatedDelay < 0 ? 0 : calculatedDelay;
            rabbitTemplate.convertAndSend(RabbitMQConfig.DELAY_QUEUE, activity.getId(), message -> {
                message.getMessageProperties().setExpiration(String.valueOf(delay));
                return message;
            });
        }

        return activityMapper.toDTO(activity);
    }

    @Transactional
    @Override
    public ActivityDTO updateActivity(Long id, ActivityDTO dto) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        activity.setType(dto.getType());
        activity.setContent(dto.getContent());
        activity.setOutcome(dto.getOutcome());
        activity.setNextFollowUpAt(dto.getNextFollowUpAt());

        activity = activityRepository.save(activity);
        return activityMapper.toDTO(activity);
    }

    @Transactional
    @Override
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }
}

