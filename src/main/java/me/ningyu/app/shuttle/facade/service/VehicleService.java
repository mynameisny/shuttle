package me.ningyu.app.shuttle.facade.service;

import lombok.RequiredArgsConstructor;
import me.ningyu.app.shuttle.domain.entity.extension.Vehicle;
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
        Vehicle v = apply(new Vehicle(), req);
        return VehicleView.of(vehicleRepository.save(v));
    }

    @Transactional
    public VehicleView update(Long id, VehicleUpsertRequest req)
    {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("载具不存在: id=" + id));
        apply(v, req);
        return VehicleView.of(v);
    }

    @Transactional
    public void delete(Long id)
    {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("载具不存在: id=" + id));
        vehicleRepository.delete(v);
    }

    private Vehicle apply(Vehicle v, VehicleUpsertRequest req)
    {
        v.setPlateNumber(req.getPlateNumber());
        v.setVin(req.getVin());
        v.setBrand(req.getBrand());
        v.setSeries(req.getSeries());
        v.setModel(req.getModel());
        v.setEngineNumber(req.getEngineNumber());
        v.setSeatCount(req.getSeatCount());
        v.setVehicleType(req.getVehicleType());
        v.setEnergyType(req.getEnergyType());
        v.setStatus(req.getStatus());
        v.setSupplier(req.getSupplier());
        v.setInsuranceExpiry(req.getInsuranceExpiry());
        v.setInspectionExpiry(req.getInspectionExpiry());
        return v;
    }
}
