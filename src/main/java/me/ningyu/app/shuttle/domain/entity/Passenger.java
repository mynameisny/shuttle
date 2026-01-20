package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import me.ningyu.app.shuttle.enums.PassengerType;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 乘客实体
 * 记录乘客的基本信息及常坐线路和站点的偏好
 * </pre>
 */
@Entity(name = "passenger")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Passenger extends AbstractAuditable
{
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '姓名'")
    private String name;

    @Column(name = "employee_number", nullable = false, unique = true, columnDefinition = "VARCHAR(50) COMMENT '员工编号'")
    private String employeeNumber;

    @Column(name = "phone", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '手机号'")
    private String phone;

    @Column(name = "type", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '乘客类型（正式/派遣/借调）'")
    @Enumerated(EnumType.STRING)
    private PassengerType type;

    /**
     * 乘客的常坐线路和站点偏好
     * 一对多关系，一个乘客可以有多个偏好设置
     */
    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PassengerPreference> preferences = new ArrayList<>();

    /**
     * 便捷方法：添加偏好
     */
    public void addPreference(PassengerPreference preference)
    {
        preference.setPassenger(this);
        this.preferences.add(preference);
    }

    /**
     * 便捷方法：移除偏好
     */
    public void removePreference(PassengerPreference preference)
    {
        preference.setPassenger(null);
        this.preferences.remove(preference);
    }
}
