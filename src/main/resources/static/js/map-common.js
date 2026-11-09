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
const MAP_DEFAULT_VIEW = { center: [39.9042, 116.4074], zoom: 11 };

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
async function geocode(query) {
    const url = 'https://photon.komoot.io/api/?limit=6&q=' + encodeURIComponent(query);
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
