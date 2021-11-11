package me.ningyu.app.locator.controller;

import me.ningyu.app.locator.vo.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/points")
public class PointController
{
    @PostMapping
    public ResponseEntity<?> add(@RequestBody Point point)
    {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id)
    {
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Point point)
    {
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePart (@PathVariable String id, @RequestBody Point point)
    {
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> list()
    {
        List<Object> result = new ArrayList<>();
        return ResponseEntity.ok(result);
    }
}
