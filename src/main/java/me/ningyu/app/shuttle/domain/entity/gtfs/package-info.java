/**
 * GTFS Schedule 规范实体域，对应 gtfs.org/documentation/schedule/reference 的 32 个文件。
 *
 * <p>约束：
 * <ul>
 *   <li>仅收录 GTFS 官方文件对应的实体，字段与规范一一对应（列名、注释取自规范 Description）；</li>
 *   <li>禁止添加规范之外的字段，业务扩展属性一律放 {@link me.ningyu.app.shuttle.domain.entity.extension} 包；</li>
 *   <li>类型层面的有意差异：GTFS 数值枚举存枚举名（@Enumerated(STRING)）、0/1 存 Boolean、
 *       LocalTime 无法表达 ≥24:00:00 的跨午夜时刻；</li>
 *   <li>AbstractAuditable 审计字段为 ORM 基础设施，不计入规范对应关系，导出标准 CSV 时忽略。</li>
 * </ul>
 */
package me.ningyu.app.shuttle.domain.entity.gtfs;
