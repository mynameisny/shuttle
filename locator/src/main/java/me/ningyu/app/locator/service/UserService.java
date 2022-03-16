package me.ningyu.app.locator.service;


import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.exception.NotfoundException;
import me.ningyu.app.locator.common.vo.UserDto;
import me.ningyu.app.locator.domain.user.entity.User;
import me.ningyu.app.locator.domain.user.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class UserService
{
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }


    public User add(UserDto dto)
    {
        User entity = new User();
        BeanUtils.copyProperties(dto, entity);
        return userRepository.save(entity);
    }

    @Transactional
    public void remove(String id)
    {
        Optional.of(userRepository.findById(id)).get().orElseThrow(() -> new NotfoundException(String.format("用户%s不存在", id)));
        userRepository.deleteById(id);
    }

    public User update(String id, UserDto dto)
    {
        User user = Optional.of(userRepository.findById(id)).get().orElseThrow(() -> new RuntimeException(String.format("用户%s不存在", id)));
        BeanUtils.copyProperties(user, dto);
        return userRepository.save(user);
    }

    public UserDto get(String id)
    {
        User user = Optional.of(userRepository.findById(id)).get().orElseThrow(() -> new RuntimeException(String.format("用户%s不存在", id)));
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    public Page<User> list(Predicate predicate, Pageable pageable)
    {
        return userRepository.findAll(predicate, pageable);
    }
}
