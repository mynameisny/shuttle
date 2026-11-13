package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.TripSubstitution;
import me.ningyu.app.shuttle.facade.service.TripAssignmentService;
import me.ningyu.app.shuttle.model.fleet.CreateAssignmentRequest;
import me.ningyu.app.shuttle.model.fleet.SubstituteDriverRequest;
import me.ningyu.app.shuttle.model.fleet.TripAssignmentView;
import me.ningyu.app.shuttle.model.fleet.UpdateAssignmentRequest;
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

@Tag(name = "线路分配")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class TripAssignmentController
{
    private final TripAssignmentService tripAssignmentService;

    @GetMapping
    @Operation(summary = "线路分配列表")
    public ResponseEntity<List<TripAssignmentView>> list()
    {
        return ResponseEntity.ok(tripAssignmentService.list());
    }

    @PostMapping
    @Operation(summary = "新增线路分配")
    public ResponseEntity<TripAssignmentView> create(@Valid @RequestBody CreateAssignmentRequest request)
    {
        return ResponseEntity.ok(tripAssignmentService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新线路分配（车辆/司机/车长）")
    public ResponseEntity<TripAssignmentView> update(
            @Parameter(description = "分配主键") @PathVariable Long id,
            @Valid @RequestBody UpdateAssignmentRequest request)
    {
        return ResponseEntity.ok(tripAssignmentService.update(id, request));
    }

    @GetMapping("/trip/{tripId}/substitutions")
    @Operation(summary = "某班次（去/返）的代班记录")
    public ResponseEntity<List<TripSubstitution>> substitutions(
            @Parameter(description = "班次 trip_id") @PathVariable String tripId)
    {
        return ResponseEntity.ok(tripAssignmentService.listSubstitutions(tripId));
    }

    @PostMapping("/trip/{tripId}/substitutions")
    @Operation(summary = "新增代班（班次级）")
    public ResponseEntity<TripSubstitution> addSubstitution(
            @Parameter(description = "班次 trip_id") @PathVariable String tripId,
            @Valid @RequestBody SubstituteDriverRequest request)
    {
        return ResponseEntity.ok(tripAssignmentService.addSubstitution(tripId, request));
    }

    @DeleteMapping("/substitutions/{substitutionId}")
    @Operation(summary = "删除代班")
    public ResponseEntity<Void> deleteSubstitution(
            @Parameter(description = "代班主键") @PathVariable Long substitutionId)
    {
        tripAssignmentService.deleteSubstitution(substitutionId);
        return ResponseEntity.noContent().build();
    }
}
