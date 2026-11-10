package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Driver;
import me.ningyu.app.shuttle.domain.repo.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService
{
    private final DriverRepository driverRepository;

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
                .build());
    }

    @Transactional
    public Driver update(Long id, Driver req)
    {
        Driver d = driverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("司机不存在: id=" + id));
        d.setName(req.getName());
        d.setPhone(req.getPhone());
        d.setStatus(req.getStatus());
        d.setIdCardNumber(req.getIdCardNumber());
        d.setGender(req.getGender());
        d.setLicenseClass(req.getLicenseClass());
        d.setLicenseNumber(req.getLicenseNumber());
        return driverRepository.save(d);
    }

    @Transactional
    public void delete(Long id)
    {
        driverRepository.deleteById(id);
    }
}
