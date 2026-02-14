package com.gucardev.eventsphere.domain.auth.permission.mapper;

import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.auth.permission.model.dto.PermissionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    PermissionResponseDto toDto(Permission permission);
}
