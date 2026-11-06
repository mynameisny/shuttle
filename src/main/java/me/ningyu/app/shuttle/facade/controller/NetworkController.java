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
import me.ningyu.app.shuttle.domain.entity.gtfs.Network;
import me.ningyu.app.shuttle.facade.service.NetworkService;
import me.ningyu.app.shuttle.model.network.CreateNetworkRequest;
import me.ningyu.app.shuttle.model.network.RouteNetworkTagsView;
import me.ningyu.app.shuttle.model.network.UpdateNetworkRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * 线路网络管理（GTFS networks.txt）。一条线路可挂多个网络标签（route_networks.txt）。
 */
@Tag(name = "线路网络管理")
@SecurityRequirement(name = "TONY")
@RestController
@RequestMapping("/api/networks")
@Slf4j
@RequiredArgsConstructor
public class NetworkController
{
    private final NetworkService networkService;

    @PostMapping
    @Operation(summary = "创建网络", description = "创建线路网络分组（networks.txt）；routes.network_id 与 route_networks 互斥")
    public ResponseEntity<Network> create(@Valid @RequestBody CreateNetworkRequest request)
    {
        Network network = networkService.createNetwork(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(network.getId())
                .toUri();
        return ResponseEntity.created(location).body(network);
    }

    @GetMapping
    @Operation(summary = "网络列表", description = "获取全部网络分组")
    public ResponseEntity<List<Network>> getAll()
    {
        return ResponseEntity.ok(networkService.getAllNetworks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "网络详情", description = "按主键获取网络")
    @Parameters({
            @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "网络主键", schema = @Schema(type = "long"))
    })
    public ResponseEntity<Network> getById(@PathVariable Long id)
    {
        return ResponseEntity.ok(networkService.getNetworkById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新网络", description = "更新网络名称")
    public ResponseEntity<Network> update(@PathVariable Long id, @Valid @RequestBody UpdateNetworkRequest request)
    {
        return ResponseEntity.ok(networkService.updateNetwork(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除网络", description = "删除网络（网络下仍有线路时拒绝）")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        networkService.deleteNetwork(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/routes/{routePk}")
    @Operation(summary = "线路的标签", description = "获取某线路的全部网络标签")
    public ResponseEntity<RouteNetworkTagsView> getRouteTags(@PathVariable Long routePk)
    {
        return ResponseEntity.ok(networkService.getRouteNetworks(routePk));
    }

    @PutMapping("/routes/{routePk}")
    @Operation(summary = "全量设置线路标签", description = "覆盖式设置线路的网络标签（传空数组即清空）")
    public ResponseEntity<RouteNetworkTagsView> setRouteTags(
            @PathVariable Long routePk,
            @RequestBody List<String> networkIds)
    {
        return ResponseEntity.ok(networkService.setRouteNetworks(routePk, networkIds));
    }

    @PostMapping("/routes/{routePk}/{networkId}")
    @Operation(summary = "追加线路标签", description = "给线路追加一个网络标签（幂等）")
    public ResponseEntity<Void> addRouteTag(@PathVariable Long routePk, @PathVariable String networkId)
    {
        networkService.addRouteNetwork(routePk, networkId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/routes/{routePk}/{networkId}")
    @Operation(summary = "移除线路标签", description = "移除线路上的某个网络标签")
    public ResponseEntity<Void> removeRouteTag(@PathVariable Long routePk, @PathVariable String networkId)
    {
        networkService.removeRouteNetwork(routePk, networkId);
        return ResponseEntity.noContent().build();
    }
}
