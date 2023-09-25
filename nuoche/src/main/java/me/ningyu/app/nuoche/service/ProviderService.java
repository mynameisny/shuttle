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
import me.ningyu.app.nuoche.domain.User;
import me.ningyu.app.nuoche.domain.UserRepository;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
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
    private final UserRepository userRepository;
    public static final String NOT_FOUND = "通知器不存在";
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
        Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotfoundException(NOT_FOUND));

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
        Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotfoundException(NOT_FOUND));
        return entityToVO(provider);
    }

    @Transactional
    public ProviderVO enable(String id)
    {
        Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotfoundException(NOT_FOUND));
        provider.setEnabled(true);
        providerRepository.save(provider);
        return entityToVO(provider);
    }

    @Transactional
    public ProviderVO disable(String id)
    {
        Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotfoundException(NOT_FOUND));
        provider.setEnabled(false);
        providerRepository.save(provider);
        return entityToVO(provider);
    }

    @Transactional
    public void addProviderToUser(String id, String userCode)
    {
        Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotfoundException(NOT_FOUND));
        User user = userRepository.findByCode(userCode).orElseThrow(() -> new NotfoundException(NOT_FOUND));

        user.getProviders().add(provider);
        userRepository.save(user);
    }

    @Transactional
    public void removeProviderFromUser(String id, String userCode)
    {
        Provider provider = providerRepository.findById(id).orElseThrow(() -> new NotfoundException(NOT_FOUND));
        User user = userRepository.findByCode(userCode).orElseThrow(() -> new NotfoundException(NOT_FOUND));

        user.getProviders().removeIf(p -> p.getId().equals(provider.getId()));
        userRepository.save(user);
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

    public void ocr()
    {
        ITesseract instance = new Tesseract();

        // 设置语言库位置
        instance.setDatapath("src/main/resources/data");

        // 设置语言：chi_sim/eng
        String language = "chi_sim";
        instance.setLanguage(language);


    }
}
