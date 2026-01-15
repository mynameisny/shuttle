package me.ningyu.app.shuttle.facade.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "线路管理")
@SecurityRequirement(name = "TONY")
@RestController
@Slf4j
@RequiredArgsConstructor
public class RouteController
{
}
