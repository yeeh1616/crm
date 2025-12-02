package com.example.crmbackend.mapper;

import com.example.crmbackend.dto.LeadDTO;
import com.example.crmbackend.entity.Lead;
import org.mapstruct.*;

/**
 * MapStruct mapper for Lead entity and DTO conversion
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeadMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.username")
    LeadDTO toDTO(Lead lead);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "convertedAt", ignore = true)
    Lead toEntity(LeadDTO dto);
}

