package me.ningyu.app.nuoche.domain;

import lombok.*;
import me.ningyu.app.nuoche.common.enums.ProviderVendor;
import me.ningyu.app.nuoche.domain.common.Variable;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "nuoche_t_provider")
@Inheritance(strategy = InheritanceType.JOINED)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Provider extends Variable
{
    @Column(name = "vendor", columnDefinition = "VARCHAR(50) DEFAULT 0 COMMENT '推送服务的服务商：NTFY或BARK等'")
    @Enumerated(value = EnumType.STRING)
    private ProviderVendor vendor;

    @Column(name = "url", columnDefinition = "VARCHAR(150) DEFAULT 0 COMMENT '推送到设备的URL地址'")
    private String url;

    @Column(name = "push_key", columnDefinition = "VARCHAR(100) DEFAULT 0 COMMENT '推送网站提供的认证Key'")
    private String pushKey;

    @Column(name = "description", columnDefinition = "TEXT DEFAULT '' COMMENT '描述'")
    private String description;

    @Column(name = "weight", columnDefinition = "INT(11) DEFAULT 0 COMMENT '权重'")
    private int weight = 0;

    @Column(name = "enabled", columnDefinition = "TINYINT(1) DEFAULT 1 COMMENT '是否启用'")
    private boolean enabled = true;

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Provider provider = (Provider) o;
        return vendor == provider.vendor && url.equals(provider.url) && pushKey.equals(provider.pushKey);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(vendor, url, pushKey);
    }
}
