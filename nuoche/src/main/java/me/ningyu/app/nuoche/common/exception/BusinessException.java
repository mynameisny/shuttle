package me.ningyu.app.nuoche.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class BusinessException extends RuntimeException
{
    private String msg;
}
