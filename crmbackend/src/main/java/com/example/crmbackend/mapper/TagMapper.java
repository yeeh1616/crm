package com.example.crmbackend.mapper;

import com.example.crmbackend.dto.SegmentDTO;
import com.example.crmbackend.dto.TagDTO;
import com.example.crmbackend.entity.Segment;
import com.example.crmbackend.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for Tag entity and DTO conversion
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "id", source = "tag.id")
    @Mapping(target = "name", source = "tag.name")
    TagDTO toDTO(Tag tag);

    @Mapping(target = "name", ignore = true)
    Tag toEntity(TagDTO dto);

    default List<Tag> toEntityList(List<TagDTO> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    default List<TagDTO> toDTOList(List<Tag> tags) {
        if (tags == null) return new ArrayList<>();
        return tags.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

