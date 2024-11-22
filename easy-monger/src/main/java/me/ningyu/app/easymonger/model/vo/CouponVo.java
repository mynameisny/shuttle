package me.ningyu.app.easymonger.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CouponVo implements Serializable
{
    private String id;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 券码
     */
    private String code;

    /**
     * 券名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 订单编号
     */
    private String orderNO;

    /**
     * 订单时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    /**
     * 到期时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * 原始价格
     */
    private float originalPrice;

    /**
     * 售出价格
     */
    private float sellingPrice;

    /**
     * 售出时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sellingTime;

    /**
     * 到期自动退
     */
    private boolean expiredAutoRefund;

    /**
     * 无理由随时退
     */
    private boolean noReasonRefund;

    /**
     * 免预约
     */
    private boolean noAppointment;

    /**
     * 多店可用
     */
    private boolean moreShopSupport;

    /**
     * 状态
     */
    // private CouponStatusEnum status;

    /**
     * 状态显示值
     */
    private String statusName;

    /**
     * 状态说明
     */
    private String statusDescription;

    /**
     * 图片路径
     */
    private String imagePath;

    /**
     * 图片ID
     */
    private String imageId;

    /**
     * 标签
     */
    private String tags;

    /**
     * 备注（预留字段）
     */
    private String remark;

    private String accountMobile;

    private String accountPlatform;
}