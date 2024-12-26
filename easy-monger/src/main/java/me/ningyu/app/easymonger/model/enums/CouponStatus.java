package me.ningyu.app.easymonger.model.enums;

import java.util.EnumSet;

public enum CouponStatus
{
    ON_SALE("待出售", "已上架，等待出售"),

    SELLING("销售中", "已售出，但是买家没确认收货"),

    SOLD("已售出", "买家已经确认收货"),

    REFUND("已退货", "买家已退货"),

    UNKNOWN("未知", "未知状态");

    private final String name;

    private final String description;
    

    CouponStatus(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public static CouponStatus findByValue(String input)
    {
        CouponStatus[] values = CouponStatus.values();
        for (CouponStatus value : values)
        {
            if (value.name.equals(input))
            {
                return value;
            }
        }

        return null;
    }
    
    public boolean canSale()
    {
        EnumSet<CouponStatus> available = EnumSet.of(CouponStatus.ON_SALE, CouponStatus.SELLING);
        return available.contains(this);
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}
