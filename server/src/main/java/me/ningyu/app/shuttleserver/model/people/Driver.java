package me.ningyu.app.shuttleserver.model.people;

import lombok.Data;

import java.util.Date;

@Data
public class Driver
{
    private String id;

    /**
     * 姓名
     */
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
     * 身份证号码
     */
    private String citizenIDNumber;

    /**
     * 身份证保存路径
     */
    private String citizenIDPath;

    /**
     * 手机号码
     */
    private String mobilePhone;
}
