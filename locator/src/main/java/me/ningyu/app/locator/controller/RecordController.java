package me.ningyu.app.locator.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.BrandDto;
import me.ningyu.app.locator.common.vo.RecordDto;
import me.ningyu.app.locator.common.vo.StationDto;
import me.ningyu.app.locator.domain.map.entity.QStation;
import me.ningyu.app.locator.domain.map.entity.Station;
import me.ningyu.app.locator.domain.record.entity.QRecord;
import me.ningyu.app.locator.domain.record.entity.Record;
import me.ningyu.app.locator.domain.vehicle.entity.Brand;
import me.ningyu.app.locator.service.RecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable String id)
    {
        recordService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Validated @RequestBody BrandDto dto)
    {
        Record record = recordService.update(id, dto);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        RecordDto recordDto = recordService.get(id);
        return ResponseEntity.ok(recordDto);
    }

    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = Record.class, bindings = RecordController.RecordBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<Record> list = recordService.list(predicate, pageable);
        return ResponseEntity.ok(list);
    }

    private static class RecordBinding implements QuerydslBinderCustomizer<QRecord>
    {
        @Override
        public void customize(QuerydslBindings bindings, QRecord root)
        {
            bindings.bind(root.name).first(StringExpression::containsIgnoreCase);
        }
    }
}
