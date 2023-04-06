package me.ningyu.app.nuoche.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "nuoche_t_provider_bark")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bark extends Provider
{
    private String pushPath = "/push";
}
