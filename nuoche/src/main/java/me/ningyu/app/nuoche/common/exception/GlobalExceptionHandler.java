package me.ningyu.app.nuoche.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
    @ExceptionHandler(NotfoundException.class)
    public ResponseEntity<?> notFoundExceptionHandler(final Exception e, HttpServletRequest request, HttpServletResponse response)
    {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        NotfoundException exception = (NotfoundException) e;
        log.info("程序异常：{}", exception.getMsg(), exception);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMsg());
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<?> duplicateExceptionHandler(final Exception e, HttpServletRequest request, HttpServletResponse response)
    {
        response.setStatus(HttpStatus.CONFLICT.value());
        DuplicateException exception = (DuplicateException) e;
        log.info("程序异常：{}", exception.getMsg(), exception);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMsg());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(final Exception e, HttpServletRequest request, HttpServletResponse response)
    {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
        log.info("请求参数有误：{}", exception.getMessage(), exception);

        BindingResult exceptions = exception.getBindingResult();
        List<ObjectError> errors = exceptions.getAllErrors();
        List<String> errorList = new ArrayList<>();
        if(ObjectUtils.isNotEmpty(errors))
        {
            errorList.add(errors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", ")));
        }

        return ResponseEntity.badRequest().body("请求参数有误，详见：" + errorList);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> exceptionHandler(final HttpMessageNotReadableException e, HttpServletRequest request, HttpServletResponse response)
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
