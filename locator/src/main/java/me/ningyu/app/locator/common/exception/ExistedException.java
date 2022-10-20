package me.ningyu.app.locator.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExistedException extends RuntimeException
{
    private String id;
    private String msg;
}
