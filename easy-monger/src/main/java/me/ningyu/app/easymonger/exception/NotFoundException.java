package me.ningyu.app.easymonger.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException
{

    public NotFoundException(String message)
    {
        super(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), message);
    }

    public NotFoundException(String bizCode, String message)
    {
        super(HttpStatus.NOT_FOUND.value(), bizCode, message);
    }

}
