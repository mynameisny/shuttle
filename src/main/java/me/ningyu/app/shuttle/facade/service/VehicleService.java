package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Driver;
import me.ningyu.app.shuttle.domain.entity.extension.Vehicle;
import me.ningyu.app.shuttle.domain.repo.DriverRepository;
import me.ningyu.app.shuttle.domain.repo.VehicleRepository;
import me.ningyu.app.shuttle.model.fleet.VehicleUpsertRequest;
import me.ningyu.app.shuttle.model.fleet.VehicleView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService
{
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    @Transactional(readOnly = true)
    public List<VehicleView> getAll()
    {
        return vehicleRepository.findAll().stream()
                .map(VehicleView::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehicleView create(VehicleUpsertRequest req)
    {
        if (vehicleRepository.existsByPlateNumber(req.getPlateNumber()))
        {
            throw new IllegalArgumentException("车牌号已存在: " + req.getPlateNumber());
        }
        Vehicle v = Vehicle.builder()
                .plateNumber(req.getPlateNumber())
                .seatCount(req.getSeatCount())
                .vehicleType(req.getVehicleType())
                .supplier(req.getSupplier())
                .adminName(req.getAdminName())
                .adminPhone(req.getAdminPhone())
                .driver(resolveDriver(req.getDriverId()))
                .build();
        return VehicleView.of(vehicleRepository.save(v));
    }

    @Transactional
    public VehicleView update(Long id, VehicleUpsertRequest req)
    {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("载具不存在: id=" + id));
        v.setPlateNumber(req.getPlateNumber());
        v.setSeatCount(req.getSeatCount());
        v.setVehicleType(req.getVehicleType());
        v.setSupplier(req.getSupplier());
        v.setAdminName(req.getAdminName());
        v.setAdminPhone(req.getAdminPhone());
        v.setDriver(resolveDriver(req.getDriverId()));
        return VehicleView.of(v);
    }

    @Transactional
    public void delete(Long id)
    {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("载具不存在: id=" + id));
        vehicleRepository.delete(v);
    }

    private Driver resolveDriver(Long driverId)
    {
        if (driverId == null) return null;
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("司机不存在: id=" + driverId));
    }
}
