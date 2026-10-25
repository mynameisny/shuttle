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
import me.ningyu.app.shuttle.domain.entity.Agency;
import me.ningyu.app.shuttle.facade.service.AgencyService;
import me.ningyu.app.shuttle.model.agency.CreateAgencyRequest;
import me.ningyu.app.shuttle.model.agency.UpdateAgencyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "运营机构管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/agencies")
@Slf4j
@RequiredArgsConstructor
public class AgencyController
{
    private final AgencyService agencyService;

    @PostMapping
    @Operation(summary = "创建运营机构", description = "创建一个新的运营机构（对应GTFS agency.txt）")
    public ResponseEntity<Agency> create(@Valid @RequestBody CreateAgencyRequest request)
    {
        Agency agency = agencyService.createAgency(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(agency.getId())
                .toUri();
        return ResponseEntity.created(location).body(agency);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除运营机构", description = "删除指定运营机构")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "机构ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        agencyService.deleteAgency(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新运营机构", description = "更新指定运营机构的信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "机构ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Agency> update(@PathVariable Long id, @Valid @RequestBody UpdateAgencyRequest request)
    {
        Agency agency = agencyService.updateAgency(id, request);
        return ResponseEntity.ok(agency);
    }

    @GetMapping
    @Operation(summary = "获取运营机构列表", description = "获取所有运营机构列表，可按 agencyId 或 agencyName 筛选")
    public ResponseEntity<List<Agency>> getAll(@RequestParam(required = false) String agencyId,
                                               @RequestParam(required = false) String agencyName)
    {
        if (agencyId != null && !agencyId.isBlank())
        {
            Agency agency = agencyService.getAgencyByAgencyId(agencyId);
            return ResponseEntity.ok(List.of(agency));
        }
        if (agencyName != null && !agencyName.isBlank())
        {
            List<Agency> agencies = agencyService.getAgenciesByAgencyName(agencyName);
            return ResponseEntity.ok(agencies);
        }
        List<Agency> agencies = agencyService.getAllAgencies();
        return ResponseEntity.ok(agencies);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取运营机构详情", description = "根据ID获取运营机构的详细信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "机构ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Agency> getById(@PathVariable Long id)
    {
        Agency agency = agencyService.getAgencyById(id);
        return ResponseEntity.ok(agency);
    }
}