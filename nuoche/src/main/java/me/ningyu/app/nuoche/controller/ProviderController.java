package me.ningyu.app.nuoche.controller;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.nuoche.common.dto.ProviderDTO;
import me.ningyu.app.nuoche.common.dto.validation.ProviderAdd;
import me.ningyu.app.nuoche.common.vo.ProviderVO;
import me.ningyu.app.nuoche.common.vo.UserVO;
import me.ningyu.app.nuoche.controller.binder.ProviderSearchBinding;
import me.ningyu.app.nuoche.controller.binder.UserSearchBinding;
import me.ningyu.app.nuoche.domain.Provider;
import me.ningyu.app.nuoche.domain.User;
import me.ningyu.app.nuoche.service.ProviderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.groups.Default;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/providers")
@RequiredArgsConstructor
@Slf4j
public class ProviderController
{
    private final ProviderService providerService;


    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated({ProviderAdd.class, Default.class}) ProviderDTO dto, UriComponentsBuilder builder)
    {
        ProviderVO vo = providerService.add(dto);
        URI location = builder.replacePath("/providers/{id}").buildAndExpand(vo.getId()).toUri();
        return ResponseEntity.created(location).body(vo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id)
    {
        providerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody @Validated({Default.class}) ProviderDTO dto)
    {
        ProviderVO vo = providerService.update(id, dto);
        return ResponseEntity.ok(vo);
    }

    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = Provider.class, bindings = ProviderSearchBinding.class) Predicate predicate, @PageableDefault(sort = "modifiedAt") Pageable pageable)
    {
        Page<ProviderVO> content = providerService.list(predicate, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        ProviderVO vo = providerService.get(id);
        return ResponseEntity.ok(vo);
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<?> enable(@PathVariable("id") String id)
    {
        ProviderVO vo = providerService.enable(id);
        return ResponseEntity.ok(vo);
    }

    @PostMapping("/{id}/disable")
    public ResponseEntity<?> disable(@PathVariable("id") String id)
    {
        ProviderVO vo = providerService.disable(id);
        return ResponseEntity.ok(vo);
    }

    @PutMapping("/{id}/users/{userCode}")
    public ResponseEntity<?> addProviderToUser(@PathVariable("id") String id, @PathVariable("userCode") String userCode)
    {
        return ResponseEntity.ok().build();
    }
}
