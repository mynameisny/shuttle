package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Driver;
import me.ningyu.app.shuttle.domain.repo.DriverRepository;
import me.ningyu.app.shuttle.domain.repo.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService
{
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

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
                .build());
    }

    @Transactional
    public Driver update(Long id, Driver req)
    {
        Driver d = driverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("司机不存在: id=" + id));
        d.setName(req.getName());
        d.setPhone(req.getPhone());
        return driverRepository.save(d);
    }

    @Transactional
    public void delete(Long id)
    {
        if (vehicleRepository.existsByDriverId(id))
        {
            throw new IllegalStateException("该司机仍关联车辆，请先解除关联再删除");
        }
        driverRepository.deleteById(id);
    }
}
