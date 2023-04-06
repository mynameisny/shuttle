package me.ningyu.app.nuoche.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DuplicateException extends RuntimeException
{
    private String msg;
}
