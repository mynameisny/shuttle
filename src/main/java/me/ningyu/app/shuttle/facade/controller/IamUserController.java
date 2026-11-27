package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.iam.application.UserService;
import me.ningyu.app.shuttle.iam.domain.IamUser;
import me.ningyu.app.shuttle.model.iam.AssignRolesRequest;
import me.ningyu.app.shuttle.model.iam.CreateUserRequest;
import me.ningyu.app.shuttle.model.iam.ResetPasswordRequest;
import me.ningyu.app.shuttle.model.iam.UpdateUserRequest;
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

@Tag(name = "账号管理")
@RestController
@RequestMapping("/api/iam/users")
@RequiredArgsConstructor
public class IamUserController
{
    private final UserService userService;

    @GetMapping
    @Operation(summary = "用户列表")
    public ResponseEntity<List<IamUser>> list()
    {
        return ResponseEntity.ok(userService.list());
    }

    @PostMapping
    @Operation(summary = "新增用户")
    public ResponseEntity<IamUser> create(@Valid @RequestBody CreateUserRequest request)
    {
        return ResponseEntity.ok(userService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public ResponseEntity<IamUser> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request)
    {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "重置密码")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request)
    {
        userService.resetPassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "查询用户角色")
    public ResponseEntity<List<String>> roles(@PathVariable Long id)
    {
        return ResponseEntity.ok(userService.rolesOf(id));
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "分配角色（全量替换）")
    public ResponseEntity<Void> assignRoles(@PathVariable Long id, @RequestBody AssignRolesRequest request)
    {
        userService.assignRoles(id, request);
        return ResponseEntity.noContent().build();
    }
}
