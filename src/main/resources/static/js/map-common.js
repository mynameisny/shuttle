// Shuttle 管理端地图页公共库：map.html（线路描图）/ stops.html（站点维护）共用。
// 依赖：Leaflet 1.9.4 须先于本文件加载；页面须包含 <div id="toast"></div> 元素与对应 .show/.err 样式。

const $ = id => document.getElementById(id);

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
function createMap(elementId, opts) {
    const map = L.map(elementId, Object.assign({ doubleClickZoom: false }, opts || {})).setView([40.10, 116.30], 12);
    const basemaps = {
        'OSM 标准': L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19, attribution: '&copy; OpenStreetMap contributors'
        })
    };
    basemaps['OSM 标准'].addTo(map);
    L.control.layers(basemaps, null, { position: 'bottomright' }).addTo(map);
    return { map, basemaps };
}
