package com.rin.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.rin.identity.dto.request.RoleRequest;
import com.rin.identity.dto.response.RoleResponse;
import com.rin.identity.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}