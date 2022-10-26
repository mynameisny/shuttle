package me.ningyu.app.locator.common.exception;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
    @ExceptionHandler(NotfoundException.class)
    public ResponseEntity exceptionHandler(final Exception e, HttpServletRequest request, HttpServletResponse response)
    {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        NotfoundException exception = (NotfoundException) e;
        log.info("程序异常：{}", exception.getMsg(), exception);

        return ResponseEntity.notFound().build();
    }
}
