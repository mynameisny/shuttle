/* ==========================================================================
   班车调度管理 · 应用外壳（侧边栏 + 页面容器）
   --------------------------------------------------------------------------
   用法（页面 <body> 内只需写两段，其余由本脚本生成）：

     <body>
       <div class="app-shell">
         <aside class="sidebar" id="sidebar"></aside>
         <div class="main-area">
           <div class="page" id="page">  ...页面内容...  </div>
         </div>
       </div>
       <div id="toast"></div>

   页面须在 <head> 里给出自己的位置，用于高亮菜单 + 生成面包屑：

     <meta name="nav" content="vehicles.archives">     // 对应 MENU 里的 key
     <meta name="desc" content="车辆台账与状态维护">    // 可选：页头副标题

   也可用 <div class="page" data-crumbs="车辆管理,车辆档案"> 覆盖面包屑。
   ========================================================================== */

// 菜单定义。group = 分组小标题；items = 二级项。
// ready:false 表示后端尚未实现，点击进入占位页 placeholder.html?m=<key>
const MENU = [
    {
        group: '总览',
        items: [
            { key: 'dashboard', name: '工作台', href: 'dashboard.html', icon: 'grid' }
        ]
    },
    {
        group: '车队管理',
        items: [
            {
                key: 'vehicles', name: '车辆管理', icon: 'bus', children: [
                    { key: 'vehicles.archives', name: '车辆档案', href: 'vehicles.html', ready: true },
                    { key: 'vehicles.maintenance', name: '车辆维护', ready: false, note: '保养计划、维修工单、费用台账' },
                    { key: 'vehicles.status', name: '车辆状态', ready: false, note: '实时位置、在线/离线、里程与能耗' }
                ]
            },
            {
                key: 'drivers', name: '司机管理', icon: 'user', children: [
                    { key: 'drivers.archives', name: '司机档案', href: 'drivers.html', ready: true },
                    { key: 'drivers.records', name: '驾驶记录', ready: false, note: '出车记录、违章、安全考核' }
                ]
            }
        ]
    },
    {
        group: '运营管理',
        items: [
            {
                key: 'routes', name: '线路管理', icon: 'route', children: [
                    { key: 'routes.list', name: '班车线路', href: 'routes.html', ready: true },
                    { key: 'routes.stops', name: '站点', href: 'stops.html', ready: true },
                    { key: 'routes.direction', name: '运行方向', href: 'map.html', ready: true }
                ]
            },
            {
                key: 'trips', name: '班次管理', icon: 'clock', children: [
                    { key: 'trips.calendar', name: '服务日历', href: 'calendars.html', ready: true },
                    { key: 'trips.plan', name: '发车计划', href: 'trips.html', ready: true },
                    { key: 'trips.assign', name: '班次安排', ready: false, note: '线路 × 班次 × 车辆 × 司机 指派' }
                ]
            },
            {
                key: 'ops', name: '运营管理', icon: 'activity', children: [
                    { key: 'ops.reports', name: '运营报表', href: 'reports.html', ready: true },
                    { key: 'ops.records', name: '运营记录', ready: false, note: '实际发车、载客量、准点率' },
                    { key: 'ops.dispatch', name: '车辆调度', ready: false, note: '临时调车、加车、替班' }
                ]
            },
            {
                key: 'twin', name: '数字孪生', icon: 'cube', children: [
                    { key: 'twin.scene', name: '3D 车辆场景', ready: false, note: '车队三维可视化场景' },
                    { key: 'twin.status', name: '车辆状态展示', ready: false, note: '孪生体实时状态映射' }
                ]
            }
        ]
    },
    {
        group: '系统',
        items: [
            {
                key: 'system', name: '系统管理', icon: 'settings', children: [
                    { key: 'system.users', name: '用户', ready: false, note: '账号与人员绑定' },
                    { key: 'system.roles', name: '角色', ready: false, note: '角色与权限组' },
                    { key: 'system.perms', name: '权限', ready: false, note: '菜单与数据权限' }
                ]
            }
        ]
    }
];

