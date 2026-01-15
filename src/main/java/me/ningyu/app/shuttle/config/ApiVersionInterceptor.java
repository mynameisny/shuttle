package me.ningyu.app.shuttle.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class ApiVersionInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        String version = request.getHeader("X-API-Version");

        // 允许的版本（可配置到 application.yml）
        if (version == null || !version.equals("v1"))
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setHeader("Content-Type", "application/json");
            try
            {
                response.getWriter().write("{\"error\":\"Invalid API version. Use X-API-Version: v1\"}");
            }
            catch (Exception exception)
            {
                log.error(exception.getMessage(), exception);
            }
            return false;
        }

        return true;
    }
}
