package me.ningyu.app.locator.service;


import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.vo.UserDto;
import me.ningyu.app.locator.domain.user.entity.User;
import me.ningyu.app.locator.domain.user.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
}
