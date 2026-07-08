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
import me.ningyu.app.shuttle.domain.entity.Line;
import me.ningyu.app.shuttle.facade.service.LineService;
import me.ningyu.app.shuttle.model.route.CreateLineRequest;
import me.ningyu.app.shuttle.model.route.UpdateLineRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "线路管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/lines")
@Slf4j
@RequiredArgsConstructor
public class LineController
{
    private final LineService lineService;

    @PostMapping
    @Operation(summary = "创建线路", description = "创建新的业务线路")
    public ResponseEntity<Line> create(@Valid @RequestBody CreateLineRequest request)
    {
        Line line = lineService.createLine(request);
        return ResponseEntity.ok(line);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新线路", description = "更新指定线路的信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Line> update(@PathVariable Long id, @Valid @RequestBody UpdateLineRequest request)
    {
        Line line = lineService.updateLine(id, request);
        return ResponseEntity.ok(line);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除线路", description = "删除指定线路（需确保该线路下无线路方向）")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        lineService.deleteLine(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取线路详情", description = "根据ID获取线路的详细信息")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "线路ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Line> getById(@PathVariable Long id)
    {
        Line line = lineService.getLineById(id);
        return ResponseEntity.ok(line);
    }

    @GetMapping
    @Operation(summary = "获取线路列表", description = "获取所有线路的列表")
    public ResponseEntity<List<Line>> getAll()
    {
        List<Line> lines = lineService.getAllLines();
        return ResponseEntity.ok(lines);
    }

    @GetMapping("/by-organization/{organizationId}")
    @Operation(summary = "根据组织ID获取线路列表", description = "获取指定组织下的所有线路")
    @Parameters({
            @Parameter(name = "organizationId", in = ParameterIn.PATH, required = true, description = "组织ID", schema = @Schema(type = "long"))
    })
    public ResponseEntity<List<Line>> getByOrganizationId(@PathVariable Long organizationId)
    {
        List<Line> lines = lineService.getLinesByOrganizationId(organizationId);
        return ResponseEntity.ok(lines);
    }
}
