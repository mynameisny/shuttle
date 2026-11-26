package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Captain;
import me.ningyu.app.shuttle.domain.repo.CaptainRepository;
import me.ningyu.app.shuttle.event.api.DomainEventPublisher;
import me.ningyu.app.shuttle.event.change.FieldChange;
import me.ningyu.app.shuttle.event.change.FieldChangeDetector;
import me.ningyu.app.shuttle.event.domain.CaptainUpdatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CaptainService
{
    private final CaptainRepository captainRepository;
    private final DomainEventPublisher eventPublisher;
    private final FieldChangeDetector fieldChangeDetector;

    @Transactional(readOnly = true)
    public List<Captain> getAll()
    {
        return captainRepository.findAll();
    }

    @Transactional
    public Captain create(Captain req)
    {
        return captainRepository.save(Captain.builder()
                .name(req.getName())
                .phone(req.getPhone())
                .gender(req.getGender())
                .idCardNumber(req.getIdCardNumber())
                .avatarUrl(req.getAvatarUrl())
                .build());
    }

    @Transactional
    public Captain update(Long id, Captain req)
    {
        Captain c = captainRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "车长不存在: id=" + id));
        List<FieldChange> changes = fieldChangeDetector.diff("CAPTAIN", c, req);
        c.setName(req.getName());
        c.setPhone(req.getPhone());
        c.setGender(req.getGender());
        c.setIdCardNumber(req.getIdCardNumber());
        c.setAvatarUrl(req.getAvatarUrl());
        Captain saved = captainRepository.save(c);
        if (!changes.isEmpty())
        {
            eventPublisher.publish(new CaptainUpdatedEvent(new CaptainUpdatedEvent.CaptainUpdatedData(
                    saved.getId(), saved.getName(), changes)));
        }
        return saved;
    }

    @Transactional
    public void delete(Long id)
    {
        captainRepository.deleteById(id);
    }
}
