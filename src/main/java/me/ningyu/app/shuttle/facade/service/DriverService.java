package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Driver;
import me.ningyu.app.shuttle.domain.repo.DriverRepository;
import me.ningyu.app.shuttle.event.api.DomainEventPublisher;
import me.ningyu.app.shuttle.event.domain.DriverStatusChangedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService
{
    private final DriverRepository driverRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<Driver> getAll()
    {
        return driverRepository.findAll();
    }

    @Transactional
    public Driver create(Driver req)
    {
        return driverRepository.save(Driver.builder()
                .name(req.getName())
                .phone(req.getPhone())
                .status(req.getStatus())
                .idCardNumber(req.getIdCardNumber())
                .gender(req.getGender())
                .licenseClass(req.getLicenseClass())
                .licenseNumber(req.getLicenseNumber())
                .avatarUrl(req.getAvatarUrl())
                .company(req.getCompany())
                .build());
    }

    @Transactional
    public Driver update(Long id, Driver req)
    {
        Driver d = driverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("司机不存在: id=" + id));
        String oldStatus = d.getStatus();
        d.setName(req.getName());
        d.setPhone(req.getPhone());
        d.setStatus(req.getStatus());
        d.setIdCardNumber(req.getIdCardNumber());
        d.setGender(req.getGender());
        d.setLicenseClass(req.getLicenseClass());
        d.setLicenseNumber(req.getLicenseNumber());
        d.setAvatarUrl(req.getAvatarUrl());
        d.setCompany(req.getCompany());
        Driver saved = driverRepository.save(d);
        // 状态发生变化时发布领域事件（数据快照不含手机号等敏感信息）
        if (!java.util.Objects.equals(oldStatus, saved.getStatus()))
        {
            eventPublisher.publish(new DriverStatusChangedEvent(new DriverStatusChangedEvent.DriverStatusChangedData(
                    saved.getId(), saved.getName(), oldStatus, saved.getStatus())));
        }
        return saved;
    }

    @Transactional
    public void delete(Long id)
    {
        driverRepository.deleteById(id);
    }
}
