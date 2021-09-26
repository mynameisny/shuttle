package me.ningyu.app.locator.entity;

public enum CoordType
{
    wgs84("GPS 坐标"), gcj02("国测局加密坐标"), bd09ll("百度经纬度坐标");

    private String desc;


    CoordType(String desc)
    {
        this.desc = desc;
    }
}
