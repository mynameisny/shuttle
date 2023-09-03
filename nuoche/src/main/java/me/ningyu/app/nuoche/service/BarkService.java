package me.ningyu.app.nuoche.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.nuoche.common.dto.MessageDTO;
import me.ningyu.app.nuoche.domain.Provider;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BarkService implements Notification
{
    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static final String DEFAULT_GROUP = "挪车通知";


    @Override
    public void send(MessageDTO message)
    {

    }

    public boolean send(String url, String pushKey, String title, String content, String groupName, String link)
    {
        String api = url + "/" + pushKey;

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(api);
        URI uri = uriComponentsBuilder.build().encode(StandardCharsets.UTF_8).toUri();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        BarkBody body = BarkBody.builder().title(title).body(content).group(groupName).isArchive(1).build();
        if (StringUtils.isNotBlank(link))
        {
            body.setUrl(link);
        }

        RequestEntity<BarkBody> requestEntity = RequestEntity.post(uri).headers(httpHeaders).body(body);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(uri, requestEntity, String.class);
        if (responseEntity.getStatusCodeValue() == HttpStatus.OK.value())
        {
            String responseBody = responseEntity.getBody();

            try
            {
                log.info("访问API：{}，参数：{}，响应：{}", api, objectMapper.writeValueAsString(body), responseBody);
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                if ("success".equalsIgnoreCase(jsonNode.get("message").asText()))
                {
                    return true;
                }
            }
            catch (JsonProcessingException e)
            {
                log.error("解析HTTP响应体时出现异常", e);
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    public void sendMessage(List<Provider> vendorList, String title,  String content, String userPhone)
    {
        for (Provider vendor : vendorList)
        {
            String link = null;
            if (StringUtils.isNotBlank(userPhone))
            {
                link = "tel://" + userPhone;
            }

            send(vendor.getUrl(), vendor.getPushKey(), title, content, DEFAULT_GROUP, link);
        }
    }

    @Builder
    @Getter
    @Setter
    @ToString
    static class BarkBody
    {
        private String title;

        private String body;

        private String level = "active";

        private int badge = 1;

        private int autoCopy = 1;

        private String copy;

        private String sound;

        private String icon;

        private String group;

        private int isArchive = 1;

        private String url;
    }
}
