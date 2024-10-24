package me.ningyu.app.easymonger.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BusinessException
{

    public InvalidTokenException(String message)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), message);
    }

    public InvalidTokenException(String bizCode, String message)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), bizCode, message);
    }

}