// 图标库（16x16 线性图标，stroke 由 CSS 提供）
const ICONS = {
    grid: '<rect x="3" y="3" width="6" height="6" rx="1.4"/><rect x="11" y="3" width="6" height="6" rx="1.4"/><rect x="3" y="11" width="6" height="6" rx="1.4"/><rect x="11" y="11" width="6" height="6" rx="1.4"/>',
    bus: '<path d="M4 5.5A2.5 2.5 0 0 1 6.5 3h7A2.5 2.5 0 0 1 16 5.5V14a1 1 0 0 1-1 1h-1.2a1 1 0 0 1-1-1v-.6H7.2V14a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1z"/><path d="M4 9h12"/><circle cx="7" cy="12" r=".6"/><circle cx="13" cy="12" r=".6"/>',
    user: '<circle cx="10" cy="6.2" r="3.1"/><path d="M4 17.2c0-2.8 2.7-4.6 6-4.6s6 1.8 6 4.6"/>',
    route: '<circle cx="5" cy="5" r="2"/><circle cx="15" cy="15" r="2"/><path d="M5 7v3.5A2.5 2.5 0 0 0 7.5 13H12a2.5 2.5 0 0 1 2.5 2.5V13"/>',
    clock: '<circle cx="10" cy="10" r="7"/><path d="M10 6v4.3l2.8 1.7"/>',
    activity: '<path d="M2.5 10h3.2l1.8-4.6L11 15l1.9-5h4.6"/>',
    cube: '<path d="M10 2.6 17 6.4v7.2L10 17.4 3 13.6V6.4z"/><path d="M3 6.4 10 10l7-3.6M10 10v7.4"/>',
    settings: '<circle cx="10" cy="10" r="2.6"/><path d="M10 2.6v2M10 15.4v2M4.8 4.8l1.4 1.4M13.8 13.8l1.4 1.4M2.6 10h2M15.4 10h2M4.8 15.2l1.4-1.4M13.8 6.2l1.4-1.4"/>',
    search: '<circle cx="9" cy="9" r="5.4"/><path d="M13 13l3.4 3.4"/>',
    home: '<path d="M3.2 9.2 10 3.4l6.8 5.8V16a1 1 0 0 1-1 1h-3.6v-4.4H7.8V17H4.2a1 1 0 0 1-1-1z"/>',
    plus: '<path d="M10 4.2v11.6M4.2 10h11.6"/>',
    x: '<path d="M5.2 5.2l9.6 9.6M14.8 5.2l-9.6 9.6"/>',
    chevronLeft: '<path d="M12.2 4.6 6.8 10l5.4 5.4"/>',
    chevronRight: '<path d="M7.8 4.6 13.2 10l-5.4 5.4"/>',
    edit: '<path d="M12.6 3.4l4 4L7.4 16.6H3.4v-4z"/>',
    trash: '<path d="M4 6.4h12M8 6.4V4.2h4v2.2M5.6 6.4l.7 10a1 1 0 0 0 1 .9h5.4a1 1 0 0 0 1-.9l.7-10"/>',
    alert: '<path d="M10 3.2 17.4 16H2.6z"/><path d="M10 8v3.4M10 13.6v.6"/>',
    shield: '<path d="M10 2.8 16.4 5v5.2c0 3.4-2.5 5.8-6.4 7-3.9-1.2-6.4-3.6-6.4-7V5z"/>',
    pin: '<path d="M10 17.4s5.4-5 5.4-9a5.4 5.4 0 1 0-10.8 0c0 4 5.4 9 5.4 9z"/><circle cx="10" cy="8.2" r="2"/>',
    wrench: '<path d="M12.4 3.2a4.4 4.4 0 0 0-5.6 5.6l-3.4 3.4a1.8 1.8 0 0 0 2.5 2.5l3.4-3.4a4.4 4.4 0 0 0 5.6-5.6l-2.6 2.6-2.4-.6-.6-2.4z"/>',
    inBox: '<path d="M3 6.6 10 3l7 3.6V14L10 17.6 3 14z"/><path d="M3 6.6 10 10l7-3.4"/>'
};

function iconSvg(name, cls) {
    const d = ICONS[name];
    if (!d) return '';
    return '<svg class="ico ' + (cls || '') + '" viewBox="0 0 20 20" aria-hidden="true">' + d + '</svg>';
}

// 当前页 nav key（来自 <meta name="nav">）
function currentNavKey() {
    const m = document.querySelector('meta[name="nav"]');
    return m ? m.content.trim() : '';
}

// 侧边栏折叠状态持久化
const SB_KEY = 'shuttle.sidebar.collapsed';

