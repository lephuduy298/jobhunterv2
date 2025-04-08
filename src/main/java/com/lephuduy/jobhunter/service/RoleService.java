package com.lephuduy.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lephuduy.jobhunter.domain.Permission;
import com.lephuduy.jobhunter.domain.Role;
import com.lephuduy.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.lephuduy.jobhunter.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    private final PermissionService permissionService;

    RoleService(RoleRepository roleRepository, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
    }

    public boolean existRoleName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role handleCreateRole(Role role) {
        if (role.getPermissions() != null) {
            List<Long> pIds = role.getPermissions()
                    .stream().map(p -> p.getId())
                    .collect(Collectors.toList());

            List<Permission> listPermissions = this.permissionService.getIdIn(pIds);

            role.setPermissions(listPermissions);
        }

        return this.roleRepository.save(role);

    }

    public Optional<Role> findRoleById(long id) {
        return this.roleRepository.findById(id);
    }

    public Role handleUpdateRole(Role role, Role roleInDB) {
        if (role.getPermissions() != null) {
            List<Long> pIds = role.getPermissions()
                    .stream().map(p -> p.getId())
                    .collect(Collectors.toList());

            List<Permission> listPermissions = this.permissionService.getIdIn(pIds);

            roleInDB.setPermissions(listPermissions);
        }
        roleInDB.setName(role.getName());
        roleInDB.setDescription(role.getDescription());
        roleInDB.setCreatedBy(role.getDescription());
        roleInDB.setActive(role.isActive());

        return this.roleRepository.save(roleInDB);
    }

    public ResultPaginationDTO getAllPermission(Specification<Role> spec, Pageable pageable) {
        Page<Role> rolePages = this.roleRepository.findAll(spec, pageable);
        List<Role> listRoles = rolePages.getContent();

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(rolePages.getNumber() + 1);
        meta.setPageSize(rolePages.getSize());
        meta.setPages(rolePages.getTotalPages());
        meta.setTotal(rolePages.getTotalElements());

        result.setMeta(meta);
        result.setResult(listRoles);

        return result;
    }

    public void deleteARole(long id) {
        this.roleRepository.deleteById(id);
    }

}

