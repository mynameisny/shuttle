package me.ningyu.app.nuoche.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.nuoche.common.dto.ProviderDTO;
import me.ningyu.app.nuoche.common.dto.UserDTO;
import me.ningyu.app.nuoche.common.dto.validation.ProviderAdd;
import me.ningyu.app.nuoche.common.dto.validation.UserAdd;
import me.ningyu.app.nuoche.common.vo.ProviderVO;
import me.ningyu.app.nuoche.common.vo.UserVO;
import me.ningyu.app.nuoche.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String providerId, @RequestBody @Validated({Default.class}) ProviderDTO dto)
    {
        ProviderVO vo = providerService.update(providerId, dto);
        return ResponseEntity.ok(vo);
    }

}
