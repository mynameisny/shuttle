package me.ningyu.app.easymonger.domain.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.ningyu.app.easymonger.domain.BaseEntity;

import java.util.List;

@Entity
@Table(name = "parse_template")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParseTemplate extends BaseEntity
{
    /**
     * 解析模板属于哪个账号
     */
    @ManyToOne
    private Account account;

    /**
     * 解析模板名称
     */
    @Column(name = "name", columnDefinition = "VARCHAR(200) DEFAULT '' COMMENT '解析模板名称'")
    private String name;

    /**
     * 设备名称
     */
    @Column(name = "device_name", columnDefinition = "VARCHAR(200) DEFAULT '' COMMENT '设备名称'")
    private String deviceName;

    /**
    //  * 解析模板的截图坐标
    //  */
    // @OneToMany(mappedBy = "parseTemplate", cascade = CascadeType.REMOVE)
    // private List<ParseTemplateCoordinate> coordinates;

    /**
     * 解析模板是否启用
     */
    @Column(name = "enabled", columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '解析模板是否启用：1是，0否'")
    private boolean enabled;

    /**
     * 备注（预留字段）
     */
    @Column(name = "remark", columnDefinition = "VARCHAR(500) DEFAULT '' COMMENT '备注（预留字段）'")
    private String remark;
}