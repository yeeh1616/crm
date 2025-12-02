package com.example.crmbackend.mapper;

import com.example.crmbackend.dto.ActivityDTO;
import com.example.crmbackend.entity.Activity;
import org.mapstruct.*;

/**
 * MapStruct mapper for Activity entity and DTO conversion
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ActivityMapper {

    @Mapping(target = "leadId", source = "lead.id")
    ActivityDTO toDTO(Activity activity);

    @Mapping(target = "lead", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Activity toEntity(ActivityDTO dto);
}

