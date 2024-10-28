package me.ningyu.app.easymonger.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.domain.auth.UserRepository;
import me.ningyu.app.easymonger.exception.DuplicateException;
import me.ningyu.app.easymonger.exception.InvalidTokenException;
import me.ningyu.app.easymonger.exception.NotFoundException;
import me.ningyu.app.easymonger.model.dto.UserAddDto;
import me.ningyu.app.easymonger.model.dto.UserRegisterDto;
import me.ningyu.app.easymonger.model.dto.UserUpdateDto;
import me.ningyu.app.easymonger.model.enums.UserStatus;
import me.ningyu.app.easymonger.model.mapstruct.UserMapper;
import me.ningyu.app.easymonger.model.vo.UserVo;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService
{
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    @Transactional
    public UserVo add(UserAddDto dto)
    {
        userRepository.findByCode(dto.getCode()).ifPresent(user -> {
            log.info("用户[{}]已存在：{}", dto.getCode(), user);
            throw new DuplicateException(String.format("用户[%s]已存在", dto.getCode()));
        });
        userRepository.findByEmail(dto.getEmail()).ifPresent(user -> {
            log.info("邮箱[{}]已存在：{}", dto.getEmail(), user);
            throw new DuplicateException(String.format("邮箱[%s]已存在", dto.getEmail()));
        });
        userRepository.findByMobile(dto.getEmail()).ifPresent(user -> {
            log.info("手机号码[{}]已存在：{}", dto.getEmail(), user);
            throw new DuplicateException(String.format("手机号码[%s]已存在", dto.getEmail()));
        });
        User user = UserMapper.INSTANCE.dtoToEntity(dto);
        return UserMapper.INSTANCE.entityToVo(user);
    }

    @Transactional
    public void delete(String userCode, boolean force)
    {
        User user = userRepository.findByCode(userCode).orElseThrow(() -> new NotFoundException("用户不存在"));
        userRepository.delete(user);
    }
    
    @Transactional
    public UserVo update(String code, UserUpdateDto dto)
    {
        User user = userRepository.findByCode(code).orElseThrow(() -> new NotFoundException("用户不存在"));
        BeanUtils.copyProperties(dto, user);
        userRepository.save(user);
        
        return UserMapper.INSTANCE.entityToVo(user);
    }
    
    public Page<UserVo> list(Predicate predicate, Pageable pageable)
    {
        Page<User> page = userRepository.findAll(predicate, pageable);
        return page.map(UserMapper.INSTANCE::entityToVo);
    }
    
    public UserVo get(String code)
    {
        User user = userRepository.findByCode(code).orElseThrow(() -> new NotFoundException("用户不存在"));
        return UserMapper.INSTANCE.entityToDetailVo(user);
    }

    @Transactional
    public UserVo register(UserRegisterDto dto)
    {
        User user = UserMapper.INSTANCE.dtoToEntity(dto);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        
        UserVo vo = UserMapper.INSTANCE.entityToVo(user);
        emailService.sendActivationEmail(vo.getEmail(), "");
        return vo;
    }

    @Transactional
    public UserVo activate(String userCode, String activationToken)
    {
        User user = userRepository.findByCode(userCode).orElseThrow(() -> new NotFoundException("用户不存在"));

        if (!isValidToken(user, activationToken))
        {
            throw new InvalidTokenException("无效的激活Token");
        }

        // 更新用户状态为已激活
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return UserMapper.INSTANCE.entityToVo(user);
    }

    private boolean isValidToken(User user, String activationToken)
    {
        return generateActivationCode(user.getId(), user.getEmail(), user.getCode(), user.getCreatedDate().toInstant(ZoneOffset.UTC).toEpochMilli()).equals(activationToken);
    }
    
    public String generateActivationCode(String id, String email, String username, long registrationTime)
    {
        try
        {
            // 创建哈希对象
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // 拼接用户信息
            String input = id + email + username + registrationTime;
            
            // 计算哈希值
            byte[] hashBytes = digest.digest(input.getBytes());
            
            // 使用 Apache Commons Codec 将字节数组转换为十六进制字符串
            return Hex.encodeHexString(hashBytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("激活码生成失败", e);
        }
    }
}
