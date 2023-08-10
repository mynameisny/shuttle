package me.ningyu.app.nuoche.controller;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.nuoche.common.dto.ProviderDTO;
import me.ningyu.app.nuoche.common.dto.UserDTO;
import me.ningyu.app.nuoche.common.dto.validation.ProviderAdd;
import me.ningyu.app.nuoche.common.dto.validation.UserAdd;
import me.ningyu.app.nuoche.common.vo.ProviderVO;
import me.ningyu.app.nuoche.common.vo.UserVO;
import me.ningyu.app.nuoche.controller.binder.UserSearchBinding;
import me.ningyu.app.nuoche.domain.Provider;
import me.ningyu.app.nuoche.domain.User;
import me.ningyu.app.nuoche.service.ProviderService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.groups.Default;
import java.net.URI;


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
        URI location = builder.replacePath("/providers/{code}").buildAndExpand(vo.getId()).toUri();
        return ResponseEntity.created(location).body(vo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id)
    {
        providerService.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String providerId, @RequestBody @Validated({Default.class}) ProviderDTO dto)
    {
        ProviderVO vo = providerService.update(providerId, dto);
        return ResponseEntity.ok(vo);
    }

    @GetMapping
    public ResponseEntity<?> list(@QuerydslPredicate(root = User.class, bindings = UserSearchBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<ProviderVO> content = providerService.list(predicate, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id)
    {
        Provider provider = providerService.get(id);
        ProviderVO vo = new ProviderVO();
        BeanUtils.copyProperties(provider, vo);
        return ResponseEntity.ok(vo);
    }
}
