#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
重排 network 表的自增主键 id（保持 network_id 字符串不变），使 id 顺序 = 期望的业务展示顺序。

为什么安全：
- route_network 外键引用的是 network.network_id（字符串），不是 network.id 主键；
- 故重排 id 只改 network 表本身，不影响任何线路的标签挂载。

用法：
1. 编辑下方 ORDER 列表为期望的 network_id 顺序（必须恰好覆盖当前全部标签，不重不漏）；
2. 先跑一遍无参（dry-run 预览），确认无误后加 --execute 执行。

依赖：pymysql。连接参数以 src/main/resources/application.yml 为准（密码可能轮换，执行前请核对）。
"""
import sys, json, time, os, re
import pymysql


def _local_db_password():
    """从 application-local.yml 读取数据库密码（该文件已被 .gitignore，避免硬编码密码入库）。"""
    path = os.path.join(os.path.dirname(os.path.abspath(__file__)),
                        '..', 'src', 'main', 'resources', 'application-local.yml')
    try:
        with open(path, encoding='utf-8') as f:
            text = f.read()
        m = re.search(r'^\s*password:\s*(\S+)\s*$', text, re.M)
        if m:
            return m.group(1)
    except OSError:
        pass
    return os.environ.get('SHUTTLE_DB_PASSWORD', '')


DB = dict(host='127.0.0.1', port=3306, user='ucmp_dev',
          password=_local_db_password(), database='ucmp_shuttle', charset='utf8mb4')
EXEC = '--execute' in sys.argv

# ===== 期望顺序：按此列表重排 id（未来调整顺序只改这里）=====
ORDER = [
    'COMMUTER',       # 班车
    'LOCAL_SHUTTLE',  # 园区周边短驳
    'CHANGPING',      # 昌平办公区
    'BAJIAO',         # 八角办公区
    'NORTH',          # 城北片区
    'EAST',           # 城东片区
    'WEST_CENTER',    # 城西南片区
    'INTERCITY',      # 跨城线路
    'ZHUOZHOU_TO_CP', # 涿州-昌平
]
TMP_OFFSET = 10000

conn = pymysql.connect(**DB)  # 单事务 + 显式 commit
cur = conn.cursor()

# 1. 备份
cur.execute('SELECT * FROM network')
cols = [d[0] for d in cur.description]
rows = [dict(zip(cols, r)) for r in cur.fetchall()]
with open('/tmp/shuttle_backup_network_reorder.json', 'w', encoding='utf-8') as f:
    json.dump(rows, f, ensure_ascii=False, default=str)
print(f'[备份] network {len(rows)} 行 -> /tmp/shuttle_backup_network_reorder.json')

# 2. 计算映射并校验 ORDER 覆盖
cur.execute('SELECT id, network_id, network_name FROM network ORDER BY id')
cur_map = {r[1]: (r[0], r[2]) for r in cur.fetchall()}  # network_id -> (id, name)
missing = set(cur_map.keys()) - set(ORDER)
extra = set(ORDER) - set(cur_map.keys())
if missing or extra:
    print(f'[错误] ORDER 与库不一致：库中缺少 {sorted(missing) or "无"}，ORDER 多余 {sorted(extra) or "无"}')
    sys.exit(1)

print('\n===== 重排计划（id 旧 -> 新）=====')
for new_id, nid in enumerate(ORDER, 1):
    old_id, name = cur_map[nid]
    print(f'  #{new_id:>2} {nid:<16} {name:<12} id: {old_id} -> {new_id}')

if not EXEC:
    print('\n（用 --execute 执行）')
    cur.close(); conn.close(); sys.exit(0)

# 3. 执行（单事务，两步重命名腾空间）
cur.execute('UPDATE network SET id = id + %s ORDER BY id DESC', (TMP_OFFSET,))
for new_id, nid in enumerate(ORDER, 1):
    cur.execute('UPDATE network SET id = %s, last_modified_by = %s, last_modified_date = NOW() '
                'WHERE network_id = %s', (new_id, 'ningyu-fix-reorder', nid))
conn.commit()
print(f'\n[执行] 已重排 {len(ORDER)} 个标签的 id 并 commit')

# 恢复自增起点
cur.execute('ALTER TABLE network AUTO_INCREMENT = %s', (len(ORDER) + 1,))
conn.commit()

# 4. 校验（新连接）
cur.close(); conn.close()
conn2 = pymysql.connect(**DB)
cur2 = conn2.cursor()
cur2.execute('SELECT id, network_id, network_name FROM network ORDER BY id')
print('\n===== 校验：重排后 network 表 =====')
for r in cur2.fetchall():
    print(f'  id={r[0]:>2} {r[1]:<16} {r[2]}')
cur2.execute('SELECT COUNT(DISTINCT id), COUNT(*) FROM network')
d, c = cur2.fetchone()
print(f'\n[校验] id 去重数={d}，总行数={c}（应相等）')
cur2.execute('SELECT COUNT(*) FROM route_network')
print(f'[校验] route_network 行数={cur2.fetchone()[0]}（应不变，外键引用 network_id 未受影响）')
cur2.close(); conn2.close()

# 5. 复验
time.sleep(3)
conn3 = pymysql.connect(**DB)
cur3 = conn3.cursor()
cur3.execute('SELECT COUNT(*) FROM network')
print(f'[复验] 3 秒后 network 总数={cur3.fetchone()[0]}')
cur3.close(); conn3.close()
print('[完成]')
