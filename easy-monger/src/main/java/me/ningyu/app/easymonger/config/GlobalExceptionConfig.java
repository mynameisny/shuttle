package me.ningyu.app.easymonger.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionConfig
{
    public static final String ERROR_MESSAGE = "errorMessage";

    private ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> handlerException(HttpServletRequest request, Exception ex) throws JsonProcessingException
    {
        log.error("服务器处理异常", ex);

        String message = ex.getMessage();
        if (ex instanceof DataIntegrityViolationException)
        {
            message = ((DataIntegrityViolationException) ex).getMostSpecificCause().getMessage();
        }

        Map<String, String> map = new HashMap<>();
        map.put(ERROR_MESSAGE, message);
        String body = objectMapper.writeValueAsString(map);

        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> bindExceptionHandler(BindException exception) throws JsonProcessingException
    {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String messages = StringUtils.join(fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(), ";");
        log.error("参数校验异常：{}", messages, exception);

        Map<String, String> map = new HashMap<>();
        map.put(ERROR_MESSAGE, messages);
        String body = objectMapper.writeValueAsString(map);

        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(body);
    }
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> onNotFound(HttpServletRequest request, Exception exception) throws JsonProcessingException
    {
        log.error("无法找到资源", exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
