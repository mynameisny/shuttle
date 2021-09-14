package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 行驶证
 */
@Table(name = "vehicle_license")
@Entity
@Data
public class VehicleLicense
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 号牌号码
     */
    @NotBlank(message = "号牌号码不能为空")
    private String plateNo;

    /**
     * 车辆类型
     */
    @NotBlank(message = "车辆类型不能为空")
    private String vehicleType;

    /**
     * 所有人
     */
    @NotBlank(message = "所有人不能为空")
    private String owner;

    /**
     * 使用性质
     */
    @NotBlank(message = "使用性质不能为空")
    private String useCharacter;

    /**
     * 住址
     */
    @NotBlank(message = "住址类型不能为空")
    private String address;

    /**
     * 品牌型号
     */
    @NotBlank(message = "品牌型号不能为空")
    private String model;

    /**
     * 车辆类型
     */
    @NotBlank(message = "车辆类型不能为空")
    private String vin;

    /**
     * 发动机号码
     */
    @NotBlank(message = "发动机号码不能为空")
    private String engineNo;

    /**
     * 注删日期
     */
    @NotBlank(message = "注册日期不能为空")
    @Temporal(TemporalType.DATE)
    private Date registerDate;

    /**
     * 发证日期
     */
    @NotBlank(message = "发证日期不能为空")
    @Temporal(TemporalType.DATE)
    private Date issueDate;

    /**
     * 档案编号
     */
    @NotBlank(message = "档案编号不能为空")
    private String number;

    /**
     * 核定载人数
     */
    @NotNull
    private int peoples;

    /**
     * 总质量（吨）
     */
    @NotNull
    private int totalQuality;

    /**
     * 外观尺寸：长
     */
    @NotNull
    private int length;

    /**
     * 外观尺寸：宽
     */
    @NotNull
    private int width;

    /**
     * 外观尺寸：高
     */
    @NotNull
    private int height;

    /**
     * 备注
     */
    private String remark;
}
