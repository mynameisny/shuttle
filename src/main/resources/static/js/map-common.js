// Shuttle 管理端地图页公共库：map.html（线路描图）/ stops.html（站点维护）共用。
// 依赖：Leaflet 1.9.4 须先于本文件加载；页面须包含 <div id="toast"></div> 元素与对应 .show/.err 样式。

const $ = id => document.getElementById(id);

// XSS 转义：站名等用户输入拼 HTML 前必须经过 esc()（toast 用 textContent 无需转义）
function esc(s) {
    return String(s == null ? '' : s)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

// REST 封装：非 2xx 抛错（优先取 body.message）；204/空响应体返回 null（如 DELETE 形状）
async function api(url, options) {
    const resp = await fetch(url, options);
    if (!resp.ok) {
        let detail = 'HTTP ' + resp.status;
        try { const body = await resp.json(); if (body && body.message) detail = body.message; } catch (e) { /* ignore */ }
        throw new Error(detail);
    }
    if (resp.status === 204) return null;
    const text = await resp.text();
    return text ? JSON.parse(text) : null;
}

// 顶部浮动提示
function toast(msg, isErr) {
    const t = $('toast');
    t.textContent = msg;
    t.className = 'show' + (isErr ? ' err' : '');
    clearTimeout(t._timer);
    t._timer = setTimeout(() => t.className = '', 2600);
}

// 创建地图。WGS-84 数据 + 无偏移底图（勿换高德/百度 GCJ-02 底图）。
// 2026-09-23 起 CARTO 底图要求免费 API key：无 key 请求返回 "API KEY REQUIRED" 水印瓦片
// （HTTP 200 正常图，不触发 tileerror，前端无法识别过滤）。如需恢复简洁/细节底图：
// 到 https://carto.com/basemaps/apikey 申请免费 key（邮箱直达，非商用 5M 请求/月），
// 将 YOUR_CARTO_KEY 替换为实际 key 后取消下方注释，并把图层加回 basemaps。
// const cartoKey = 'YOUR_CARTO_KEY';
// const cartoLayers = {
//     '简洁底图': L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png?key=' + cartoKey, {
//         maxZoom: 20, subdomains: 'abcd', attribution: '&copy; OpenStreetMap contributors &copy; CARTO'
//     }),
//     '细节底图': L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png?key=' + cartoKey, {
//         maxZoom: 20, subdomains: 'abcd', attribution: '&copy; OpenStreetMap contributors &copy; CARTO'
//     })
// };
// 默认 OSM 标准底图：无 key 要求，内部低流量使用符合 OSM 瓦片政策（代价：natural=peak 山峰 ▲ 图标仍在）
// 默认视野：北京。站点列表为空或全部为 (0,0) 占位（Excel 导入未定位站）时兜底。
// 注意：center/zoom 可被服务端配置覆盖（见下方地图配置应用逻辑，换城市部署时随 app.map.* 走）。
const MAP_DEFAULT_VIEW = { center: [39.9042, 116.4074], zoom: 11 };

// 用服务端下发的地图配置覆盖默认视野（/api/app-config 的 mapCenterLat/mapCenterLon/mapZoom）。
// 覆盖后：createMap 初始视野与 geocode 的地名搜索偏好（bias）一并跟随。
function applyMapConfig(centerLat, centerLon, zoom) {
    const lat = parseFloat(centerLat), lon = parseFloat(centerLon), z = parseInt(zoom, 10);
    if (isFinite(lat) && isFinite(lon)) MAP_DEFAULT_VIEW.center = [lat, lon];
    if (isFinite(z) && z > 0) MAP_DEFAULT_VIEW.zoom = z;
}

// 地图配置应用：localStorage 同步恢复（createMap 之前生效，保证初始视野正确）+
// 异步拉取 /api/app-config 刷新（当前页的 geocode 偏好用最新值，并写缓存供下次访问同步生效）。
// 放在 map-common.js 内自动执行，各地图页无需任何改动。
(function () {
    try {
        const saved = localStorage.getItem('shuttle.map-config');
        if (saved) {
            const c = JSON.parse(saved);
            applyMapConfig(c.mapCenterLat, c.mapCenterLon, c.mapZoom);
        }
    } catch (e) { /* 缓存损坏时用内置默认值 */ }
    fetch('/api/app-config').then(r => r.ok ? r.json() : null).then(cfg => {
        if (cfg && cfg.mapCenterLat != null && cfg.mapCenterLon != null) {
            try { localStorage.setItem('shuttle.map-config', JSON.stringify(cfg)); } catch (e) { /* ignore */ }
            applyMapConfig(cfg.mapCenterLat, cfg.mapCenterLon, cfg.mapZoom);
        }
    }).catch(() => { /* 配置接口不可用时静默用默认值 */ });
})();

// 坐标有效性解析：非有限数字或 (0,0) 占位均视为"无坐标"→ 返回 null；有效 → [lat, lng]。
// 消费约定：凡是拿站点坐标上图（marker/fitBounds/画线接入）前必须过此函数，
// 避免占位站把视野拉到赤道 (0,0) 或画出无效几何。
function parseLatLng(lat, lng) {
    const a = parseFloat(lat), b = parseFloat(lng);
    if (!isFinite(a) || !isFinite(b) || (a === 0 && b === 0)) return null;
    return [a, b];
}

function createMap(elementId, opts) {
    const map = L.map(elementId, Object.assign({ doubleClickZoom: false }, opts || {}))
        .setView(MAP_DEFAULT_VIEW.center, MAP_DEFAULT_VIEW.zoom);
    const basemaps = {
        'OSM 标准': L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19, attribution: '&copy; OpenStreetMap contributors'
        })
    };
    basemaps['OSM 标准'].addTo(map);
    L.control.layers(basemaps, null, { position: 'bottomright' }).addTo(map);
    return { map, basemaps };
}

// 地名地理编码（Photon/komoot，基于 OSM 数据，WGS-84，与 OSM 底图同坐标系、无需 key、国内可直连）。
// 返回 [{ lat, lon, displayName }]；网络异常/无结果时抛错或返回空数组。
// opts.bias 可选：[lat, lon] 结果排序偏好（photon 的 lat/lon 参数，非硬过滤）。
// 默认偏向 MAP_DEFAULT_VIEW 中心（随 app.map.center-* 配置走，默认北京）——
// 不带 bias 时 photon 常把地名（如"霍营"）解析到外省同名村，加默认偏好后本地结果优先；
// stops.html 等既有调用无需改动即受益。
async function geocode(query, opts) {
    const bias = (opts && opts.bias) || MAP_DEFAULT_VIEW.center;
    const url = 'https://photon.komoot.io/api/?limit=6&q=' + encodeURIComponent(query) +
        '&lat=' + bias[0] + '&lon=' + bias[1];
    const resp = await fetch(url);
    if (!resp.ok) throw new Error('地理编码失败 HTTP ' + resp.status);
    const data = await resp.json();
    return (data.features || []).map(f => {
        const c = (f.geometry && f.geometry.coordinates) || [];
        const p = f.properties || {};
        const name = [p.name, p.city, p.state, p.country].filter(Boolean).join(' · ');
        return { lat: c[1], lon: c[0], displayName: name || query };
    });
}
