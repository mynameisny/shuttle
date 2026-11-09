package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.facade.service.VehicleService;
import me.ningyu.app.shuttle.model.fleet.VehicleUpsertRequest;
import me.ningyu.app.shuttle.model.fleet.VehicleView;
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

@Tag(name = "载具管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController
{
    private final VehicleService vehicleService;

    @GetMapping
    @Operation(summary = "载具列表", description = "返回全部载具，司机信息展平为 driverName/driverPhone")
    public ResponseEntity<List<VehicleView>> getAll()
    {
        return ResponseEntity.ok(vehicleService.getAll());
    }

    @PostMapping
    @Operation(summary = "新增载具")
    public ResponseEntity<VehicleView> create(@Valid @RequestBody VehicleUpsertRequest request)
    {
        return ResponseEntity.ok(vehicleService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新载具")
    public ResponseEntity<VehicleView> update(
            @Parameter(description = "载具主键") @PathVariable Long id,
            @Valid @RequestBody VehicleUpsertRequest request)
    {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除载具")
    public ResponseEntity<Void> delete(@Parameter(description = "载具主键") @PathVariable Long id)
    {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
