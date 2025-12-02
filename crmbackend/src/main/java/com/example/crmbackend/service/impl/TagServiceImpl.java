package com.example.crmbackend.service.impl;

import com.example.crmbackend.dto.TagDTO;
import com.example.crmbackend.entity.Tag;
import com.example.crmbackend.mapper.TagMapper;
import com.example.crmbackend.repository.TagRepository;
import com.example.crmbackend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for tag operations
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public List<TagDTO> getTags() {
        List<Tag> tags = tagRepository.findAll();

        return tags.stream().map(tagMapper::toDTO).collect(Collectors.toList());
    }
}

