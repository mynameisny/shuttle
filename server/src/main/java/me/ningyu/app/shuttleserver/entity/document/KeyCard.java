package me.ningyu.app.shuttleserver.entity.document;

import lombok.Data;

import javax.persistence.*;

/**
 * 出入证
 */
@Table(name = "keycard")
@Entity
@Data
public class KeyCard
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
