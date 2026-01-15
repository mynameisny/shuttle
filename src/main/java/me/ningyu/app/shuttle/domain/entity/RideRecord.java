package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalTime;

@Entity(name = "ride_record")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRecord extends AbstractAuditable
{
    @Column(name = "passenger", nullable = false, columnDefinition = "VARCHAR(100) COMMENT '乘客'")
    private String passenger;

    @Column(name = "aboard_time", nullable = false, columnDefinition = "TIME COMMENT '上车时间'")
    private LocalTime aboardTime;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ShuttleSchedule schedule;
}
