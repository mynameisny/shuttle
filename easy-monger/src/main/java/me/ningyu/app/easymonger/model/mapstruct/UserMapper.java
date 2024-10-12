package me.ningyu.app.easymonger.model.mapstruct;

import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.model.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper
{
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserVo entityToVo(User user);
}
