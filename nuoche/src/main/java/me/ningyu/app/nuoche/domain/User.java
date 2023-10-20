package me.ningyu.app.nuoche.domain;

import lombok.*;
import me.ningyu.app.nuoche.common.enums.Gender;
import me.ningyu.app.nuoche.domain.common.Variable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nuoche_t_user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User extends Variable
{
    @Column(name = "code", columnDefinition = "VARCHAR(150) DEFAULT 0 COMMENT '用户编码'", unique = true)
    private String code;

    @Column(name = "name", columnDefinition = "VARCHAR(150) DEFAULT 0 COMMENT '用户名称'")
    private String name;

    @Column(name = "gender", columnDefinition = "VARCHAR(150) DEFAULT 0 COMMENT '用户性别'")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "password", columnDefinition = "VARCHAR(150) DEFAULT 0 COMMENT '用户密码'")
    private String password;

    @ElementCollection
    @CollectionTable(name = "nuoche_t_user_phone", joinColumns = {@JoinColumn(name = "user_id")})
    private Set<String> phones;

    @ElementCollection
    @CollectionTable(name = "nuoche_t_user_email", joinColumns = {@JoinColumn(name = "user_id")})
    private Set<String> emails;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "nuoche_t_user_provider", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "product_id")})
    @ToString.Exclude
    private Set<Provider> providers = new HashSet<>();
}


