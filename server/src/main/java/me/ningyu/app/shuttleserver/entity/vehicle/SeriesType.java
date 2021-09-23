package me.ningyu.app.shuttleserver.entity.vehicle;

public enum SeriesType
{
    SEDAN("轿车"),
    VAN(" 厢式货车"),
    TRUCK("卡车"),
    SUV("运动型多用途汽车"),
    MPV("多用途汽车"),
    BUS("客车");

    private String name;
    private String chineseName;

    SeriesType(String chineseName)
    {
        this.chineseName = chineseName;
    }
}
