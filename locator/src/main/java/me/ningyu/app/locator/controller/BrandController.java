package me.ningyu.app.locator.controller;

import io.swagger.annotations.Api;
import me.ningyu.app.locator.service.BrandService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "品牌管理接口")
@RestController
@RequestMapping("/brands")
public class BrandController
{
    private final BrandService brandService;


    public BrandController(BrandService brandService)
    {
        this.brandService = brandService;
    }
}
