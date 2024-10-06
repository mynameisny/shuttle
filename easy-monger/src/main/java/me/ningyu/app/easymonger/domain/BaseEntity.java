package me.ningyu.app.easymonger.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 使用 hibernate UUIDHexGenerator 会生成一条警告，说生成的 uuid 不符合规范，推荐使用 UUIDGenerator，生成的字符串默认有 '-' 连接，导致最大长度长于 32，这里把 @column 去掉，使用默认 255 长度
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class BaseEntity implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36) NOT NULL COMMENT '主键id'", nullable = false)
    private String id;

    @Version
    private Integer version;

    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime modifiedDate;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "is_deleted", columnDefinition = " tinyint(1) default 0", nullable = false)
    private int isDeleted;
    
    @Override
    public final boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        
        BaseEntity that = (BaseEntity) o;
        return isDeleted == that.isDeleted && id.equals(that.id) && Objects.equals(version, that.version) && Objects.equals(createdDate, that.createdDate) && Objects.equals(modifiedDate, that.modifiedDate) && Objects.equals(createdBy, that.createdBy) && Objects.equals(lastModifiedBy, that.lastModifiedBy);
    }
    
    @Override
    public int hashCode()
    {
        int result = id.hashCode();
        result = 31 * result + Objects.hashCode(version);
        result = 31 * result + Objects.hashCode(createdDate);
        result = 31 * result + Objects.hashCode(modifiedDate);
        result = 31 * result + Objects.hashCode(createdBy);
        result = 31 * result + Objects.hashCode(lastModifiedBy);
        result = 31 * result + isDeleted;
        return result;
    }
}
