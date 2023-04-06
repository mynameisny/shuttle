package me.ningyu.app.nuoche.controller;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.nuoche.common.dto.ProviderDTO;
import me.ningyu.app.nuoche.common.dto.UserDTO;
import me.ningyu.app.nuoche.controller.binder.UserSearchBinding;
import me.ningyu.app.nuoche.common.dto.validation.UserAdd;
import me.ningyu.app.nuoche.common.dto.validation.UserUpdate;
import me.ningyu.app.nuoche.common.vo.UserVO;
import me.ningyu.app.nuoche.domain.User;
import me.ningyu.app.nuoche.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.groups.Default;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController
{
    private final UserService userService;


    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated({UserAdd.class, Default.class}) UserDTO dto, UriComponentsBuilder builder)
    {
        UserVO vo = userService.add(dto);
        URI location = builder.replacePath("/users/{code}").buildAndExpand(vo.getCode()).toUri();
        return ResponseEntity.created(location).body(vo);
    }

    @DeleteMapping("/{userCode}")
    public ResponseEntity<?> delete(@PathVariable("userCode") String userCode)
    {
        userService.delete(userCode);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userCode}")
    public ResponseEntity<?> update(@PathVariable("userCode") String userCode, @RequestBody @Validated({UserUpdate.class, Default.class}) UserDTO dto)
    {
        UserVO vo = userService.update(userCode, dto);
        return ResponseEntity.ok(vo);
    }

    @GetMapping
    public ResponseEntity<?> search(@QuerydslPredicate(root = User.class, bindings = UserSearchBinding.class) Predicate predicate, Pageable pageable)
    {
        Page<UserVO> content = userService.search(predicate, pageable);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/{userCode}")
    public ResponseEntity<?> get(@PathVariable("userCode") String userCode)
    {
        UserVO userVO = userService.getByCode(userCode);
        return ResponseEntity.ok(userVO);
    }

    @PutMapping("/{userCode}/providers")
    public ResponseEntity<?> addProviderToUser(@PathVariable("userCode") String userCode, @RequestBody @Validated List<ProviderDTO> dto)
    {
        UserVO vo = userService.addProviderToUser(userCode, dto);
        return ResponseEntity.ok(vo);
    }

    @DeleteMapping("/{userCode}/providers")
    public ResponseEntity<?> removeProviderFromUser(@PathVariable("userCode") String userCode, @RequestParam("id") List<String> providerIds)
    {
        UserVO vo = userService.removeProviderFromUser(userCode, providerIds);
        return ResponseEntity.ok(vo);
    }

    @PostMapping("/{userCode}/providers/toggle")
    public ResponseEntity<?> toggleProvider(@PathVariable("userCode") String userCode, @RequestParam("id") List<String> providerIds)
    {
        UserVO vo = userService.toggleProvider(userCode, providerIds);
        return ResponseEntity.ok(vo);
    }
}