function buildSidebar(mount, activeKey) {
    const activeParent = activeKey.split('.')[0];

    let html = '' +
        '<a class="sb-brand" href="dashboard.html">' +
        '  <span class="logo">班</span>' +
        '  <span class="brand-text"><span class="brand-name">班车调度管理</span>' +
        '  <span class="brand-sub">Shuttle Ops</span></span>' +
        '</a>' +
        '<nav class="sb-nav">';

    MENU.forEach(sec => {
        html += '<div class="sb-section">' + sec.group + '</div>';
        sec.items.forEach(it => {
            const isChildActive = activeKey === it.key || activeKey.startsWith(it.key + '.');
            if (it.children && it.children.length) {
                // 父项：可跳转到第一个 ready 的子项；父项高亮表示“当前在这个模块内”
                const firstReady = it.children.find(c => c.ready) || it.children[0];
                const parentHref = firstReady.ready ? firstReady.href : ('placeholder.html?m=' + encodeURIComponent(firstReady.key));
                const isGroupActive = activeKey.startsWith(it.key + '.') || activeKey === it.key;
                // 当前页是某个子项时，父项只做次要强调（避免父子同时高亮）
                const hasActiveChild = activeKey.startsWith(it.key + '.') && activeKey !== it.key;
                const parentCls = 'sb-item' + (hasActiveChild ? ' parent-of-active' : (isGroupActive ? ' active' : ''));
                html +=
                    '<a class="' + parentCls + '" href="' + parentHref + '"' +
                    ' title="' + _e(it.name) + '" data-nav="' + it.key + '">' +
                    iconSvg(it.icon) + '<span class="txt">' + _e(it.name) + '</span></a>';
                // 二级项：当前模块的子项展开显示。
                // sb-sub 类用于折叠态整体隐藏——二级项没有图标，折叠后只剩 64px 空条，
                // 与其留下空白残影，不如折叠态直接收起二级层（主流 SaaS 后台行为）。
                if (isGroupActive) {
                    it.children.forEach(c => {
                        const href = c.ready && c.href ? c.href : ('placeholder.html?m=' + encodeURIComponent(c.key));
                        const cls = 'sb-item sb-sub' + (activeKey === c.key ? ' active' : '') + (c.ready ? '' : ' disabled');
                        html +=
                            '<a class="' + cls + '" href="' + href + '" title="' + _e(c.name) + '" data-nav="' + c.key + '"' +
                            ' style="padding-left:34px;font-size:12.5px">' +
                            '<span class="txt">' + _e(c.name) + '</span>' +
                            (c.ready ? '' : '<span class="badge-soon">建设</span>') +
                            '</a>';
                    });
                }
            } else {
                const href = it.ready === false ? ('placeholder.html?m=' + encodeURIComponent(it.key)) : it.href;
                html +=
                    '<a class="sb-item' + (activeKey === it.key ? ' active' : '') + '" href="' + href + '"' +
                    ' title="' + _e(it.name) + '" data-nav="' + it.key + '">' +
                    iconSvg(it.icon) + '<span class="txt">' + _e(it.name) + '</span></a>';
            }
        });
    });

    html += '</nav>' +
        '<div class="sb-toggle-wrap">' +
        '<button class="sb-toggle" id="sbCollapse" type="button" title="收起侧边栏" aria-label="收起/展开侧边栏">' +
        iconSvg('chevronLeft') + '<span class="txt">收起侧边栏</span></button>' +
        '</div>' +
        '<div class="sb-foot">数据源 GTFS · 本地台账</div>';

    mount.innerHTML = html;

    // 折叠/展开：按钮在侧边栏底部，任何状态下都可见可点（折叠后靠它恢复展开）。
    // 状态 class 挂在 <html>（sb-collapsed），与各页 <head> 的防闪烁内联脚本共用，
    // 保证刷新/跳转时首帧即折叠，不出现"先展开再缩回"的 FOUC。
    const root = document.documentElement;
    if (localStorage.getItem(SB_KEY) === '1') root.classList.add('sb-collapsed');
    const btn = mount.querySelector('#sbCollapse');
    if (btn) {
        const syncTitle = () => {
            btn.title = root.classList.contains('sb-collapsed') ? '展开侧边栏' : '收起侧边栏';
        };
        syncTitle();
        btn.addEventListener('click', e => {
            e.preventDefault();
            e.stopPropagation();
            const collapsed = root.classList.toggle('sb-collapsed');
            localStorage.setItem(SB_KEY, collapsed ? '1' : '0');
            syncTitle();
        });
    }
}

// 把 <meta name="nav"> 转成面包屑写入 .page-head 的 .crumbs（若页面没手写）
function buildCrumbs() {
    const key = currentNavKey();
    if (!key) return;
    const el = document.querySelector('[data-auto-crumbs]');
    if (!el) return;

    let secName = '', itemName = '', childName = '';
    for (const sec of MENU) {
        for (const it of sec.items) {
            if (key === it.key) { secName = sec.group; itemName = it.name; }
            else if (key.startsWith(it.key + '.') && it.children) {
                secName = sec.group; itemName = it.name;
                const c = it.children.find(x => x.key === key);
                if (c) childName = c.name;
            }
        }
    }

    let html = '<a href="dashboard.html">' + iconSvg('home') + '</a><span class="sep">/</span>';
    if (secName) html += '<span>' + _e(secName) + '</span><span class="sep">/</span>';
    html += '<span>' + _e(itemName) + '</span>';
    if (childName) html += '<span class="sep">/</span><span class="cur">' + _e(childName) + '</span>';
    el.innerHTML = html;
}

// 轻量转义。
// 注意：这里刻意不声明 `esc`，因为页面很可能同时引入 map-common.js（其中已有 function esc()）。
// 顶层 `const esc` 与 `function esc` 重复声明会抛 SyntaxError，导致整段脚本不执行
// （2026-11-09 曾因此出现“车辆/司机列表全空”）。此处用 _e() 作私有名，避免任何冲突。
function _e(s) {
    return String(s == null ? '' : s)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

// 初始化外卖壳（DOM ready 后调用一次）
function initShell() {
    const sb = document.getElementById('sidebar');
    if (sb) buildSidebar(sb, currentNavKey());
    buildCrumbs();
}
