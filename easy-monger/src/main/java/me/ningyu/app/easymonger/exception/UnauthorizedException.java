package me.ningyu.app.easymonger.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException
{

    public UnauthorizedException(String message)
    {
        super(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), message);
    }

    public UnauthorizedException(String bizCode, String message)
    {
        super(HttpStatus.UNAUTHORIZED.value(), bizCode, message);
    }

}
