package me.ningyu.app.locator.common.enums;

public enum Coordinate
{
    /**
     * WGS-84坐标系：地心坐标系，GPS原始坐标体系
     */
    WGS84,

    /**
     * GCJ-02 坐标系：国测局坐标，火星坐标系
     */
    GCJ02,

    /**
     * CGCS2000坐标系：国家大地坐标系
     */
    CGCS2000,

    /**
     * BD-09坐标系：百度中国地图所采用的坐标系，由GCJ-02进行进一步的偏移算法得到。
     */
    BD09
}
