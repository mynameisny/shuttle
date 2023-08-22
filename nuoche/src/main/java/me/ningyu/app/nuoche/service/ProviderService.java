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
        providerRepository.deleteById(id);
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

    public ProviderVO get(String id)
    {
        Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotfoundException("通知器不存在"));
        return entityToVO(provider);
    }

    @Transactional
    public ProviderVO enable(String id)
    {
        Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotfoundException("通知器不存在"));
        provider.setEnabled(true);
        providerRepository.save(provider);
        return entityToVO(provider);
    }

    @Transactional
    public ProviderVO disable(String id)
    {
        Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotfoundException("通知器不存在"));
        provider.setEnabled(false);
        providerRepository.save(provider);
        return entityToVO(provider);
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
