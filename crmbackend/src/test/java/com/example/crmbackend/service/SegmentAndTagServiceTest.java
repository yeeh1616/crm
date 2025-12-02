package com.example.crmbackend.service;

import com.example.crmbackend.dto.SegmentDTO;
import com.example.crmbackend.dto.TagDTO;
import com.example.crmbackend.entity.Segment;
import com.example.crmbackend.entity.Tag;
import com.example.crmbackend.mapper.SegmentMapper;
import com.example.crmbackend.mapper.TagMapper;
import com.example.crmbackend.repository.SegmentRepository;
import com.example.crmbackend.repository.TagRepository;
import com.example.crmbackend.service.impl.SegmentServiceImpl;
import com.example.crmbackend.service.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Simple unit tests for {@link SegmentService} and {@link TagService}.
 */
@ExtendWith(MockitoExtension.class)
class SegmentAndTagServiceTest {

    @Mock
    private SegmentRepository segmentRepository;

    @Mock
    private SegmentMapper segmentMapper;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private SegmentServiceImpl segmentService;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    void getSegments_ReturnsMappedDtos() {
        Segment segment = new Segment();
        segment.setId(1L);
        segment.setName("VIP");

        when(segmentRepository.findAll()).thenReturn(List.of(segment));

        SegmentDTO dto = new SegmentDTO();
        dto.setId(1L);
        dto.setName("VIP");
        when(segmentMapper.toDTO(segment)).thenReturn(dto);

        List<SegmentDTO> result = segmentService.getSegments();

        assertEquals(1, result.size());
        assertEquals("VIP", result.get(0).getName());
    }

    @Test
    void getTags_ReturnsMappedDtos() {
        Tag tag = new Tag(1L, "Important");
        when(tagRepository.findAll()).thenReturn(List.of(tag));

        TagDTO dto = new TagDTO(1L, "Important");
        when(tagMapper.toDTO(tag)).thenReturn(dto);

        List<TagDTO> result = tagService.getTags();

        assertEquals(1, result.size());
        assertEquals("Important", result.get(0).getName());
    }
}


