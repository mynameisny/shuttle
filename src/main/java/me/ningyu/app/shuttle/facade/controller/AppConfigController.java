package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前端可消费的应用配置（application.yml 的 app.* 段）。
 *
 * <p>静态页无法读取服务端配置，这里把需要下发的配置项以只读 JSON 暴露。
 * 当前提供：大地图横幅标题、地图默认中心/缩放（初始视野与 geocode 偏好，
 * 换城市部署通过 MAP_CENTER_LAT / MAP_CENTER_LON / MAP_CENTER_ZOOM 覆盖）。
 * 后续新的前端配置项统一收口到本接口。
 */
@Tag(name = "应用配置")
@RestController
public class AppConfigController
{
    private final String mapBannerTitle;
    private final double mapCenterLat;
    private final double mapCenterLon;
    private final int mapZoom;

    public AppConfigController(
            @Value("${app.map-banner.title:昆仑数智班车线路图}") String mapBannerTitle,
            @Value("${app.map.center-lat:39.9042}") double mapCenterLat,
            @Value("${app.map.center-lon:116.4074}") double mapCenterLon,
            @Value("${app.map.zoom:11}") int mapZoom)
    {
        this.mapBannerTitle = mapBannerTitle;
        this.mapCenterLat = mapCenterLat;
        this.mapCenterLon = mapCenterLon;
        this.mapZoom = mapZoom;
    }

    /**
     * 前端配置项集合（键与 app.* 配置路径对应）。
     *
     * @return 如 {"mapBannerTitle": "昆仑数智班车线路图", "mapCenterLat": "39.9042", ...}
     */
    @GetMapping("/api/app-config")
    @Operation(summary = "前端应用配置", description = "下发 application.yml 中需要前端消费的配置项（横幅标题、地图默认中心/缩放）")
    public Map<String, String> appConfig()
    {
        return Map.of(
                "mapBannerTitle", mapBannerTitle,
                "mapCenterLat", String.valueOf(mapCenterLat),
                "mapCenterLon", String.valueOf(mapCenterLon),
                "mapZoom", String.valueOf(mapZoom));
    }
}
