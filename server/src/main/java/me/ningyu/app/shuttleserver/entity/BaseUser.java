package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseUser
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    /**
     * 性别：Male或Female
     */
    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(M|F)$", message = "性别只能是M或F")
    private String gender;
    
    /**
     * 出生日期
     */
    @NotNull(message = "出生日期不能为空")
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    
    /**
     * 身份证
     */
    @OneToOne(mappedBy = "driver")
    private CitizenIDCard card;
    
    /**
     * 手机号码
     */
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$", message = "手机号码不合法")
    private String mobile;
    
    /**
     * 电子邮箱
     */
    @Pattern(regexp = "(^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)$)", message = "手机号码不合法")
    @Column(unique = true)
    private String email;
}
