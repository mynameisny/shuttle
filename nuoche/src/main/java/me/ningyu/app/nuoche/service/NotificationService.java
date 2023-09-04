package me.ningyu.app.nuoche.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.nuoche.common.dto.NotificationDTO;
import me.ningyu.app.nuoche.common.enums.ProviderVendor;
import me.ningyu.app.nuoche.common.exception.NotfoundException;
import me.ningyu.app.nuoche.domain.Provider;
import me.ningyu.app.nuoche.domain.User;
import me.ningyu.app.nuoche.domain.UserRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService
{
    private final UserRepository userRepository;

    private final BarkService barkService;

    private final NtfyService ntfyService;

    public static final String DEFAULT_TITLE = "请您挪车";

    public static final String DEFAULT_CONTENT = "尊敬的车主，您的爱车已防碍到他人，请尽快挪车。";


    public String send(NotificationDTO dto)
    {
        User user = userRepository.findByCode(dto.getUserCode()).orElseThrow(() -> new NotfoundException(String.format("找不到用户：%s", dto.getUserCode())));
        String userPhone = dto.getUserPhone();
        String title = StringUtils.isNotBlank(dto.getTitle()) ? dto.getTitle() : DEFAULT_TITLE;
        String message = StringUtils.isNotBlank(dto.getMessage()) ? dto.getMessage() : DEFAULT_CONTENT;

        Set<Provider> providers = user.getProviders();
        if (ObjectUtils.isEmpty(providers))
        {
            log.error("用户[{}]未配置通知器", user.getName());
            throw new RuntimeException("用户未配置通知器");
        }

        Map<ProviderVendor, List<Provider>> vendorMap = providers.stream().collect(Collectors.groupingBy(Provider::getVendor));
        for (Map.Entry<ProviderVendor, List<Provider>> entry : vendorMap.entrySet())
        {
            ProviderVendor vendorName = entry.getKey();
            List<Provider> vendorList = entry.getValue().stream().filter(Provider::isEnabled).sorted(Comparator.comparing(Provider::getWeight, Comparator.reverseOrder())).collect(Collectors.toList());

            if (vendorName == ProviderVendor.BARK)
            {
                barkService.sendMessage(vendorList, userPhone, title, message);
            }

            if (vendorName == ProviderVendor.NTFY)
            {
                ntfyService.sendMessage(vendorList, userPhone, title, message);
            }
        }

        return "OK";
    }
}
