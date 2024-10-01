package me.ningyu.app.easymonger.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils
{
    public static String getClientIP(HttpServletRequest request)
    {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr()))
        {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
