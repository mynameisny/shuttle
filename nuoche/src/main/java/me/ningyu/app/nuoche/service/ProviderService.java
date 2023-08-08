package me.ningyu.app.nuoche.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.nuoche.common.BeanCopyUtils;
import me.ningyu.app.nuoche.common.dto.ProviderDTO;
import me.ningyu.app.nuoche.common.exception.NotfoundException;
import me.ningyu.app.nuoche.common.vo.ProviderVO;
import me.ningyu.app.nuoche.domain.Provider;
import me.ningyu.app.nuoche.domain.ProviderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProviderService
{
    private final ProviderRepository providerRepository;


    @Transactional
    public ProviderVO add(ProviderDTO dto)
    {
        Provider provider = new Provider();
        BeanUtils.copyProperties(dto, provider, BeanCopyUtils.getNullPropertyNames(dto));
        providerRepository.save(provider);

        return entityToVO(provider);
    }

    @Transactional
    public void deleteById(String id)
    {
        providerRepository.deleteById(id);
    }

    @Transactional
    public ProviderVO update(String providerId, ProviderDTO dto)
    {
        Provider provider = get(providerId);

        BeanUtils.copyProperties(dto, provider, BeanCopyUtils.getNullPropertyNames(dto));
        providerRepository.save(provider);

        return entityToVO(provider);
    }

    @Transactional
    public Page<ProviderVO> list(Predicate predicate, Pageable pageable)
    {
        return null;
    }

    private Provider get(String id)
    {
        Optional<Provider> providerOptional = providerRepository.findById(id);

        if (!providerOptional.isPresent())
        {
            throw new NotfoundException("通知器不存在");
        }

        return providerOptional.get();
    }

    public static ProviderVO entityToVO(Provider provider)
    {
        return ProviderVO.builder()
                         .id(provider.getId())
                         .vendor(provider.getVendor())
                         .description(provider.getDescription())
                         .url(provider.getUrl())
                         .pushKey(provider.getPushKey())
                         .weight(provider.getWeight())
                         .enabled(provider.isEnabled())
                         .build();
    }
}
