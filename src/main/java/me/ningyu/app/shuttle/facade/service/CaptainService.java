package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Captain;
import me.ningyu.app.shuttle.domain.repo.CaptainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CaptainService
{
    private final CaptainRepository captainRepository;

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
                .build());
    }

    @Transactional
    public Captain update(Long id, Captain req)
    {
        Captain c = captainRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("车长不存在: id=" + id));
        c.setName(req.getName());
        c.setPhone(req.getPhone());
        c.setGender(req.getGender());
        return captainRepository.save(c);
    }

    @Transactional
    public void delete(Long id)
    {
        captainRepository.deleteById(id);
    }
}
