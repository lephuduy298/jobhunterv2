package com.lephuduy.jobhunter.repository;

import com.lephuduy.jobhunter.domain.Permission;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    List<Permission> findByIdIn(List<Long> pIds);

    boolean existsByApiPathAndMethodAndModule(@NotBlank(message = "apiPath không được để trống") String apiPath, @NotBlank(message = "method không được để trống") String method, @NotBlank(message = "module không được để trống") String module);
}
