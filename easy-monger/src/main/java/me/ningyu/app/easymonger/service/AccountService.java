package me.ningyu.app.easymonger.service;

import com.github.dozermapper.core.Mapper;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.domain.auth.UserRepository;
import me.ningyu.app.easymonger.domain.coupon.*;
import me.ningyu.app.easymonger.exception.DuplicateException;
import me.ningyu.app.easymonger.exception.NotFoundException;
import me.ningyu.app.easymonger.model.dto.AccountDto;
import me.ningyu.app.easymonger.model.dto.ParseTemplateDto;
import me.ningyu.app.easymonger.model.enums.Field;
import me.ningyu.app.easymonger.model.vo.AccountVo;
import me.ningyu.app.easymonger.model.vo.ParseTemplateVo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService
{
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final ParseTemplateRepository parseTemplateRepository;
    private final ParseTemplateCoordinateRepository parseTemplateCoordinateRepository;
    private final Mapper mapper;

    @Transactional
    public AccountVo add(AccountDto dto)
    {
        if (accountRepository.exists(QAccount.account.platform.eq(dto.getPlatform()).and(QAccount.account.mobile.eq(dto.getMobile()))))
        {
            throw new DuplicateException("账户已存在");
        }

        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new NotFoundException("用户[{}]不存在", dto.getUserId()));

        Account entity = dtoToEntity(dto);
        entity.setUser(user);

        accountRepository.save(entity);

        return entityToVo(entity);
    }

    @Transactional
    public void delete(String id)
    {
        if (!accountRepository.exists(QAccount.account.id.eq(id)))
        {
            throw new NotFoundException("账户不存在");
        }

        accountRepository.deleteById(id);
    }

    @Transactional
    public AccountVo update(String id, AccountDto dto)
    {
        Account entity = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("账户不存在"));

        accountRepository.save(entity);

        return entityToVo(entity);
    }

    public Page<AccountVo> list(Predicate predicate, Pageable pageable)
    {
        String userCode = SecurityContextHolder.getContext().getAuthentication().getName();
        predicate = QAccount.account.user.code.eq(userCode).and(predicate);

        Page<Account> users = accountRepository.findAll(predicate, pageable);

        return users.map(this::entityToVo);
    }

    public AccountVo get(String id)
    {
        Account entity = accountRepository.findById(id).orElseThrow(() -> new NotFoundException("账户不存在"));
        return entityToVo(entity);
    }

    private AccountVo entityToVo(Account entity)
    {
        AccountVo vo = new AccountVo();
        mapper.map(entity, vo);
        vo.setPlatformText(entity.getPlatform().getName());
        return vo;
    }

    private Account dtoToEntity(AccountDto dto)
    {
        Account entity = new Account();
        mapper.map(dto, entity);
        return entity;
    }

    @Transactional
    public ParseTemplateVo addParseTemplate(String accountId, ParseTemplateDto dto)
    {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("找不到账号"));

        checkParseTemplateParam(account, dto);

        ParseTemplate parseTemplate = dtoToEntity(dto);
        parseTemplate.setAccount(account);
        parseTemplateRepository.save(parseTemplate);

        for (Coordinate coordinate : dto.getCoordinates())
        {
            ParseTemplateCoordinate parseTemplateCoordinate = new ParseTemplateCoordinate();
            parseTemplateCoordinate.setField(coordinate.getField());
            parseTemplateCoordinate.setX(coordinate.getX());
            parseTemplateCoordinate.setY(coordinate.getY());
            parseTemplateCoordinate.setWidth(coordinate.getWidth());
            parseTemplateCoordinate.setHeight(coordinate.getHeight());
            parseTemplateCoordinate.setParseTemplate(parseTemplate);
            parseTemplateCoordinateRepository.save(parseTemplateCoordinate);
        }

        account.getParseTemplates().add(parseTemplate);
        accountRepository.save(account);

        return entityToVo(parseTemplate);
    }

    @Transactional
    public void deleteParseTemplates(String accountId, List<String> parseTemplateIds)
    {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("找不到账号"));

        Iterable<ParseTemplate> parseTemplates;
        if (ObjectUtils.isEmpty(parseTemplateIds))
        {
            parseTemplates = parseTemplateRepository.findAll(QParseTemplate.parseTemplate.account.id.eq(account.getId()));
        }
        else
        {
            parseTemplates = parseTemplateRepository.findAll(QParseTemplate.parseTemplate.account.id.eq(account.getId()).and(QParseTemplate.parseTemplate.id.in(parseTemplateIds)));
        }

        parseTemplateRepository.deleteAll(parseTemplates);

        accountRepository.save(account);
    }

    public Page<ParseTemplateVo> listParseTemplates(String accountId, Predicate predicate, Pageable pageable)
    {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("找不到账号"));

        predicate = QParseTemplate.parseTemplate.account.id.eq(account.getId()).and(predicate);

        Page<ParseTemplate> parseTemplates = parseTemplateRepository.findAll(predicate, pageable);

        return parseTemplates.map(this::entityToVo);
    }

    private ParseTemplate dtoToEntity(ParseTemplateDto dto)
    {
        ParseTemplate entity = new ParseTemplate();
        mapper.map(dto, entity);
        return entity;
    }

    public ParseTemplateVo entityToVo(ParseTemplate entity)
    {
        ParseTemplateVo vo = new ParseTemplateVo();
        mapper.map(entity, vo);
        vo.setAccountId(entity.getAccount().getId());
        return vo;
    }

    private void checkParseTemplateParam(Account account, ParseTemplateDto dto)
    {
        if (dto.isEnabled())
        {
            account.getParseTemplates().stream().filter(ParseTemplate::isEnabled).findFirst().ifPresent(parseTemplate -> {
                throw new DuplicateException(String.format("只能有一个被启用的解析模板，当前被启用的是：%s(%s)", parseTemplate.getName(), parseTemplate.getId()));
            });
        }

        if (parseTemplateCoordinateRepository.exists(QParseTemplate.parseTemplate.account.id.eq(account.getId()).and(QParseTemplate.parseTemplate.name.eq(dto.getName()))))
        {
            throw new DuplicateException("解析模板名称已存在");
        }

        List<Field> fields = dto.getCoordinates().stream().map(Coordinate::getField).collect(Collectors.toList());
        if (fields.stream().distinct().count() != fields.size())
        {
            throw new DuplicateException("相同的Field只能存在一个");
        }

        if (dto.getCoordinates().stream().distinct().count() != dto.getCoordinates().size())
        {
            throw new DuplicateException("相同的坐标只能存在一个");
        }
    }
}
