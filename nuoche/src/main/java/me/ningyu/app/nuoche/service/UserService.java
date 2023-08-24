package me.ningyu.app.nuoche.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.nuoche.common.dto.ProviderDTO;
import me.ningyu.app.nuoche.common.dto.UserDTO;
import me.ningyu.app.nuoche.common.exception.DuplicateException;
import me.ningyu.app.nuoche.common.exception.NotfoundException;
import me.ningyu.app.nuoche.common.vo.ProviderVO;
import me.ningyu.app.nuoche.common.vo.UserVO;
import me.ningyu.app.nuoche.domain.Provider;
import me.ningyu.app.nuoche.domain.ProviderRepository;
import me.ningyu.app.nuoche.domain.User;
import me.ningyu.app.nuoche.domain.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService
{
    private final UserRepository userRepository;

    private final ProviderRepository providerRepository;


    @Transactional
    public UserVO add(UserDTO dto)
    {
        Optional<User> userOptional = userRepository.findByCode(dto.getCode());
        if (userOptional.isPresent())
        {
            throw new DuplicateException("用户已存在");
        }

        User user = User.builder()
                        .code(dto.getCode())
                        .name(dto.getName())
                        .code(Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()))
                        .phones(dto.getPhones())
                        .emails(dto.getEmails())
                        .build();
        userRepository.save(user);

        return entityToVO(user);
    }

    @Transactional
    public void delete(String userCode)
    {
        findUserByCode(userCode);
        userRepository.deleteByCode(userCode);
    }

    @Transactional
    public UserVO update(String userCode, UserDTO dto)
    {
        User user = findUserByCode(userCode);

        BeanUtils.copyProperties(dto, user, "code");
        userRepository.save(user);

        return entityToVO(user);
    }

    @Transactional
    public UserVO addProviderToUser(String userCode, List<ProviderDTO> dto)
    {
        User user = findUserByCode(userCode);
        Set<Provider> providers = user.getProviders();

        for (ProviderDTO providerDTO : dto)
        {
            Provider provider = Provider.builder()
                                        .vendor(providerDTO.getVendor())
                                        .description(providerDTO.getDescription())
                                        .enabled(providerDTO.getEnabled())
                                        .url(providerDTO.getUrl())
                                        .pushKey(providerDTO.getPushKey())
                                        .weight(providerDTO.getWeight())
                                        .build();

            if (providers.stream().anyMatch(item -> item.getVendor() == provider.getVendor() && item.getUrl().equals(provider.getUrl()) && item.getPushKey().equals(provider.getPushKey())))
            {
                throw new DuplicateException("通知器已存在");
            }
            else
            {
                providers.add(provider);
            }
        }

        user.setProviders(providers);
        userRepository.save(user);

        return entityToVO(user);
    }

    @Transactional
    public UserVO removeProviderFromUser(String userCode, List<String> providerIds)
    {
        User user = findUserByCode(userCode);

        providerIds.forEach(providerId ->
        {
            Provider provider = findProviderById(providerId);
            log.debug("通知器将被从用户[{}]移除", provider);
        });

        user.setProviders(new HashSet<>());
        userRepository.save(user);

        return entityToVO(user);
    }

    @Transactional
    public UserVO toggleProvider(String userCode, List<String> providerIds)
    {
        User user = findUserByCode(userCode);

        for (Provider provider : user.getProviders())
        {
            if (providerIds.contains(provider.getId()))
            {
                provider.setEnabled(!provider.isEnabled());
            }
        }

        userRepository.save(user);

        return entityToVO(user);
    }

    public Page<UserVO> search(Predicate predicate, Pageable pageable)
    {
        Page<User> users = userRepository.findAll(predicate, pageable);
        return users.map(user -> UserVO.builder()
                                       .code(user.getCode())
                                       .name(user.getName())
                                       .key(user.getCode())
                                       .phones(user.getPhones())
                                       .emails(user.getEmails())
                                       .providers(providerEntityToVO(user.getProviders()))
                                       .build());
    }

    public UserVO getByCode(String userCode)
    {
        User user = findUserByCode(userCode);

        return entityToVO(user);
    }

    public static UserVO entityToVO(User user)
    {
        if (user == null)
        {
            return null;
        }

        UserVO userVO = UserVO.builder()
                              .code(user.getCode())
                              .name(user.getName())
                              .key(user.getCode())
                              .phones(user.getPhones())
                              .emails(user.getEmails())
                              .build();

        Set<Provider> providers = user.getProviders();
        if (providers != null && !providers.isEmpty())
        {
            Set<ProviderVO> providerVOs = providerEntityToVO(providers);
            userVO.setProviders(providerVOs);
        }
        else
        {
            userVO.setProviders(new HashSet<>());
        }

        return userVO;
    }

    public static Set<ProviderVO> providerEntityToVO(Set<Provider> providers)
    {
        Set<ProviderVO> providerVOs = new HashSet<>();
        for (Provider provider : providers)
        {
            providerVOs.add(ProviderVO.builder()
                                      .id(provider.getId())
                                      .vendor(provider.getVendor())
                                      .description(provider.getDescription())
                                      .url(provider.getUrl())
                                      .pushKey(provider.getPushKey())
                                      .weight(provider.getWeight())
                                      .enabled(provider.isEnabled())
                                      .build());
        }
        return providerVOs;
    }

    protected User findUserByCode(String code)
    {
        Optional<User> userOptional = userRepository.findByCode(code);
        if (!userOptional.isPresent())
        {
            throw new NotfoundException("用户不存在");
        }

        return userOptional.get();
    }

    protected Provider findProviderById(String id)
    {
        Optional<Provider> providerOptional = providerRepository.findById(id);
        if (!providerOptional.isPresent())
        {
            throw new NotfoundException("通知器不存在");
        }

        return providerOptional.get();
    }
}
