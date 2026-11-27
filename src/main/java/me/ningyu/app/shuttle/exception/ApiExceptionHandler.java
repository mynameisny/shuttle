package me.ningyu.app.shuttle.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 全局异常处理器：统一 API 错误响应体。
 *
 * <p>约定：
 * <ul>
 *   <li>{@link ResponseStatusException}：尊重其状态码（400/404/409 等），消息取 reason；</li>
 *   <li>{@link IllegalArgumentException}：业务校验/参数非法 → 400；</li>
 *   <li>{@link MethodArgumentNotValidException}：@Valid 字段校验失败 → 400（聚合字段错误）；</li>
 *   <li>{@link HttpMessageNotReadableException}：请求体解析失败（非法枚举、JSON 格式错误）→ 400；</li>
 *   <li>其余未捕获异常 → 500（记录日志，对外隐藏内部细节）。</li>
 * </ul>
 *
 * <p>错误体统一为 {@link ApiError}，前端约定读取 {@code message} 字段展示。
 */
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler
{
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request)
    {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null)
        {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return build(status, message, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request)
    {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /** 登录认证失败（密码错误 / 账号禁用 / 锁定）→ 401。 */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex, HttpServletRequest request)
    {
        String message = ex instanceof BadCredentialsException ? "用户名或密码错误" : "认证失败或账号不可用";
        return build(HttpStatus.UNAUTHORIZED, message, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request)
    {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message.isBlank() ? "参数校验失败" : message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request)
    {
        return build(HttpStatus.BAD_REQUEST, "请求体格式错误或包含非法值", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request)
    {
        log.error("未处理异常: {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误", request);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request)
    {
        ApiError body = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    /**
     * 统一 API 错误响应体。
     *
     * @param timestamp 发生时间
     * @param status    HTTP 状态码
     * @param code      错误码（HTTP 状态枚举名，如 BAD_REQUEST / CONFLICT / NOT_FOUND）
     * @param message   错误消息（前端展示用）
     * @param path      请求路径
     */
    public record ApiError(LocalDateTime timestamp, int status, String code, String message, String path)
    {
    }
}
