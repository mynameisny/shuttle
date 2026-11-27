package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.iam.application.RoleService;
import me.ningyu.app.shuttle.iam.domain.IamRole;
import me.ningyu.app.shuttle.model.iam.CreateRoleRequest;
import me.ningyu.app.shuttle.model.iam.GrantPermissionRequest;
import me.ningyu.app.shuttle.model.iam.UpdateRoleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/iam/roles")
@RequiredArgsConstructor
public class IamRoleController
{
    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "角色列表")
    public ResponseEntity<List<IamRole>> list()
    {
        return ResponseEntity.ok(roleService.list());
    }

    @PostMapping
    @Operation(summary = "新增角色")
    public ResponseEntity<IamRole> create(@Valid @RequestBody CreateRoleRequest request)
    {
        return ResponseEntity.ok(roleService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public ResponseEntity<IamRole> update(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request)
    {
        return ResponseEntity.ok(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{code}/permissions")
    @Operation(summary = "角色权限列表")
    public ResponseEntity<List<List<String>>> permissions(@PathVariable String code)
    {
        return ResponseEntity.ok(roleService.permissionsOf(code));
    }

    @PostMapping("/{code}/permissions")
    @Operation(summary = "授予权限")
    public ResponseEntity<Void> grant(@PathVariable String code, @Valid @RequestBody GrantPermissionRequest request)
    {
        roleService.grant(code, request.resource(), request.action());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{code}/permissions")
    @Operation(summary = "撤销权限")
    public ResponseEntity<Void> revoke(@PathVariable String code, @Valid @RequestBody GrantPermissionRequest request)
    {
        roleService.revoke(code, request.resource(), request.action());
        return ResponseEntity.noContent().build();
    }
}
