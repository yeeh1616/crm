package com.example.crmbackend.service;

import com.example.crmbackend.dto.TagDTO;

import java.util.List;

/**
 * Service interface for tag operations
 */
public interface TagService {
    List<TagDTO> getTags();
}
