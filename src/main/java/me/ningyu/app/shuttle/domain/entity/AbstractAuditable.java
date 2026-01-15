package me.ningyu.app.shuttle.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AbstractAuditable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT COMMENT '记录的唯一ID'")
    private Long id;

    @Column(name = "created_by", columnDefinition = "VARCHAR(50) COMMENT '创建人'")
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", columnDefinition = "DATETIME COMMENT '创建时间'")
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", columnDefinition = "VARCHAR(50) COMMENT '最后修改人'")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date", columnDefinition = "DATETIME COMMENT '最后修改时间'")
    private LocalDateTime lastModifiedDate;
}
