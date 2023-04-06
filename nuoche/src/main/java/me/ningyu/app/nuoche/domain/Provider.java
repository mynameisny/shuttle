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
    @Enumerated(value = EnumType.STRING)
    private ProviderVendor vendor;

    private String url;

    private String pushKey;

    private String description;

    /**
     * 权重
     */
    private int weight = 0;

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
