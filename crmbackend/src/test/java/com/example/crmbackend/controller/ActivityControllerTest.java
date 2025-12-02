package com.example.crmbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.crmbackend.dto.ActivityDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.entity.Activity;
import com.example.crmbackend.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {

    @Mock
    private ActivityService activityService;

    @InjectMocks
    private ActivityController activityController;

    @Test
    void getAllActivitiesReturnsPageResponse() {
        PageResponse<ActivityDTO> response = PageResponse.<ActivityDTO>builder()
                .content(Collections.emptyList())
                .page(0)
                .size(10)
                .totalElements(0L)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();

        when(activityService.getAllActivities(anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(response);

        ResponseEntity<PageResponse<ActivityDTO>> result = activityController.getAllActivities(
                0, 10, "id", "desc", null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().getPage()).isEqualTo(0);
    }

    @Test
    void getActivityByIdReturnsActivityDTO() {
        ActivityDTO activity = ActivityDTO.builder()
                .id(1L)
                .type(Activity.Type.CALL)
                .content("Test activity")
                .build();

        when(activityService.getActivityById(1L)).thenReturn(activity);

        ResponseEntity<ActivityDTO> result = activityController.getActivityById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(activity);
        assertThat(result.getBody().getId()).isEqualTo(1L);
        assertThat(result.getBody().getType()).isEqualTo(Activity.Type.CALL);
    }

    @Test
    void createActivityReturnsActivityDTO() {
        ActivityDTO input = ActivityDTO.builder()
                .type(Activity.Type.CALL)
                .content("New activity")
                .leadId(1L)
                .build();

        ActivityDTO created = ActivityDTO.builder()
                .id(1L)
                .type(Activity.Type.CALL)
                .content("New activity")
                .leadId(1L)
                .build();

        when(activityService.createActivity(input)).thenReturn(created);

        ResponseEntity<ActivityDTO> result = activityController.createActivity(input);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(created);
        assertThat(result.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void updateActivityReturnsUpdatedDTO() {
        ActivityDTO input = ActivityDTO.builder()
                .id(1L)
                .type(Activity.Type.EMAIL)
                .content("Updated activity")
                .build();

        when(activityService.updateActivity(eq(1L), any(ActivityDTO.class))).thenReturn(input);

        ResponseEntity<ActivityDTO> result = activityController.updateActivity(1L, input);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(input);
        assertThat(result.getBody().getContent()).isEqualTo("Updated activity");
    }

    @Test
    void deleteActivityReturnsNoContent() {
        doNothing().when(activityService).deleteActivity(1L);

        ResponseEntity<Void> result = activityController.deleteActivity(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(activityService).deleteActivity(1L);
    }
}
