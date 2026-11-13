package me.ningyu.app.shuttle.domain.entity.extension;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.ningyu.app.shuttle.domain.entity.AbstractAuditable;
import me.ningyu.app.shuttle.domain.entity.gtfs.Trip;
import me.ningyu.app.shuttle.enums.SubstituteReason;

import java.time.LocalDate;

/**
 * 班次代班（GTFS 扩展域，非 GTFS 规范字段）。
 *
 * <p>承载「司机请假/有事时由别人代班」的临时覆盖：某天某班次（trip，精确到去/返）
 * 由替补司机代替，不改动线路级的长期分配。查询某天实际司机时，代班记录优先于长期分配。
 */
@Entity(name = "trip_substitution")
@Table(name = "trip_substitution")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripSubstitution extends AbstractAuditable
{
    /**
     * 被代班的班次（引用 GTFS trips.trip_id，精确到去/返）。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", referencedColumnName = "trip_id", nullable = false,
            columnDefinition = "VARCHAR(50) COMMENT '被代班的班次'")
    private Trip trip;

    /**
     * 代班日期。
     */
    @Column(name = "substitute_date", nullable = false, columnDefinition = "DATE COMMENT '代班日期'")
    private LocalDate substituteDate;

    /**
     * 代班司机。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substitute_driver_id", nullable = false, columnDefinition = "BIGINT COMMENT '代班司机'")
    private Driver substituteDriver;

    /**
     * 代班原因（请假 / 有事 / 其他）。
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", columnDefinition = "VARCHAR(20) COMMENT '代班原因'")
    private SubstituteReason reason;
}
