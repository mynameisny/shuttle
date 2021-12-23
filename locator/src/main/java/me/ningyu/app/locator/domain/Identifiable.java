package me.ningyu.app.locator.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
@Getter
@Setter
public class Identifiable implements Serializable
{
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(length = 36)
    private String id;
}