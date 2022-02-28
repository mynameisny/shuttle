package me.ningyu.app.locator.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.RecordDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.record.entity.Record;
import me.ningyu.app.locator.service.RecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Api(tags = "记录管理接口")
@RestController
@RequestMapping("/records")
@Slf4j
public class RecordController
{
    private final RecordService recordService;

    public RecordController(RecordService recordService)
    {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated RecordDto dto, UriComponentsBuilder builder)
    {
        Record saved = recordService.add(dto);
        URI location = builder.replacePath("/records/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }
}
