package me.ningyu.app.locator.controller;

import me.ningyu.app.locator.vo.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
public class PointController
{
    @PostMapping
    public ResponseEntity add(@RequestBody Point point)
    {
        return ResponseEntity.ok().build();
    }
}
