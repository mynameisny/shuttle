package me.ningyu.app.easymonger.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BusinessException extends RuntimeException
{
    private int httpCode;

    private String bizCode;

    private String message;
}
