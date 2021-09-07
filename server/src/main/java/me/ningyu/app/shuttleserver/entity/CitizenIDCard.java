package me.ningyu.app.shuttleserver.entity;

import lombok.Data;

import javax.persistence.*;
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
    private String name;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 民族
     */
    private String nation;
    
    /**
     * 出生日期
     */
    private Date birthDate;
    
    /**
     * 住址
     */
    private String address;
    
    /**
     * 公民身份证号码
     */
    private String number;
    
    /**
     * 签发机关
     */
    private String authority;
    
    /**
     * 签发期限起始日期
     */
    private Date validThroughBegin;
    
    /**
     * 签发期限截止日期
     */
    private Date validThroughEnd;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private Driver driver;
}
