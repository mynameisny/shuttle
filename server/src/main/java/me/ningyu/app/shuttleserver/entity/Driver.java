package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "driver")
@Entity
@Data
public class Driver
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    /**
     * 性别：Male或Female
     */
    private String gender;
    
    /**
     * 出生日期
     */
    private Date birthDate;
    
    /**
     * 身份证
     */
    @OneToOne(mappedBy = "driver")
    private CitizenIDCard card;
    
    /**
     * 身份证保存路径
     */
    private String cardPath;
    
    /**
     * 手机号码
     */
    private String mobilePhone;
}
