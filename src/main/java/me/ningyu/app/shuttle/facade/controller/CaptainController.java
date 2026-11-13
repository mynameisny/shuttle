package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Captain;
import me.ningyu.app.shuttle.facade.service.CaptainService;
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

@Tag(name = "车长管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/captains")
@RequiredArgsConstructor
public class CaptainController
{
    private final CaptainService captainService;

    @GetMapping
    @Operation(summary = "车长列表")
    public ResponseEntity<List<Captain>> getAll()
    {
        return ResponseEntity.ok(captainService.getAll());
    }

    @PostMapping
    @Operation(summary = "新增车长")
    public ResponseEntity<Captain> create(@Valid @RequestBody Captain captain)
    {
        return ResponseEntity.ok(captainService.create(captain));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新车长")
    public ResponseEntity<Captain> update(
            @Parameter(description = "车长主键") @PathVariable Long id,
            @Valid @RequestBody Captain captain)
    {
        return ResponseEntity.ok(captainService.update(id, captain));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除车长")
    public ResponseEntity<Void> delete(@Parameter(description = "车长主键") @PathVariable Long id)
    {
        captainService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
