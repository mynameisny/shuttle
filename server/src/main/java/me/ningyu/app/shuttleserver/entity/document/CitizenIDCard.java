package me.ningyu.app.shuttleserver.entity.document;

import lombok.Data;
import me.ningyu.app.shuttleserver.entity.human.Driver;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name = "citizen_id_card")
@Entity
@Data
public class CitizenIDCard
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    /**
     * 性别
     */
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(M|F)$", message = "性别只能是M或F")
    private String gender;
    
    /**
     * 民族
     */
    @NotBlank(message = "民族不能为空")
    private String nation;
    
    /**
     * 出生日期
     */
    @NotNull(message = "出生日期不能为空")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    
    /**
     * 住址
     */
    @NotBlank(message = "住址不能为空")
    private String address;
    
    /**
     * 公民身份证号码
     */
    @NotBlank(message = "身份证号码不能为空")
    private String number;
    
    /**
     * 签发机关
     */
    @NotBlank(message = "签发机关不能为空")
    private String authority;
    
    /**
     * 签发期限起始日期
     */
    @NotNull(message = "签发期限起始日期不能为空")
    @Temporal(TemporalType.DATE)
    private Date validThroughBegin;

    /**
     * 签发期限截止日期
     */
    @NotNull(message = "签发期限截止日期不能为空")
    @Temporal(TemporalType.DATE)
    private Date validThroughEnd;

    /**
     * 身份证保存路径
     */
    private String cardPath;
    
    @OneToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
}
