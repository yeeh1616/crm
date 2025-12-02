package com.example.crmbackend.mapper;

import com.example.crmbackend.dto.SegmentDTO;
import com.example.crmbackend.entity.Segment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Segment entity and DTO conversion
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SegmentMapper {

    @Mapping(target = "id", source = "segment.id")
    @Mapping(target = "name", source = "segment.name")
    SegmentDTO toDTO(Segment segment);

    @Mapping(target = "name", ignore = true)
    Segment toEntity(SegmentDTO dto);
}

