package me.ningyu.app.easymonger.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.domain.auth.UserRepository;
import me.ningyu.app.easymonger.exception.NotFoundException;
import me.ningyu.app.easymonger.model.dto.UserDto;
import me.ningyu.app.easymonger.model.mapstruct.UserMapper;
import me.ningyu.app.easymonger.model.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService
{
    private final UserRepository userRepository;
    
    
    @Transactional
    public UserVo add(UserDto dto, Predicate predicate)
    {
        List<User> all = userRepository.findAll(predicate);
        for (User user : all)
        {
            System.out.println("###" + user);
        }
        
        User user = userRepository.findByCode(dto.getCode()).orElseThrow(() -> new NotFoundException("用户不存在"));
        
        return UserMapper.INSTANCE.entityToVo(user);
    }
}
