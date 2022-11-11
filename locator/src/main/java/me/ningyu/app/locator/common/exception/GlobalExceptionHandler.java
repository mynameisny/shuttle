package me.ningyu.app.locator.common.exception;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity exceptionHandler(final HttpMessageNotReadableException e, HttpServletRequest request, HttpServletResponse response)
    {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        log.info("请求参数有误：{}", e.getMessage(), e);

        return ResponseEntity.badRequest().body("请求参数有误，详见：" + extractDetailedMessage(e));
    }

    // 等价于：String detailMessage = StringUtils.substringBefore(e.getMessage(), "; nested exception is");
    private String extractDetailedMessage(Throwable e)
    {
        final String message = e.getMessage();
        if (message == null)
        {
            return "";
        }

        final int tailIndex = StringUtils.indexOf(message, "; nested exception is");
        if (tailIndex == -1)
        {
            return message;
        }
        return StringUtils.left(message, tailIndex);
    }
}
