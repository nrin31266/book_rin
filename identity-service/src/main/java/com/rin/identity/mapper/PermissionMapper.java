package com.rin.identity.mapper;

import org.mapstruct.Mapper;

import com.rin.identity.dto.request.PermissionRequest;
import com.rin.identity.dto.response.PermissionResponse;
import com.rin.identity.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
