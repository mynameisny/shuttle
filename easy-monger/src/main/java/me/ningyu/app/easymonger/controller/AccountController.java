package me.ningyu.app.easymonger.controller;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.coupon.Account;
import me.ningyu.app.easymonger.domain.coupon.ParseTemplate;
import me.ningyu.app.easymonger.domain.coupon.QAccount;
import me.ningyu.app.easymonger.domain.coupon.QParseTemplate;
import me.ningyu.app.easymonger.model.dto.AccountDto;
import me.ningyu.app.easymonger.model.dto.ParseTemplateDto;
import me.ningyu.app.easymonger.model.vo.AccountVo;
import me.ningyu.app.easymonger.model.vo.ParseTemplateVo;
import me.ningyu.app.easymonger.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController
{
    private final AccountService accountService;


    @PostMapping
    public ResponseEntity<AccountVo> add(@Validated @RequestBody AccountDto dto)
    {
        AccountVo vo = accountService.add(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", UriComponentsBuilder.fromUriString("/accounts/{accountId}").buildAndExpand(vo.getId()).toUriString());

        return new ResponseEntity<>(vo, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id)
    {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountVo> update(@PathVariable String id, @RequestBody AccountDto dto)
    {
        AccountVo vo = accountService.update(id, dto);
        return ResponseEntity.ok(vo);
    }

    @GetMapping
    public Page<AccountVo> list(@QuerydslPredicate(root = Account.class, bindings = AccountSearchBinding.class) Predicate predicate, @PageableDefault(size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "modifiedDate", direction = Sort.Direction.DESC), @SortDefault(sort = "id", direction = Sort.Direction.ASC)}) Pageable pageable)
    {
        return accountService.list(predicate, pageable);
    }

    @GetMapping("/{id}")
    public AccountVo get(@PathVariable String id)
    {
        return accountService.get(id);
    }

    @PostMapping("/{accountId}/parse-templates")
    public ResponseEntity<ParseTemplateVo> addParseTemplate(@PathVariable String accountId, @Validated @RequestBody ParseTemplateDto dto)
    {
        ParseTemplateVo vo = accountService.addParseTemplate(accountId, dto);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", UriComponentsBuilder.fromUriString("/accounts/{accountId}/parse-templates/{id}").buildAndExpand(accountId, vo.getId()).toUriString());

        return new ResponseEntity<>(vo, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{accountId}/parse-templates")
    public ResponseEntity<Void> deleteParseTemplates(@PathVariable String accountId, @RequestBody(required = false) List<String> parseTemplateIds)
    {
        accountService.deleteParseTemplates(accountId, parseTemplateIds);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{accountId}/parse-templates")
    public Page<ParseTemplateVo> listParseTemplates(@PathVariable String accountId, @QuerydslPredicate(root = ParseTemplate.class, bindings = ParseTemplateSearchBinding.class) Predicate predicate, @PageableDefault(size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "modifiedDate", direction = Sort.Direction.DESC), @SortDefault(sort = "id", direction = Sort.Direction.ASC)}) Pageable pageable)
    {
        return accountService.listParseTemplates(accountId, predicate, pageable);
    }

    private static class AccountSearchBinding implements QuerydslBinderCustomizer<QAccount>
    {
        @Override
        public void customize(QuerydslBindings bindings, QAccount root)
        {
            bindings.bind(QAccount.account.id).first(StringExpression::eq);
            bindings.bind(root.id).first(StringExpression::eq);
            bindings.bind(root.user.id).first(StringExpression::eq);
            bindings.bind(root.platform).first(EnumExpression::eq);
            bindings.bind(root.mobile).first(StringExpression::eq);
            bindings.bind(root.email).first(StringExpression::eq);
        }
    }

    private static class ParseTemplateSearchBinding implements QuerydslBinderCustomizer<QParseTemplate>
    {
        @Override
        public void customize(QuerydslBindings bindings, QParseTemplate root)
        {
            bindings.bind(QParseTemplate.parseTemplate.id).first(StringExpression::eq);
            bindings.bind(QParseTemplate.parseTemplate.account.id).first(StringExpression::eq);
            bindings.bind(root.id).first(StringExpression::eq);
            bindings.bind(root.name).all(this::nameIn);
            bindings.bind(root.deviceName).all((path, value) ->
            {
                if (value.size() == 1)
                {
                    return Optional.of(path.eq(value.iterator().next()));
                }
                else
                {
                    return Optional.of(path.in(value));
                }
            });
            bindings.bind(root.enabled).first(BooleanExpression::eq);
            bindings.bind(root.remark).first(StringExpression::contains);
        }

        public Optional<Predicate> nameIn(StringPath path, Collection<? extends String> value)
        {
            if (value.size() == 1)
            {
                return Optional.of(path.eq(value.iterator().next()));
            }
            else
            {
                return Optional.of(path.in(value));
            }
        }
    }
}
