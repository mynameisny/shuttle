package me.ningyu.app.locator.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DuplicateException extends RuntimeException
{
    private String msg;
}
