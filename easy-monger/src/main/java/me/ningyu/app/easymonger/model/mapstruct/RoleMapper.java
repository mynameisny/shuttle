package me.ningyu.app.easymonger.model.mapstruct;

import me.ningyu.app.easymonger.domain.auth.Role;
import me.ningyu.app.easymonger.domain.auth.User;
import me.ningyu.app.easymonger.model.dto.UserAddDto;
import me.ningyu.app.easymonger.model.dto.UserRegisterDto;
import me.ningyu.app.easymonger.model.vo.RoleVo;
import me.ningyu.app.easymonger.model.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper
{
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);
    
    RoleVo entityToVo(Role role);
    
    RoleVo entityToDetailVo(Role role);
    
    RoleVo dtoToEntity(UserAddDto dto);
    
    RoleVo dtoToEntity(UserRegisterDto dto);
}
