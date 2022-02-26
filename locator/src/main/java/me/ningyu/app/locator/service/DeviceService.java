package me.ningyu.app.locator.service;

import com.querydsl.core.types.Predicate;
import lombok.extern.slf4j.Slf4j;
import me.ningyu.app.locator.common.exception.NotfoundException;
import me.ningyu.app.locator.common.vo.DeviceDto;
import me.ningyu.app.locator.domain.device.entity.Device;
import me.ningyu.app.locator.domain.device.repository.DeviceRepository;
import me.ningyu.app.locator.domain.map.entity.Station;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Service
@Slf4j
public class DeviceService
{
    private DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository)
    {
        this.deviceRepository = deviceRepository;
    }

    public Device add(DeviceDto dto)
    {
        Device entity = new Device();
        BeanUtils.copyProperties(dto, entity);
        return deviceRepository.save(entity);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable String id)
    {
        Optional.of(deviceRepository.findById(id)).get().orElseThrow(() -> new NotfoundException(String.format("设备%s不存在", id)));
        deviceRepository.deleteById(id);
    }

    public Device update(String id, DeviceDto dto)
    {
        Device device = Optional.of(deviceRepository.findById(id)).get().orElseThrow(() -> new RuntimeException(String.format("设备%s不存在", id)));
        BeanUtils.copyProperties(device, dto);
        return deviceRepository.save(device);
    }

    public DeviceDto get(String id)
    {
        Device device = Optional.of(deviceRepository.findById(id)).get().orElseThrow(() -> new RuntimeException(String.format("站点%s不存在", id)));
        DeviceDto dto = new DeviceDto();
        BeanUtils.copyProperties(device, dto);
        return dto;
    }

    public Page<Device> list(Predicate predicate, Pageable pageable)
    {
        return deviceRepository.findAll(predicate, pageable);
    }
}
