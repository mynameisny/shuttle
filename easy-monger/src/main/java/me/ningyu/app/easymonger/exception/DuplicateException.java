package me.ningyu.app.easymonger.exception;

import org.springframework.http.HttpStatus;

public class DuplicateException extends BusinessException
{

    public DuplicateException(String message)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), message);
    }

    public DuplicateException(String bizCode, String message)
    {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), bizCode, message);
    }

}
