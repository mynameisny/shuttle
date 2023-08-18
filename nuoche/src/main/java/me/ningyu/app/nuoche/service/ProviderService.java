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
    public void delete(String id)
    {
        Provider provider = get(id);
        providerRepository.delete(provider);
    }

    @Transactional
    public ProviderVO update(String id, ProviderDTO dto)
    {
        Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotfoundException("通知器不存在"));

        BeanUtils.copyProperties(dto, provider, BeanCopyUtils.getNullPropertyNames(dto));
        providerRepository.save(provider);

        return entityToVO(provider);
    }

    @Transactional
    public Page<ProviderVO> list(Predicate predicate, Pageable pageable)
    {
        Page<Provider> page = providerRepository.findAll(predicate, pageable);
        return page.map(this::entityToVO);
    }

    public Provider get(String id)
    {
        Optional<Provider> optional = providerRepository.findById(id);

        if (!optional.isPresent())
        {
            throw new NotfoundException("通知器不存在");
        }

        return optional.get();
    }

    public ProviderVO getById(String id)
    {
        Optional<Provider> optional = providerRepository.findById(id);

        if (!optional.isPresent())
        {
            throw new NotfoundException("通知器不存在");
        }

        return entityToVO(optional.get());
    }

    public ProviderVO entityToVO(Provider provider)
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
