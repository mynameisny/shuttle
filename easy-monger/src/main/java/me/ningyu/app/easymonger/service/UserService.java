package me.ningyu.app.easymonger.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.domain.auth.UserRepository;
import me.ningyu.app.easymonger.exception.NotFoundException;
import me.ningyu.app.easymonger.model.dto.UserAddDto;
import me.ningyu.app.easymonger.model.dto.UserUpdateDto;
import me.ningyu.app.easymonger.model.mapstruct.UserMapper;
import me.ningyu.app.easymonger.model.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService
{
    private final UserRepository userRepository;
    
    
    @Transactional
    public UserVo add(UserAddDto dto)
    {
        User user = UserMapper.INSTANCE.dtoToEntity(dto);
        return UserMapper.INSTANCE.entityToVo(user);
    }
    
    @Transactional
    public void delete(String userCode)
    {
        User user = userRepository.findByCode(userCode).orElseThrow(() -> new NotFoundException("用户不存在"));
        userRepository.delete(user);
    }
    
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
        User user = userRepository.findByCode(code).orElseThrow(() -> new NotFoundException(""));
        return UserMapper.INSTANCE.entityToVo(user);
    }
}
