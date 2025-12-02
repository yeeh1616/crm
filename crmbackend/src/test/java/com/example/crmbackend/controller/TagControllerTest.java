package com.example.crmbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.crmbackend.dto.TagDTO;
import com.example.crmbackend.service.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    @Test
    void getTagsReturnsList() {
        TagDTO tag1 = TagDTO.builder()
                .id(1L)
                .name("Important")
                .build();

        TagDTO tag2 = TagDTO.builder()
                .id(2L)
                .name("VIP")
                .build();

        List<TagDTO> tags = Arrays.asList(tag1, tag2);

        when(tagService.getTags()).thenReturn(tags);

        ResponseEntity<List<TagDTO>> result = tagController.getAllActivities();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(tags);
        assertThat(result.getBody()).hasSize(2);
        assertThat(result.getBody().get(0).getName()).isEqualTo("Important");
    }
}


