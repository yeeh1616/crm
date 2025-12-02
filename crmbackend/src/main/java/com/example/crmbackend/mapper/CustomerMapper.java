package com.example.crmbackend.mapper;

import com.example.crmbackend.dto.CustomerDTO;
import com.example.crmbackend.dto.TagDTO;
import com.example.crmbackend.entity.Customer;
import com.example.crmbackend.entity.Tag;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for Customer entity and DTO conversion
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    @Mapping(target = "tags", expression = "java(mapTags(customer.getTags()))")
    @Mapping(target = "segmentId", source = "segment.id")
    @Mapping(target = "segmentName", source = "segment.name")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.username")
    CustomerDTO toDTO(Customer customer);

    @Mapping(target = "tags", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Customer toEntity(CustomerDTO dto);

    default List<TagDTO> mapTags(List<Tag> tags) {
        if (tags == null) return new java.util.ArrayList<>();
        return tags.stream()
                .map(t -> new TagDTO(
                        t.getId(),
                        t.getName()
                ))
                .toList();
    }

    default Tag mapTagDtoToTag(TagDTO dto) {
        if (dto == null) return null;
        return new Tag(dto.getId(), dto.getName());
    }

    default List<Tag> mapTagDtoList(List<TagDTO> dtos) {
        if (dtos == null) return new java.util.ArrayList<>();
        return dtos.stream()
                .map(this::mapTagDtoToTag)
                .toList();
    }
}