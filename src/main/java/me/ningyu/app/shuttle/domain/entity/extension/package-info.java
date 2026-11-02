/**
 * GTFS 扩展域：承载班车业务自身的扩展实体，与 gtfs 包平级。
 *
 * <p>定位：gtfs 包描述"运输服务本身"（官方规范概念），本包描述围绕它的业务数据。
 *
 * <p>规划中的实体方向：
 * <ul>
 *   <li>媒体类：站点图片、线路配图等；</li>
 *   <li>车队运营：司机管理、车辆管理、班次绑定等。</li>
 * </ul>
 *
 * <p>约束：
 * <ul>
 *   <li>引用 gtfs 包实体遵循项目统一风格（@ManyToOne + referencedColumnName 原生 ID，
 *       或无唯一键/多来源时用 String 弱引用）；</li>
 *   <li>依赖方向仅 extension → gtfs 单向，gtfs 包不得反向依赖本包，
 *       保证 gtfs 实体可独立导出标准 CSV。</li>
 * </ul>
 */
package me.ningyu.app.shuttle.domain.entity.extension;
