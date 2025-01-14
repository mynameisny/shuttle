package me.ningyu.app.easymonger.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.model.enums.CouponStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CouponDto implements Serializable
{
    private String accountId;

    private String brand;

    private String code;

    private String name;

    private String description;

    private String orderNO;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    private float originalPrice;

    private float sellingPrice;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sellingTime;

    private boolean expiredAutoRefund;

    private boolean noReasonRefund;

    private boolean noAppointment;

    private boolean moreShopSupport;

    private CouponStatus status;

    private String imagePath;

    private String tags;

    private String remark;
}