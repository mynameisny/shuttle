package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 管理端首页入口：根路径与旧页面路径统一重定向到 SaaS 工作台。
 *
 * <p>静态资源由 Spring Boot 默认的 {@code static/} 映射提供，但 {@code GET /} 默认 404，
 * 直接分享根地址会打不开，因此这里做一次服务端重定向。
 * 用 {@code redirect:} 前缀而非 302 + Location 手写，避免拼出绝对地址。
 */
@Tag(name = "管理端入口")
@Controller
public class HomeController
{
    /**
     * 根路径 → 工作台。
     *
     * @return 重定向指令
     */
    @GetMapping("/")
    @Operation(summary = "首页重定向到工作台")
    public String index()
    {
        return "redirect:/dashboard.html";
    }
}
