package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.shuttle.domain.entity.Organization;
import me.ningyu.app.shuttle.facade.service.OrganizationService;
import me.ningyu.app.shuttle.model.organization.CreateOrganizationRequest;
import me.ningyu.app.shuttle.model.organization.UpdateOrganizationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "组织管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/organizations")
@Slf4j
@RequiredArgsConstructor
public class OrganizationController
{
    private final OrganizationService organizationService;

    
    @PostMapping
    @Operation(summary = "创建组织", description = "创建一个新的组织（租户）")
    public ResponseEntity<Organization> create(@Valid @RequestBody CreateOrganizationRequest request)
    {
        Organization organization = organizationService.createOrganization(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(organization.getId())
                .toUri();
        return ResponseEntity.created(location).body(organization);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新组织", description = "更新指定组织的信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "组织ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Organization> update(@PathVariable Long id, @Valid @RequestBody UpdateOrganizationRequest request)
    {
        Organization organization = organizationService.updateOrganization(id, request);
        return ResponseEntity.ok(organization);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除组织", description = "删除指定组织")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "组织ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取组织详情", description = "根据ID获取组织的详细信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "组织ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Organization> getById(@PathVariable Long id)
    {
        Organization organization = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(organization);
    }

    @GetMapping
    @Operation(summary = "获取组织列表", description = "获取所有组织列表，可按组织编码或名称筛选")
    public ResponseEntity<List<Organization>> getAll(@RequestParam(required = false) String code, @RequestParam(required = false) String name)
    {
        if (code != null && !code.isBlank())
        {
            Organization organization = organizationService.getOrganizationByCode(code);
            return ResponseEntity.ok(List.of(organization));
        }
        if (name != null && !name.isBlank())
        {
            List<Organization> organizations = organizationService.getOrganizationsByName(name);
            return ResponseEntity.ok(organizations);
        }
        List<Organization> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }
}
