package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Driver;
import me.ningyu.app.shuttle.facade.service.DriverService;
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

@Tag(name = "司机管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController
{
    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "司机列表")
    public ResponseEntity<List<Driver>> getAll()
    {
        return ResponseEntity.ok(driverService.getAll());
    }

    @PostMapping
    @Operation(summary = "新增司机")
    public ResponseEntity<Driver> create(@Valid @RequestBody Driver driver)
    {
        return ResponseEntity.ok(driverService.create(driver));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新司机")
    public ResponseEntity<Driver> update(
            @Parameter(description = "司机主键") @PathVariable Long id,
            @Valid @RequestBody Driver driver)
    {
        return ResponseEntity.ok(driverService.update(id, driver));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除司机", description = "司机仍关联车辆时返回 400 拒绝")
    public ResponseEntity<Void> delete(@Parameter(description = "司机主键") @PathVariable Long id)
    {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
