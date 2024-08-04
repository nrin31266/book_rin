package com.rin.identity.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rin.identity.dto.request.PermissionRequest;
import com.rin.identity.dto.response.PermissionResponse;
import com.rin.identity.entity.Permission;
import com.rin.identity.mapper.PermissionMapper;
import com.rin.identity.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {
    PermissionRepository permissionRepository;

    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        log.info("create permission..");
        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String name) {
        permissionRepository.deleteById(name);
    }
}
