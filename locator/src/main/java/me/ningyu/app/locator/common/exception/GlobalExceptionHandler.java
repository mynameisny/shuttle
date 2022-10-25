package me.ningyu.app.locator.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(NotfoundException.class)
    public ResponseEntity exceptionHandler(final Exception e, HttpServletRequest request, HttpServletResponse response)
    {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        NotfoundException exception = (NotfoundException) e;
        return ResponseEntity.notFound().build();
    }
}
