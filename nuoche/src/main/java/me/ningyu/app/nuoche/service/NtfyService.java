package me.ningyu.app.nuoche.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.nuoche.common.dto.MessageDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class NtfyService implements Notification
{
    private final RestTemplate restTemplate;

    public static final String BASE_URL = "https://ntfy.sh/";
    public static final String TOPIC_NAME = "tony_nuo_che";

    @Override
    public void send(MessageDTO dto)
    {

        //UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(BASE_URL + TOPIC_NAME);
        //URI uri = uriComponentsBuilder.build().encode(StandardCharsets.UTF_8).toUri();
        //
        //HttpHeaders httpHeaders = new HttpHeaders();
        //httpHeaders.add("X-Priority", dto.getPriority().getCode());
        //httpHeaders.add("X-Tags", String.join(",", dto.getTags()));
        //httpHeaders.add("Click", "tel://13910841083");
        //
        //RequestEntity<String> requestEntity = RequestEntity.post(uri).headers(httpHeaders).body(dto.getContent());


        //ResponseEntity<String> exchange = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, String.class);
    }
}
