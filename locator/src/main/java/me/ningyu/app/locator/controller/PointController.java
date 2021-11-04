package me.ningyu.app.locator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
public class PointController
{
    @PostMapping
    public ResponseEntity add()
    {
        return ResponseEntity.ok().build();
    }
}
