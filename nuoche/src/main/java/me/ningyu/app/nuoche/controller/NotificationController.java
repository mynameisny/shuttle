package me.ningyu.app.nuoche.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.nuoche.common.dto.NotificationDTO;
import me.ningyu.app.nuoche.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController
{
    private final NotificationService notificationService;


    @PostMapping
    public ResponseEntity<?> send(@RequestBody @Validated NotificationDTO dto)
    {
        String result = notificationService.send(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<?> sendByGET(NotificationDTO dto)
    {
        String result = notificationService.send(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/mute")
    public ResponseEntity<Void> mute(@RequestParam String userId, @RequestParam String providerId)
    {
        notificationService.mute(userId, providerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generateQRCode")
    public String generateQRCode()
    {
        return notificationService.generateQRCode();
    }

}
