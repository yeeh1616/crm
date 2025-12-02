package com.example.crmbackend.service;

import com.example.crmbackend.dto.ActivityDTO;
import com.example.crmbackend.dto.PageResponse;

/**
 * Service interface for Activity operations
 */
public interface ActivityService {
    PageResponse<ActivityDTO> getAllActivities(int page, int size, String sortBy, String sortDir, Long leadId);
    ActivityDTO getActivityById(Long id);
    ActivityDTO createActivity(ActivityDTO dto);
    ActivityDTO updateActivity(Long id, ActivityDTO dto);
    void deleteActivity(Long id);
}
