package me.ningyu.app.shuttle.iam.application;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.iam.domain.IamRole;
import me.ningyu.app.shuttle.iam.domain.IamRoleRepository;
import me.ningyu.app.shuttle.model.iam.CreateRoleRequest;
import me.ningyu.app.shuttle.model.iam.UpdateRoleRequest;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色管理服务（可复用）：角色元数据 + 角色权限策略（jCasbin {@code p} 策略）。
 */
@Service
@RequiredArgsConstructor
public class RoleService
{
    private final IamRoleRepository roleRepository;
    private final Enforcer enforcer;

    public List<IamRole> list()
    {
        return roleRepository.findAll();
    }

    @Transactional
    public IamRole create(CreateRoleRequest request)
    {
        if (roleRepository.existsByCode(request.code()))
        {
            throw new IllegalArgumentException("角色编码已存在: " + request.code());
        }
        return roleRepository.save(IamRole.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .build());
    }

    @Transactional
    public IamRole update(Long id, UpdateRoleRequest request)
    {
        IamRole role = get(id);
        role.setName(request.name());
        role.setDescription(request.description());
        return roleRepository.save(role);
    }

    @Transactional
    public void delete(Long id)
    {
        IamRole role = get(id);
        enforcer.deleteRole(role.getCode());
        roleRepository.delete(role);
    }

    /** 角色的权限策略行（p 策略）。 */
    public List<List<String>> permissionsOf(String code)
    {
        return enforcer.getPermissionsForUser(code);
    }

    @Transactional
    public void grant(String code, String resource, String action)
    {
        enforcer.addPermissionForUser(code, resource, action);
    }

    @Transactional
    public void revoke(String code, String resource, String action)
    {
        enforcer.deletePermissionForUser(code, resource, action);
    }

    private IamRole get(Long id)
    {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: id=" + id));
    }
}
