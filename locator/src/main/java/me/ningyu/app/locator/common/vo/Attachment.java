package me.ningyu.app.locator.common.vo;

import lombok.Getter;
import lombok.Setter;
import me.ningyu.app.locator.domain.Variable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "locator_attachement")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Attachment extends Variable
{}
